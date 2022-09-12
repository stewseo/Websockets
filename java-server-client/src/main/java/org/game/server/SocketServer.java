package org.game.server;

import org.game.myDeployment.AbstractApiImpl;
import org.game.myDeployment.stackManagement.data.indexManagement.IndexImpl;
import org.game.myDeployment.stackManagement.ingest.IngestPipelineImpl;
import org.game.myDeployment.stackManagement.security.AuthTokenImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SocketServer {
    private static ServerSocket server;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private static int port = 9876;

    public static void main(String[] args) throws Exception {

        server = new ServerSocket(port);
        while(true){
            Socket socket = server.accept();
            logger.info("logging Waiting ServerSocket.accept()");

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            String message = ois.readObject().toString();

            if(message != null) {
                sendRequest(message);
                logger.info("logging Message: " + message);
            }
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            oos.writeObject("Write ObjectOutputStream to Socket " + message);
            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if(message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();
    }

    // send messages from client to api handelers
    private static void sendRequest(String endpoint) throws Exception {
        List<AbstractApiImpl> apiHandlers = List.of(new AuthTokenImpl(), new IndexImpl(), new IngestPipelineImpl());

        if (endpoint.contains("_token")) {
            if (apiHandlers.get(0) instanceof AuthTokenImpl authTokenHandler) {
                System.out.printf("contains _token %s%n get accessToken %s%n get refreshToken %s%n",
                        authTokenHandler.create("elastic", System.getenv("ES_CLOUD_PASS")),
                        authTokenHandler.getAccessToken(),
                        authTokenHandler.getRefreshToken()
                        );
            }
        }

        else if (endpoint.contains("index_patterns")) {

            if (apiHandlers.get(1) instanceof IndexImpl requestHandler) {
                System.out.printf("contains index_patterns %s%n" +
                                "getListOfAllIndexTemplates %s%n",
                        requestHandler.getIndexTemplate(endpoint),
                        requestHandler.getIndexTemplate("")); // TODO: parse fields with jsonReader. replace characters that cause invalid syntax exception
            }
        }

        else if (endpoint.contains("_ingest")) {
            if (apiHandlers.get(2) instanceof IngestPipelineImpl ingestPipelineRequestHandler) {
                System.out.printf("contains _ingest %s",
                        ingestPipelineRequestHandler.get(endpoint)
                        );
            }
        }

    }
}
