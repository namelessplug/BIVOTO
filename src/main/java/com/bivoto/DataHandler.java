package com.bivoto;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DataHandler {

    private static final File PARENT = new File(RuneLite.RUNELITE_DIR, "bivoto");
    private static final File FILE = new File(PARENT, "trades.json");

    private final Gson gson;
    private final String filePath; // Path to the JSON file
    private Map<Integer, Trade> trades = new HashMap<>();

    public void start() {
        if (!PARENT.exists()) {
            log.info("Bivoto directory does not exist, creating it now.");
            PARENT.mkdir();
        }

        if (!FILE.exists()) {
            trades.clear();
            for (var i = 0; i <= 26155; i++) {
                trades.put(i, new Trade(i, 0, 0));
            }
            log.info("Trades initialized with itemIds 0 to 26155, quantity set to 0, and price set to 0.0.");
            saveTrades();
        } else {
            var temp = loadTrades();
            temp.forEach(trade -> trades.put(trade.getItemId(), trade));
            log.info("Trades loaded from file.");
        }
    }

    public void shutdown(){
        saveTrades();
        trades.clear();
    }


    public boolean updateTrade(int itemId, int newQuantity, int newTotalPrice) {
        // Step 1: Iterate over trades to find the trade matching the provided itemId
        for (Trade trade : trades.values()) { //TODO: Change to values
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



    /**
     * Loads trades from the JSON file.
     *
     * @return A list of trades loaded from the file. If no file exists, returns an empty list.
     */
    private List<Trade> loadTrades() {
        if(FILE.exists()){
            try{
                log.info("Loading trades from file.");
                var json = Files.readAllBytes(FILE.toPath());
                var type = new TypeToken<List<Trade>>() {}.getType();
                return gson.fromJson(new String(json), type);
            }catch (Exception e){
                log.error("Error loading trades from file: {}", e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    /**
     * Saves the current trades to the JSON file.
     */
    private void saveTrades() {
        try
        {
            log.info("Saving trades to file.");
            Files.write(FILE.toPath(), gson.toJson(trades.values()).getBytes());
        }
        catch (IOException e)
        {
            log.error("Error saving trades to file: " + e.getMessage());
        }
    }

    /**
     * Adds a new trade to the list and saves it to the file.
     *
     * @param itemId     Unique ID of the item
     * @param quantity   Quantity of the item traded
     * @param totalPrice Total price of the trade
     */
    public void addTrade(int itemId, int quantity, int totalPrice) {
        Trade newTrade = new Trade(itemId, quantity, totalPrice);
        trades.put(itemId, newTrade);
        saveTrades(); //TODO probably shouldn't do this every time we add a trade
        System.out.println("Added new trade: " + newTrade);
    }

    /**
     Finds and displays a specific trade based on the itemId.*
     @param itemId Unique item ID to search for.*/@Nullable
    public Trade findTradeByItemId(int itemId) {
        return trades.get(itemId);}


    /**
     * Lists all trades currently stored.
     */
    public void listTrades() {
        if (trades.isEmpty()) {
            System.out.println("No trades available.");
            return;
        }

        System.out.println("List of Trades:");
        trades.forEach((k, v) -> System.out.println(v));
    }
}
