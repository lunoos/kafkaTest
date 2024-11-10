# Kafka Guide
## Table of Contents
- Introduction to Kafka
- Kafka Setup Using Docker
- Core Kafka Concepts
- - Partitions
- - Consumers and Consumer Groups
- - Leaders, Replicas, and In-Sync Replicas (ISR)
- Kafka with Java Spring Boot
- - Producer and Consumer Example
- - Handling Multiple Partitions and Consumer Groups
- Troubleshooting and Best Practices
- References and Quick Commands

## 1. Introduction to Kafka
Apache Kafka is a distributed event-streaming platform known for its scalability, fault tolerance, and high throughput. It’s widely used for building real-time data pipelines and streaming applications.

Key Features:
Reliability: Distributed and fault-tolerant.
Scalability: Supports large-scale, parallel processing with partitions.
Performance: High throughput and low latency.
## 2. Kafka Setup Using Docker
Setting up Kafka with Docker simplifies deployment. In this setup, we use both Kafka and Zookeeper (as Kafka requires Zookeeper to manage brokers).

Step 1: Create a Docker Compose File
Create a docker-compose.yml file for easy setup and configuration:

yaml
Copy code
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    ports:
      - "9092:9092"
Step 2: Start Kafka and Zookeeper Containers
Run the following command to start the containers:

bash
Copy code
docker-compose up -d
Step 3: Verify the Setup
Check if Kafka is running by listing the topics:

bash
Copy code
docker exec -it <kafka_container_id> kafka-topics --list --bootstrap-server localhost:9092
## 3. Core Kafka Concepts
Partitions
A partition is a unit of parallelism in Kafka, enabling data to be split and processed in parallel by multiple consumers. Topics can be divided into multiple partitions.

Consumers and Consumer Groups
Consumer: Consumes messages from a Kafka topic.
Consumer Group: Multiple consumers in a group can consume messages from different partitions of the same topic. Kafka ensures each partition is read by only one consumer in a group for load distribution.
Leaders, Replicas, and ISR
Leader: Each partition has one broker acting as the leader responsible for handling all reads and writes.
Replicas: Copies of the partition spread across brokers for redundancy.
ISR (In-Sync Replicas): The set of replicas that are up-to-date with the leader.
## 4. Kafka with Java Spring Boot
Producer and Consumer Example
Producer Setup:

Dependencies:

xml
Copy code
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
Producer Configuration:

java
Copy code
@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
Send Message:

java
Copy code
@Service
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
Consumer Setup:

Consumer Configuration:

java
Copy code
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
Listener:

java
Copy code
@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "my-topic", groupId = "my-consumer-group")
    public void listen(String message) {
        System.out.println("Received Message: " + message);
    }
}
Handling Multiple Partitions and Consumers
Creating a Topic with Partitions:

bash
Copy code
docker exec -it <kafka_container_id> kafka-topics --create --topic my-topic --partitions 3 --replication-factor 1 --bootstrap-server localhost:9092
Consumer Group Balancing: When multiple instances of the consumer (e.g., by running multiple Spring Boot application instances) are started, Kafka will balance partitions across the consumers.

## 5. Troubleshooting and Best Practices
Increasing Partitions: If needed, you can add more partitions to an existing topic:

bash
Copy code
docker exec -it <kafka_container_id> kafka-topics --alter --topic my-topic --partitions 5 --bootstrap-server localhost:9092
Monitoring: Monitor Kafka logs and consumer lag to understand performance and potential bottlenecks.

Avoid High Replication Factor on Single Node: In a single-node setup, keep replication-factor as 1 to avoid errors.

## 6. References and Quick Commands
Useful Kafka CLI Commands
List Topics:

bash
Copy code
docker exec -it <kafka_container_id> kafka-topics --list --bootstrap-server localhost:9092
Describe Topic:

bash
Copy code
docker exec -it <kafka_container_id> kafka-topics --describe --topic my-topic --bootstrap-server localhost:9092
Delete Topic:

bash
Copy code
docker exec -it <kafka_container_id> kafka-topics --delete --topic my-topic --bootstrap-server localhost:9092
This document provides a comprehensive guide to setting up and working with Kafka in a Java Spring Boot environment using Docker.