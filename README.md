# Web Analytics Capture

This is a simple html form with a Spring Boot RESTful endpoint. This simulates an web analytics request capture. The example hosted on heroku at https://warm-basin-39291.herokuapp.com/.

The service uses KafkaTemplate, initialized via Kafka properties in producer.properties. It publishes data/events to a topic called WEB_ANALYTICS_IN.
If you are using a local Kafka setup, update the bootstrap.servers to your local and remove the jaas config. 

Please see another application (https://github.com/pkamath2/web-analytics) on details on how Kafka is used for stream processing. 


Some scripts to create the topics locally and Confluent Cloud (More details on how to use Confluent Cloud - https://docs.confluent.io/current/quickstart/cloud-quickstart.html)
## Local
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic WEB_ANALYTICS_IN

bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic WEB_ANALYTICS_OUT

bin/kafka-topics.sh --list --zookeeper localhost:2181

## Confluent Cloud on GCP

ccloud topic create WEB_ANALYTICS_IN
ccloud topic create WEB_ANALYTICS_OUT
ccloud topic create WEB_ANALYTICS_REPLAY

ccloud topic list

ccloud topic describe WEB_ANALYTICS_IN
ccloud topic describe WEB_ANALYTICS_OUT
ccloud topic describe WEB_ANALYTICS_REPLAY

ccloud topic alter WEB_ANALYTICS_IN --config="retention.ms=604800000" //1 week retention
ccloud topic alter WEB_ANALYTICS_REPLAY --config="retention.ms=604800000" //1 week retention

