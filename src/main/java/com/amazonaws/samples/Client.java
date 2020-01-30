package com.amazonaws.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.*;
import java.text.*;   // for date and time
import java.net.*;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class Client implements Runnable  {
	
    final BufferedReader in;
    final PrintWriter out;
    
    private String year;
	private String make;
	private String model;
	private String name;
	private String insta;
	private String throttle;
	private String injectors;
	private String pulley;
	private String headers; 
	private String frontpipe;
	private String catback;
	private String diameter;
    //some more

    Socket s;
    
    

    // constructor
    public Client(Socket s, BufferedReader in, PrintWriter out) throws InterruptedException {
        this.in = in;
        this.out = out;
        this.s = s;
	    
    }

    @Override
    public void run() {
        String received="";
        Random rand = new Random();
        while (true)  {
            try {
                received = this.in.readLine();
                System.out.println("Recieved:" + received);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (received == null){
                break;
            }

            
            // if recieved from add
            else if(received.startsWith("<")){
            	
            	year = received.substring(received.indexOf("<")+1,received.indexOf("!"));
            	make = received.substring(received.indexOf("!")+1,received.indexOf("?"));
            	model = received.substring(received.indexOf("?")+1,received.indexOf("#"));
            	name = received.substring(received.indexOf("#")+1,received.indexOf("$"));
            	insta = received.substring(received.indexOf("$")+1,received.indexOf("%"));
            	throttle = received.substring(received.indexOf("%")+1,received.indexOf("^"));
            	injectors = received.substring(received.indexOf("^")+1,received.indexOf("&"));
            	pulley = received.substring(received.indexOf("&")+1,received.indexOf("*"));
            	headers = received.substring(received.indexOf("*")+1,received.indexOf("("));
            	frontpipe = received.substring(received.indexOf("(")+1,received.indexOf(")"));
            	catback = received.substring(received.indexOf(")")+1,received.indexOf("+"));
            	diameter = received.substring(received.indexOf("+")+1,received.indexOf(">"));
            	
            	System.out.println(year);
            	System.out.println(make);
            	System.out.println(model);
            	System.out.println(name);
            	System.out.println(insta);
            	System.out.println(throttle);
            	System.out.println(injectors);
            	System.out.println(pulley);
            	System.out.println(headers);
            	System.out.println(frontpipe);
            	System.out.println(catback);
            	System.out.println(diameter);
            	
            	CarServerProgram.addToTable("Cars", year, make, model, name, insta, throttle, injectors, pulley, headers, frontpipe, catback, diameter);

                sendMessageToMobile("#1");
                
                CarProfile temp = new CarProfile(year, make, model, name, insta, throttle, injectors, pulley, headers, frontpipe, catback, diameter);
                CarServerProgram.profiles.add(temp);
                CarServerProgram.addToNetwork(temp);

            }
            //if received is an attempted answer
            else if(received.startsWith("?")){ // do your own works
            	year = received.substring(received.indexOf("?")+1,received.indexOf("!"));
            	make = received.substring(received.indexOf("!")+1,received.indexOf("&"));
            	model = received.substring(received.indexOf("&")+1,received.indexOf("~"));
                
            	ArrayList<Integer> resultSet = CarServerProgram.scanNetwork(year, make, model);
            	sendMessageToMobile("[");
            	for(int i=0; i<resultSet.size(); i++) {
            		System.out.println("Sending profile");
            		sendMessageToMobile(CarServerProgram.profiles.get(resultSet.get(i)).getAllInfo());
            	}
            	System.out.println("end sending");
            	sendMessageToMobile("]");
            	
            }
            //sending year makes and model values
            else if(received.equals("current values")) {
            	
            	for(String year : CarServerProgram.year.keySet()) {
            		for(String make : CarServerProgram.year.get(year).keySet()) {
            			for(String model : CarServerProgram.year.get(year).get(make).keySet()) {
            				System.out.println("Sending combo" +"?"+ year + "!" + make + "&" + model + "~");
                    		sendMessageToMobile("?"+ year + "!" + make + "&" + model + "~");
            			}
            		}
            	}
            	System.out.println("done sending");
            	sendMessageToMobile("}");
            }
        }
    }

    public void sendMessageToMobile(final String str) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "utf-8"), true);

                    if (!str.isEmpty()) {
                        out.println(str);
                        out.flush();
                    }
                } catch (IOException e) {
                }
            }
            }).start();
    }

}








