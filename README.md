#### Ship data to Elasticsearch cluster
- filestream inputs: 
    - $PROJECT_HOME/spring-boot-websocketHandler/logs/*
    - $PROJECT_HOME/server-client/logs/*

#### submit indexing and search requests directly to the resource that stores append-only time series data across multiple indices.

GetDataStreamRequest
- api path: /_data_stream 
- data-stream-id: filebeat-8.4.0
- query paramter that to match: "@timestamp" : "2022-09-08T03:17:*" 

SearchRequest
- api path: /_data_stream 
- data-stream-id: filebeat-8.4.0
- query paramter that to match: "@timestamp" : "2022-09-08T03:17:*" 

#### submit a get request to the The hidden, auto-generated backing indices that a data-stream consists of.

GetRequest
- index id: .ds-filebeat-8.4.0-2022.09.08-000001


#### Create visualizations
- Discover: spring-boot-websocket-ecs.json
- viewing field statistics: log.logger and process.thread.name

![field_statistics](https://user-images.githubusercontent.com/54422342/189035703-f0e67e70-4a84-48e5-80f8-fdb2ad88982d.jpg)

- Creating a new visualization panel

![creating_spring-websocket_visualization](https://user-images.githubusercontent.com/54422342/189037901-58ad6c3d-9060-4c21-bea5-3e85e1f50585.jpg)


