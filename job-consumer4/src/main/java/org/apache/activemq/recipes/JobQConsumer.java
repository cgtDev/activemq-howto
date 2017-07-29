package org.apache.activemq.recipes;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JobQConsumer {

    private final String connectionUri = "tcp://localhost:61616?jms.prefetchPolicy.queuePrefetch=1";
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;

    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //这里的这个队列要和product发送消息的队列一致
        destination = session.createQueue("JOBQ.Work");
    }

    public void after() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void run() throws Exception {
        MessageConsumer consumer = session.createConsumer(destination);
        //设置消费监听，product
        consumer.setMessageListener(new JobQListener());
        //５分钟后仍然没有消息发过来，就终consumer止程序运行
        TimeUnit.MINUTES.sleep(2);
        System.out.println("１分钟后停止");
        //暂停链接
        connection.stop();
        consumer.close();
    }

    public static void main(String[] args) {
        JobQConsumer producer = new JobQConsumer();
        System.out.print("\n\n\n");
        System.out.println("Starting example Consumer now...");
        try {
            producer.before();
            producer.run();
            producer.after();
        } catch (Exception e) {
            System.out.println("Caught an exception during the example: " + e.getMessage());
        }
        System.out.println("Finished running the sample Consumer app.");
        System.out.print("\n\n\n");
    }

}