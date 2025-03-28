package com.bivoto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Trade {
    private int itemId;     // Unique ID of the item
    private int quantity;      // Quantity of the item traded
    private int totalPrice; // Total price of the trade
}
