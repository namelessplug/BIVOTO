package com.bivoto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {
    private static final Gson gson = new Gson();
    private List<Trade> trades;  // List to store all trades
    private final String filePath; // Path to the JSON file

    /**
     * Constructor: Sets up a dynamic file path and loads trades from the JSON file.
     */

    public void initializeTrades() {
        File jsonFile = new File(filePath); // Reference to the JSON file

        // Step 1: Check if the file already exists
        if (jsonFile.exists()) {
            System.out.println("Trades file already exists. Initialization skipped.");
            return; // Exit the method if the file exists
        }

        // Step 2: Create an empty list for new trades
        List<Trade> newTrades = new ArrayList<>();

        // Step 3: Populate the list with trades having itemIds from 0 to 26155
        for (int i = 0; i <= 26155; i++) {
            int itemId = i; // Convert the itemId to a string
            Trade trade = new Trade(itemId, 0, 0.0); // Create a trade with default values
            newTrades.add(trade);
        }

        // Step 4: Overwrite the trades list and save to JSON
        this.trades = newTrades;
        saveTrades(); // Save the initialized trades to the file

        System.out.println("Trades initialized with itemIds 0 to 26155, quantity set to 0, and price set to 0.0.");
    }


    public boolean updateTrade(int itemId, int newQuantity, double newTotalPrice) {
        // Step 1: Iterate over trades to find the trade matching the provided itemId
        for (Trade trade : trades) {
            if (trade.getItemId() == itemId) {
                // Step 2: Update the trade properties
                trade.setQuantity(newQuantity);
                trade.setTotalPrice(newTotalPrice);

                // Step 3: Save the updated trades back to the file
                saveTrades();

                System.out.println("Trade with itemId '" + itemId + "' successfully updated.");
                return true; // Return true to indicate the update was successful
            }
        }

        // Step 4: If no trade with the itemId is found, print a message and return false
        System.out.println("No trade found with itemId: " + itemId);
        return false;
    }




    public DataHandler() {
        // Create a dynamic file path within the user's home directory
        String userHomeDir = System.getProperty("user.home");
        String appFolder = userHomeDir + File.separator + "trades_app";
        File folder = new File(appFolder);

        // Create the directory if it doesn't exist
        if (!folder.exists()) {
            boolean created = folder.mkdir();
            if (created) {
                System.out.println("Application folder created: " + appFolder);
            }
        }

        // Set the full path to the trades.json file
        this.filePath = appFolder + File.separator + "trades.json";

        // Print the file path to the console
        System.out.println("Trades data will be saved at: " + filePath);


        // Load data from the file (or initialize an empty list if file does not exist)
        this.trades = loadTrades();
    }

    /**
     * Loads trades from the JSON file.
     *
     * @return A list of trades loaded from the file. If no file exists, returns an empty list.
     */
    private List<Trade> loadTrades() {
        try (Reader reader = new FileReader(filePath)) {
            Type tradeListType = new TypeToken<ArrayList<Trade>>() {}.getType();
            return gson.fromJson(reader, tradeListType); // Convert JSON to List<Trade>
        } catch (FileNotFoundException e) {
            System.out.println("No existing trades file found. Starting fresh.");
            return new ArrayList<>(); // If file not found, start with an empty list
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage());
        }
    }

    /**
     * Saves the current trades to the JSON file.
     */
    private void saveTrades() {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(trades, writer); // Convert List<Trade> to JSON and write it
        } catch (IOException e) {
            System.err.println("Error saving trades to file: " + e.getMessage());
        }
    }

    /**
     * Adds a new trade to the list and saves it to the file.
     *
     * @param itemId     Unique ID of the item
     * @param quantity   Quantity of the item traded
     * @param totalPrice Total price of the trade
     */
    public void addTrade(int itemId, int quantity, double totalPrice) {
        Trade newTrade = new Trade(itemId, quantity, totalPrice);
        trades.add(newTrade); // Add the new trade to the list
        saveTrades(); // Save the updated list to the file
        System.out.println("Added new trade: " + newTrade);
    }

    /**
     * Finds and displays a specific trade based on the itemId.
     *
     * @param itemId Unique item ID to search for.
     */
    public Trade findTradeByItemId(Integer itemId) {
        for (Trade trade : trades) {
            if (Integer.valueOf(trade.getItemId()).equals(itemId)) {
                return trade; // Return the matching trade
            }
        }
        return null; // No trade found
    }


    /**
     * Lists all trades currently stored.
     */
    public void listTrades() {
        if (trades.isEmpty()) {
            System.out.println("No trades available.");
            return;
        }

        System.out.println("List of Trades:");
        for (Trade trade : trades) {
            System.out.println(trade);
        }
    }
}

