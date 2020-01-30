package com.amazonaws.samples;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
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

public class CarServerProgram {

	public static HashMap<String,HashMap<String,HashMap< String,ArrayList<Integer>>>> year;
	public static ArrayList<CarProfile> profiles;
	
	
    public static BufferedReader in;
    public static PrintWriter out;
    static AmazonDynamoDB dynamoDB;
    HashMap<String, Client> client = new HashMap<>();   // eg: <id, socket info>

    public static void main(String[] args) throws Exception {

        ServerSocket ss = new ServerSocket(31615);
        Socket s;
        
        profiles = new ArrayList<>();
        year = new HashMap<>();
        
      //connection to dynamodb
        try {
        	System.out.println("trying init");
			init();
		} catch (Exception e) {
			System.out.println("not connected");
			e.printStackTrace();
		}
	    
    	makeCars();
	    
	    System.out.println("scanning all");
	    scanAll();

        while (true) {

            s = ss.accept(); // Accept the incoming request from mobile

            in = new BufferedReader(new InputStreamReader(s.getInputStream(),"utf-8"));
            out = new PrintWriter(new BufferedOutputStream(s.getOutputStream()));

            // Create a new object for handling this request.
            Client c = new Client(s, in, out);

            // Create a new Thread with this object.
            Thread t = new Thread(c);
            t.start();
            
        }
    }
    
    public static ArrayList<Integer> scanNetwork(String yr, String mk, String ml){
    	return year.get(yr).get(mk).get(ml);
    }
    
    private static void init() throws Exception {
        /*
         * To configure the Credentials value including the "access_key_id" and "secret_key_id"
         * which could also be found in ./.aws/credentials
         */
        
		//change the below values to match with your instance 
    	BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAQEFHXT2SZJDVOL7R","UqZzS19UYpVf8XvXYGph/7PaPbTtraV+4MSY68fx");
    	                              
        dynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .withRegion("us-west-2") //the region of your instance --- the availability zone showed in your EC2 main page 
            .build();
        
    }
    
    private static Map<String, AttributeValue> newItem(String year, String make, String model, String name, String insta, String throttle, String injectors, String pulley, String headers, String frontpipe, String catback, String diameter) {
	    Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
	    item.put("year", new AttributeValue(year));
	    item.put("make", new AttributeValue(make));
	    item.put("model", new AttributeValue(model));
	    item.put("name", new AttributeValue(name));
	    item.put("insta", new AttributeValue(insta));
	    item.put("throttle", new AttributeValue(throttle));
	    item.put("injectors", new AttributeValue(injectors));
	    item.put("pulley", new AttributeValue(pulley));
	    item.put("headers", new AttributeValue(headers));
	    item.put("frontpipe", new AttributeValue(frontpipe));
	    item.put("catback", new AttributeValue(catback));
	    item.put("diameter", new AttributeValue(diameter));

	    return item;
	}
    
 // get all the items

    public static void scanAll() {
    	
    	//scanning for fruits
	    ScanRequest scanRequest = new ScanRequest()
	        .withTableName("Cars");
	
	    ScanResult scanResult = dynamoDB.scan(scanRequest);
	
	    for (Map<String, AttributeValue> item :scanResult.getItems()){
	    	CarProfile profile = new CarProfile();
	    	for (Map.Entry<String, AttributeValue> item1 : item.entrySet()) {
	 	         String attributeName = item1.getKey();
	 	         AttributeValue value = item1.getValue();
	 	         
	 	         //fruits.add(value.getS());
	 	         System.out.println(attributeName+": "+(value.getS()));
	 	         
	 	         if(attributeName == "insta")
	 	        	 profile.setInsta(value.getS().toString());
	 	         else if(attributeName == "throttle")
	 	        	 profile.setThrottle(value.getS().toString());
	 	         else if(attributeName == "headers")
	 	        	 profile.setHeaders(value.getS().toString());
	 	         else if(attributeName == "catback")
	 	        	 profile.setCatback(value.getS().toString());
	 	         else if(attributeName == "diameter")
	 	        	 profile.setDiameter(value.getS().toString());
	 	         else if(attributeName == "year")
 	        	     profile.setYear(value.getS().toString());
	 	         else if(attributeName == "injectors")
	 	        	 profile.setInjectors(value.getS().toString());
	 	         else if(attributeName == "name")
	 	        	 profile.setName(value.getS().toString());
	 	         else if(attributeName == "model")
	 	        	 profile.setModel(value.getS().toString());
	 	         else if(attributeName == "pulley")
	 	        	 profile.setPulley(value.getS().toString());
	 	         else if(attributeName == "frontpipe")
	 	        	 profile.setFrontpipe(value.getS().toString());
	 	         else if(attributeName == "make")
	 	        	 profile.setMake(value.getS().toString());
	 	         
	        }
	    	//add to profiles array list
	    	profiles.add(profile);
	    }
	    
	    //for all profiles in the profiles array list add to hash table
	    for(CarProfile pf : profiles) {
	    	addToNetwork(pf);
	    }
	    
    }
    
    public static void makeCars() throws InterruptedException {
    	try {
	    	String tableName = "Cars";
	        
	        // Create a table with a primary hash key named 'name', which holds a string
	        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
	            .withKeySchema(new KeySchemaElement().withAttributeName("name").withKeyType(KeyType.HASH))
	            .withAttributeDefinitions(new AttributeDefinition().withAttributeName("name").withAttributeType(ScalarAttributeType.S))
	            .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(3L).withWriteCapacityUnits(3L));
	
	        // Create table if it does not exist yet
	        TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
	        // wait for the table to move into ACTIVE state
	        TableUtils.waitUntilActive(dynamoDB, tableName);

	        
	        //adding to fruits
	        addToTable(tableName, "2019","Toyota","86","Justin Edens","@justinedens","Bored","600cc","Light Weight","Headers","Midpipe","Catback","3.0 Inch");
        
    	} catch (AmazonServiceException ase) {
	        System.out.println("Cars not made");
	    } catch (AmazonClientException ace) {
	        System.out.println("Cars not made");
	    }
        
    }
    
    public static void addToTable(String tableName, String year, String make, String model, String name, String insta, String throttle, String injectors, String pulley, String headers, String frontpipe, String catback, String diameter) {
    	Map<String, AttributeValue> item;
        item = newItem(year,make,model,name,insta,throttle,injectors,pulley,headers,frontpipe,catback,diameter);
        PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
        PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
        System.out.println("Result: " + putItemResult);
    }
    
    public static void addToNetwork(CarProfile pf) {
    	
    	//if year exists
    	if(year.containsKey(pf.getYear())) {
    		//if make exists
    		if(year.get(pf.getYear()).containsKey(pf.getMake())) {
    			//if model exists
    			if(year.get(pf.getYear()).get(pf.getMake()).containsKey(pf.getModel())) {
    				//adds index of profile to array list
    				year.get(pf.getYear()).get(pf.getMake()).get(pf.getModel()).add(profiles.indexOf(pf));
    			}
    			//if model does not exist
    			else {
    				//makes array list for id
    				ArrayList<Integer> temp = new ArrayList<Integer>();
    				//put id in array list
    				temp.add(profiles.indexOf(pf));
    				
    				//put array in hash map for models
    				year.get(pf.getYear()).get(pf.getMake()).put(pf.getModel(), temp);
    			}
    		}
    		//if make does not exist
    		else {	  
    			//makes new hash map for new models
    			HashMap<String,ArrayList<Integer>> modelTemp = new HashMap< String,ArrayList<Integer>>();
    			
    			//makes new array list for id
    			ArrayList<Integer> temp = new ArrayList<Integer>();
    			//put id in array list
				temp.add(profiles.indexOf(pf));
				
				//put array in hash map for models
				modelTemp.put(pf.getModel(), temp);
    			
				//put hash map in hash map for makes
    			year.get(pf.getYear()).put(pf.getMake(), modelTemp);
    		}
    	}
    	//else year does not exist yet
    	else {
    		//makes hash map for makes
    		HashMap<String,HashMap< String,ArrayList<Integer>>> makeTemp = new HashMap<String,HashMap< String,ArrayList<Integer>>>();
			
    		//makes hash map for models
			HashMap<String,ArrayList<Integer>> modelTemp = new HashMap< String,ArrayList<Integer>>();
			
			//makes array list for id
			ArrayList<Integer> temp = new ArrayList<Integer>();
			//put id in array list
			temp.add(profiles.indexOf(pf));
			
			//put array in hash map for models
			modelTemp.put(pf.getModel(), temp);
			
			//put hash map of models in hash map of makes
			makeTemp.put(pf.getMake(), modelTemp);
			
			//puts hash map of makes into hash map of years
			year.put(pf.getYear(), makeTemp);
    	}
    }
    
}
