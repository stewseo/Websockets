package org.game.client;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class SocketClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
        public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
            //get the localhost IP address
            InetAddress host = InetAddress.getLocalHost();
            Socket socket = null;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;

            // api endpoints that will be sent to server
            List<String> apiEndpoints = List.of("/_ingest/pipeline/filebeat-8.4.0-elasticsearch-audit-pipeline", // ingest_pipeline configuration for filebeat module: elasticsearch-audit-pipeline
                    "/_cat/templates?v=true&s=order:desc,index_patterns", // all index patterns
                    "/_token"); // oauth2 tokens: access and refresh

            for (String apiEndpoint : apiEndpoints) {
                //establish socket connection to server
                socket = new Socket(host.getHostName(), 9876);
                //write to socket using ObjectOutputStream
                oos = new ObjectOutputStream(socket.getOutputStream());
                logger.info("Sending request to Socket Server");

                oos.writeObject(apiEndpoint);

                //read the server response message
                ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message: " + message);
                //close resources
                ois.close();
                oos.close();
                Thread.sleep(100);
            }

        }
    }

