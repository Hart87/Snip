package com.hart.aws;

/**
 * Created by jameshart on 9/6/18.
 */

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hart.link.Link;

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
public class DBOpsLinks {

    //Create
    public static void AddLink(String little, String big, String description, Integer hit, String createdAt) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("links");

//        final Map<String, Object> infoMap = new HashMap<String, Object>();
//        infoMap.put("plot", "Nothing happens at all.");
//        infoMap.put("rating", 0);


        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("little", little, "big", big)
                            .withString("description", description)
                            .withString("createdAt", createdAt)
                            .withInt("hit", hit));
//                            .withMap("info", infoMap))

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + little + " " + description);
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

        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + little );
            System.err.println(e.getMessage());
        }
    }

//    public static String GetLink(String email, String lastName) throws Exception {
//        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
//        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
//        DynamoDB dynamoDB = new DynamoDB(client);
//
//        Table table = dynamoDB.getTable("links");
//        String returnString = null;
//
////        String email = "hart87@gmail.com";
////        String lastName = "Hart";
//
//        GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", email); //  "title", title
//
//        try {
//            System.out.println("Attempting to read the item...");
//            Item outcome = table.getItem(spec);
//            returnString = "succeeded: " + outcome.toJSONPretty();
//            //System.out.println("GetItem succeeded: " + outcome);
//
//        }
//        catch (Exception e) {
//            System.err.println("Unable to read item: " + email );
//            System.err.println(e.getMessage());
//            returnString = e.getMessage();
//        }
//        return returnString;
//    }

    //SCAN
    //returns the entire database unless you apply filters.
    public static ArrayList<Link> ScanDB() throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("links");
        String returnString = null;
        ArrayList<Link> linksList = new ArrayList<Link>();

//        ScanSpec scanSpec = new ScanSpec().withProjectionExpression("#yr, title, info.rating")
//                .withFilterExpression("#yr between :start_yr and :end_yr").withNameMap(new NameMap().with("#yr", "year"))
//                .withValueMap(new ValueMap().withNumber(":start_yr", 1950).withNumber(":end_yr", 1959));

        try {
            ItemCollection<ScanOutcome> items = table.scan(); //scanSpec
            Link links;

            Iterator<Item> iter = items.iterator();
            Item item;
            while (iter.hasNext()) {
                item = iter.next();
                System.out.println(item.toJSON());

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                links = gson.fromJson(item.toJSON(), Link.class);

                Link link2add = new Link(links.getBig(), links.getLittle(), links.getDescription(), links.getHit(), links.getCreatedAt());
                linksList.add(link2add);
            }
        }
        catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

        return linksList;
    }

    //UPDATE ITEM
    public static String UpdateLinkHit(String little, Integer newHits) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("links");


        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("little", little)
//                .withUpdateExpression("set info.rating = :r, info.plot=:p, info.actors=:a")
                .withUpdateExpression("set hit = :a")
                .withValueMap(new ValueMap().withInt(":a", newHits))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating " + little +  "...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            String returnString = ("Update hits succeeded:\n" + outcome.getItem().toJSONPretty());
            return returnString;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return "error 505 : ITEM HIT COUNT NOT UPDATED";
        }
    }

    //DELETE ITEM.....
    public static String DeleteLink(String little) throws Exception {
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAJVWQTEYELFA6SWJA", "8ZNyh1AsicTX1D3ZZQN3INCHGm4EVmv34z0kvDEJ");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials).withRegion(Regions.US_EAST_1);
        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("links");


        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("little", little));
        //.withConditionExpression("info.rating <= :val")
        //.withValueMap(new ValueMap().withNumber(":val", 5.0));

        try {
            System.out.println("Attempting a conditional delete...");
            table.deleteItem(deleteItemSpec);
            return "DeleteItem succeeded";
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return "Unable to delete item: " + little;
        }
    }

}