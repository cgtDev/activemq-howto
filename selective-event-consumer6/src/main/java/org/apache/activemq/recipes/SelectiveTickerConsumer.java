package org.apache.activemq.recipes;

import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SelectiveTickerConsumer {

    private final String connectionUri = "tcp://localhost:61616";
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;
    private String selector;

    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        destination = session.createTopic("EVENTS.stock");
        //获取指定的topic
        selector = System.getProperty("QuoteSel", "symbol = 'GOOG'");
        //System.out.println("QuoteSel====");
    }

    public void after() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void run() throws Exception {
        System.out.println(" Running example with selector: " + selector);
        //定义一个带过滤指定topic的消息消费者
        MessageConsumer consumer = session.createConsumer(destination, selector);
        //设置消息监听
        consumer.setMessageListener(new EventListener());
        //闲置２分钟后结束该线程
        TimeUnit.MINUTES.sleep(2);
        connection.stop();
        consumer.close();
    }

    public static void main(String[] args) {
        SelectiveTickerConsumer producer = new SelectiveTickerConsumer();
        System.out.print("\n\n\n");
        System.out.println("Starting example Selective Stock Ticker Consumer now...");
        try {
            producer.before();
            producer.run();
            producer.after();
        } catch (Exception e) {
            System.out.println("Caught an exception during the example: " + e.getMessage());
        }
        System.out.println("Finished running the sample Selective Stock Ticker Consumer app.");
        System.out.print("\n\n\n");
    }

}