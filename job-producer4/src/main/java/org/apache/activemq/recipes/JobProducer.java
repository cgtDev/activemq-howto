package org.apache.activemq.recipes;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JobProducer {

    private final String connectionUri = "tcp://localhost:61616";
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;

    public void before() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建JOBQ.WORK
        destination = session.createQueue("JOBQ.Work");
    }

    public void after() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void run() throws Exception {
        MessageProducer producer = session.createProducer(destination);
        //连续生产1000条文本消息塞到JOBQ.Work队列
        for (int i = 0; i < 10; ++i) {
            TextMessage message = session.createTextMessage("Job number: " + i);
            message.setIntProperty("JobID", i);
            producer.send(message);
            System.out.println("Producer sent Job("+i+")");
        }
        //发送完毕，关闭资源
        producer.close();
    }

    public static void main(String[] args) {
        JobProducer producer = new JobProducer();
        System.out.print("\n\n\n");
        System.out.println("Starting example Job Q Producer now...");
        try {
            producer.before();
            producer.run();
            producer.after();
        } catch (Exception e) {
            System.out.println("Caught an exception during the example: " + e.getMessage());
        }
        System.out.println("Finished running the sample Job Q Producer app.");
        System.out.print("\n\n\n");
    }
}