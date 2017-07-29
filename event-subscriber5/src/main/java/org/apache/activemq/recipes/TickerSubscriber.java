package org.apache.activemq.recipes;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class TickerSubscriber {

    private final String connectionUri = "tcp://localhost:61616";
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;

    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        connection = connectionFactory.createConnection();
        //启动connction
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建topic为EVENTS.QUOTES的消息的session,要和publish一致
        destination = session.createTopic("EVENTS.stock");
    }

    public void after() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void run() throws Exception {
    	//实例化consumer----EVENTS.QUOTES的消息发送地
        MessageConsumer consumer = session.createConsumer(destination);
        //设置监听
        consumer.setMessageListener(new EventListener());
        //２分钟后，结束该应用
        TimeUnit.MINUTES.sleep(2);
        connection.stop();
        consumer.close();
    }

    public static void main(String[] args) {
        TickerSubscriber producer = new TickerSubscriber();
        System.out.print("\n\n\n");
        System.out.println("Starting example Stock Ticker Subscriber now...");
        try {
            producer.before();
            producer.run();
            producer.after();
        } catch (Exception e) {
            System.out.println("Caught an exception during the example: " + e.getMessage());
        }
        System.out.println("Finished running the sample Stock Ticker Subscriber app.");
        System.out.print("\n\n\n");
    }

}