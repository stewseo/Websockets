- Create SpringBoot and Java plain-text and json logs. 
- Monitor and collect files located at path: $PROJECT_HOME/spring-websocket/logs/* 
- Ship data to aws elasticsearch cluster
- Load index templates
- Load dashboards
- Ingest pipelines
- Create visualizations

Discover dashboard filter: spring-boot-websocket-ecs.json

viewing field statistics: log.logger and process.thread.name

![field_statistics](https://user-images.githubusercontent.com/54422342/189035703-f0e67e70-4a84-48e5-80f8-fdb2ad88982d.jpg)



Creating a new visualization panel with fields: log.level and log.logger

![spring-websocket_all-fields_ES-Discover](https://user-images.githubusercontent.com/54422342/189035827-0b504c42-0baf-4ad8-a573-f455b3036c40.jpg)
