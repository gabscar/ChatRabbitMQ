package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.*;
import java.text.*;


public class Chat {
    private static  String getData() { 
	    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	Date data = new Date(); 
    	return df.format(data); 
    }
     private static  String getHora() { 
    	DateFormat df = new SimpleDateFormat("HH:mm");
    	Date hora = new Date(); 
    	return df.format(hora); 
    }
    public static String QUEUE_NAME, aux;
  public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        Scanner scan= new Scanner(System.in);
        factory.setHost("172.31.90.43"); // Alterar
        factory.setUsername("administrador"); // Alterar
        factory.setPassword("password"); // Alterar
        factory.setVirtualHost("/");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Channel channelaux = connection.createChannel();
        String sair="exit";
        String seunome;
        System.out.print("User :");
        seunome=scan.nextLine();
        channel.queueDeclare("@"+seunome, false,   false,     false,       null);
            Consumer consumer = new DefaultConsumer(channel) {
              public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(message);
                if(QUEUE_NAME==null){
                    System.out.print(">>");
                }else{
                    System.out.print(QUEUE_NAME+">>");}
    
          }
        };
      
 
        while(true){
            channel.basicConsume("@"+seunome,true,consumer);
            System.out.print(">>");
            QUEUE_NAME = scan.nextLine();
           
            if("@".equals(QUEUE_NAME.substring(0, 1))){
                  String message1="a";
                  while(true){
                         channel.basicConsume("@"+seunome,true,consumer);
                         System.out.print(QUEUE_NAME+">>"); 
                         message1 = scan.nextLine();
                          if(message1.equals("exit")){
                             break;
                          }
                          String data=getData();
                          String hora=getHora();
                          aux=("(" +data +" as "+ hora +") "+  seunome + " diz:" + message1);
                          
                         channelaux.basicPublish("",QUEUE_NAME, null, aux.getBytes("UTF-8"));
                         if(QUEUE_NAME.length()>1 && "@".equals(message1.substring(0, 1))){
                            QUEUE_NAME=message1;
                            break;  
                         }
                  }
              }else{
                System.out.println("formato incorreto");
              }
       
        }

    
  }
}