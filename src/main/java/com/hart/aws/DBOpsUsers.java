package com.hart.aws;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hart.user.User;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by
 *
 *
 *  \\\\    \\\\         \\\\\\         \\\\\\\\\\     \\\\\\\\\\\\
 *  \\\\    \\\\       \\\\  \\\\       \\\\    \\     \\\\\\\\\\\\
 *  \\\\\\\\\\\\     \\\\      \\\\     \\\\\\\\\\         \\\\
 *  \\\\\\\\\\\\     \\\\\\\\\\\\\\     \\\\\\             \\\\
 *  \\\\    \\\\     \\\\      \\\\     \\\\  \\           \\\\
 *  \\\\    \\\\     \\\\      \\\\     \\\\    \\         \\\\
 *
 *
 */
public class DBOpsUsers {

    //Create
    public static void AddUser(String email, String password, String username, Integer reqs, String createdAt, String image) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("users");


        try {
            System.out.println("Adding a new user...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("email", email, "password", password)
                            .withString("username", username)
                            .withString("createdAt", createdAt)
                            .withString("image", image)
                            .withStringSet("roles", "ROLE_USER")
                            .withInt("reqs", reqs));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        } catch (Exception e) {
            System.err.println("Unable to add item: " + email + " " + username);
            System.err.println(e.getMessage());
        }
    }

    //Increment link hit count on GET
    public static void IncrementCounter(String little) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_WEST_2);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("links");


        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("little", little)
                .withUpdateExpression("set hit = hit + :val")
                .withValueMap(new ValueMap().withNumber(":val", 1)).withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Incrementing the atomic counter for link hits...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        } catch (Exception e) {
            System.err.println("Unable to update item: " + little);
            System.err.println(e.getMessage());
        }
    }

    public static User GetUser(String email) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("users");
        String returnString = null;

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", email); //  "title", title

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);

            Gson gson = new Gson();
            User user = gson.fromJson(outcome.toJSON(), User.class);

            //returnString = "succeeded: " + outcome.toJSONPretty();
            System.out.println("GetItem succeeded: " + outcome.toJSONPretty());
            return user;

        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + email );
            System.err.println(e.getMessage());
            returnString = e.getMessage();
            return null;
        }
    }

    //SCAN
    //returns the entire database unless you apply filters.
    public static ArrayList<User> ScanDB() throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("users");
        String returnString = null;
        ArrayList<User> usersList = new ArrayList<>();

//        ScanSpec scanSpec = new ScanSpec().withProjectionExpression("#yr, title, info.rating")
//                .withFilterExpression("#yr between :start_yr and :end_yr").withNameMap(new NameMap().with("#yr", "year"))
//                .withValueMap(new ValueMap().withNumber(":start_yr", 1950).withNumber(":end_yr", 1959));

        try {
            ItemCollection<ScanOutcome> items = table.scan(); //scanSpec
            User users;

            Iterator<Item> iter = items.iterator();
            Item item;
            while (iter.hasNext()) {
                item = iter.next();
                System.out.println(item.toJSON());

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                users = gson.fromJson(item.toJSON(), User.class);

                User user2add = new User(users.getEmail(), users.getPassword(), users.getUsername(), users.getRoles() , 5, users.getCreatedAt(), users.getImage());
                usersList.add(user2add);
            }
        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

        return usersList;
    }

    //UPDATE ITEM     ********to update password:  String password
    public static String UpdateUser(String email, String image) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("users");


        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("email", email)
//                .withUpdateExpression("set info.rating = :r, info.plot=:p, info.actors=:a")
                .withUpdateExpression("set image = :i")
                .withValueMap(new ValueMap().withString(":i", image))
                //.withUpdateExpression("set password = :p")
                //.withValueMap(new ValueMap().withString(":p", password))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating " + email + "...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            String returnString = ("Update user graph:\n" + outcome.getItem().toJSONPretty());
            return returnString;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "error 507 : USER CANNOT BE UPDATED";
        }
    }

    //DELETE ITEM.....
    public static String DeleteUser(String email) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("users");


        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("email", email));
        //.withConditionExpression("info.rating <= :val")
        //.withValueMap(new ValueMap().withNumber(":val", 5.0));

        try {
            System.out.println("Attempting a conditional delete...");
            table.deleteItem(deleteItemSpec);
            return "DeleteItem succeeded";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "Unable to delete item: " + email;
        }
    }
}


