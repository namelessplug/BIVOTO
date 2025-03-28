package com.bivoto;
import com.google.inject.Singleton;
import java.lang.Math;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;
import java.awt.*;
import java.text.DecimalFormat;
import java.math.RoundingMode;


import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.itemprices.ItemPricesConfig;

import javax.xml.crypto.Data;

@Slf4j
@Singleton
class BankOverlay extends WidgetItemOverlay
{
    private final ItemManager itemManager;
    private final DataHandler dataHandler;
    private static final DecimalFormat df = new DecimalFormat("#.00");
    @Inject
    BankOverlay(ItemManager itemManager, DataHandler dataHandler)
    {
        this.dataHandler = dataHandler;
        this.itemManager = itemManager;
        showOnBank();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {

        if (widgetItem == null || widgetItem.getId() == -1)
        {
            return; // Exit early if widgetItem is invalid
        }

        graphics.setFont(FontManager.getRunescapeSmallFont());
        var bounds = widgetItem.getCanvasBounds();
        var textComponent = new TextComponent();
        var widget = widgetItem.getId();
        var trade = dataHandler.findTradeByItemId(widget);

        if(trade == null){
            System.out.printf("Eat my entire ass id: %s%n", widget);
            return;
        }
        var value1 = itemManager.getItemPrice(widgetItem.getId());
        var value2 = trade.getQuantity() > 0? trade.getTotalPrice() / trade.getQuantity(): 0;
        var absolute = Math.abs(value2 - value1);
        textComponent.setPosition(new Point(bounds.x - 1, bounds.y + bounds.height - 1));
        textComponent.setColor(Color.PINK);
        textComponent.setText(posOrNeg(value1, value2) + df.format(calculatePercentageDifference(value1, value2)));

        textComponent.render(graphics);

    }

    public String posOrNeg(int value1, int value2){
        if (value1 > value2){
            return "+";
        }
        return "-";
    }

    public static double calculatePercentageDifference(double value1, double value2) {
        // Avoid division by zero: return 0 if both values are 0
        if (value1 == 0 && value2 == 0) {
            return 0;
        }

        // Calculate the percentage difference
        double absoluteDifference = Math.abs(value1 - value2);
        double average = (value1 + value2) / 2.0;
        return (absoluteDifference / average) * 100;
    }
}