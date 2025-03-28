package com.bivoto;

import com.google.gson.Gson;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.ItemManager;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.ui.overlay.OverlayManager;

import java.io.File;

import static net.runelite.api.widgets.InterfaceID.BANK;

@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class ExamplePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Inject
	private OverlayManager om;

	@Inject
	private BankOverlay overlay;

	@Inject Gson gson;

	@Inject ConfigManager cm;

	@Override
	protected void startUp() throws Exception
	{
		DataHandler dataHandler = new DataHandler();
		dataHandler.initializeTrades();
		log.info("Example started!");
		om.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		om.remove(overlay);
	}

	@Subscribe

	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged grandExchangeOfferChanged)

	{
		DataHandler dataHandler = new DataHandler();
		dataHandler.initializeTrades();

		String filePath = System.getProperty("user.home") + File.separator + "trades_app" + File.separator + "trades.json";


		var itemID = grandExchangeOfferChanged.getOffer().getItemId();
		var quantity = grandExchangeOfferChanged.getOffer().getQuantitySold();
		var spent = grandExchangeOfferChanged.getOffer().getSpent();
		var oldQuantity = dataHandler.findTradeByItemId(itemID).getQuantity();
		var oldSpent = dataHandler.findTradeByItemId(itemID).getTotalPrice();
		var addQuantity = quantity + oldQuantity;
		var addSpent = spent + oldSpent;
		var subQuantity = quantity - oldQuantity;
		var subSpent = spent - oldSpent;

		if (grandExchangeOfferChanged.getOffer().getState() == GrandExchangeOfferState.BUYING && grandExchangeOfferChanged.getOffer().getSpent() > 0)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Item:" + grandExchangeOfferChanged.getOffer().getItemId() + " Quantity:" + grandExchangeOfferChanged.getOffer().getTotalQuantity() + " Price:" + grandExchangeOfferChanged.getOffer().getSpent(), null);
			Trade.updateTradeInJson(filePath, itemID, addQuantity, addSpent);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Item:" + filePath, null);
		}

		if (grandExchangeOfferChanged.getOffer().getState() == GrandExchangeOfferState.SELLING && grandExchangeOfferChanged.getOffer().getSpent() > 0)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Item:" + grandExchangeOfferChanged.getOffer().getItemId() + " Quantity:" + grandExchangeOfferChanged.getOffer().getTotalQuantity() + " Price:" + grandExchangeOfferChanged.getOffer().getSpent(), null);
			Trade.updateTradeInJson(filePath, itemID, subQuantity, subSpent);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
