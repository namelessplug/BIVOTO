package com.bivoto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

public class Trade {
    private int itemId;     // Unique ID of the item
    private int quantity;      // Quantity of the item traded
    private double totalPrice; // Total price of the trade

    // Constructor
    public Trade(int itemId, int quantity, double totalPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "itemId:'" + itemId + '\'' +
                ", quantity:" + quantity +
                ", totalPrice:" + totalPrice +
                '}';
    }

    public static void updateTradeInJson(String filePath, int itemId, int newQuantity, double newPrice) {
        try {
            // Step 1: Read JSON file into a List of Trade objects
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Trade>>() {}.getType();
            List<Trade> trades;

            try (FileReader reader = new FileReader(filePath)) {
                trades = gson.fromJson(reader, listType);
            }

            // Step 2: Find the target Trade and update it
            boolean updated = false;
            for (Trade trade : trades) {
                if (trade.getItemId() == itemId) {
                    trade.setQuantity(newQuantity);
                    trade.setTotalPrice(newPrice);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                System.out.println("No trade found with itemId: " + itemId);
                return;
            }

            // Step 3: Write the updated list back to the file
            try (FileWriter writer = new FileWriter(filePath)) {
                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                prettyGson.toJson(trades, writer);
            }

            System.out.println("Trade updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

