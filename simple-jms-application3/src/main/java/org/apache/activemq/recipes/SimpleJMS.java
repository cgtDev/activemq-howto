package org.apache.activemq.recipes;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SimpleJMS {

    private final String connectionUri = "tcp://localhost:61616";
    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Destination destination;

    public void before() throws Exception {
    	//１创建connectFactory，链接到activemq的broker
        connectionFactory = new ActiveMQConnectionFactory(connectionUri);
        //使用connectionFactory创建connection
        connection = connectionFactory.createConnection();
        //启动　connection
        connection.start();
        //启动的connection创建session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //使用session创建名为MyQueue的队列
        destination = session.createQueue("MyQueue");
    }

    public void after() throws Exception {
    	//当消费者、生产者执行完消息操作后，关闭connetionFatory创建的connection
        if (connection != null) {
            connection.close();
        }
    }

    public void run() throws Exception {
       //创建消息生产者，产生的消息塞到MyQueue的队列
        MessageProducer producer = session.createProducer(destination);
        try {
        	//创建一个文本消息对象
            TextMessage message = session.createTextMessage();
            //给文本消息对象塞入一条文本消息
            message.setText("We sent a Message!");
            //生产者把这条文本消息塞入MyQueue的队列的
            producer.send(message);
        } finally {
        	//发送完文本消息后，关闭资源
            producer.close();
        }
       //创建消息消费者,从MyQueue的队列拿消息
        MessageConsumer consumer = session.createConsumer(destination);
        try {
        	//消费者从MyQueue的队列接收producer产生的消息
            TextMessage message = (TextMessage) consumer.receive();
            //验证接收到的消息内容
            System.out.println(message.getText());
        } finally {
        	//关闭资源
            consumer.close();
        }
    }

    public static void main(String[] args) {
        SimpleJMS example = new SimpleJMS();
        System.out.print("\n\n\n");
        System.out.println("Starting SimpleJMS example now...");
        try {
            example.before();
            example.run();
            example.after();
        } catch (Exception e) {
            System.out.println("Caught an exception during the example: " + e.getMessage());
        }
        System.out.println("Finished running the SimpleJMS example.");
        System.out.print("\n\n\n");
    }
}