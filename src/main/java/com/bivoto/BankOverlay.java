package com.bivoto;
import java.lang.Math;
import com.google.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;
import java.awt.*;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.itemprices.ItemPricesConfig;

import javax.xml.crypto.Data;

class BankOverlay extends WidgetItemOverlay
{
    private final ItemManager itemManager;
    private final DataHandler dataHandler;

    @Inject
    BankOverlay(ItemManager itemManager, DataHandler dataHandler)
    {
        this.dataHandler = dataHandler;
        this.itemManager = itemManager;
        showOnBank();
        revalidate();

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
        var value1 = itemManager.getItemPrice(widgetItem.getId());
        var value2 = dataHandler.findTradeByItemId(widget).getTotalPrice() / dataHandler.findTradeByItemId(widget).getQuantity();
        var absolute = Math.abs(value2 - value1);
        textComponent.setPosition(new Point(bounds.x - 1, bounds.y + bounds.height - 1));
        textComponent.setColor(Color.PINK);
        textComponent.setText(""+ calculatePercentageDifference(value1, value2));

        textComponent.render(graphics);

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