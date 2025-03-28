package com.bivoto;

import com.google.gson.Gson;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GrandExchangeOfferChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.io.File;

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

	private DataHandler dataHandler;

	@Override
	protected void startUp() throws Exception
	{
		dataHandler = injector.getInstance(DataHandler.class);
		dataHandler.start();
		log.info("Example started!");
		om.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		om.remove(overlay);
		dataHandler.shutdown();
	}

	@Subscribe
	public void onGrandExchangeOfferChanged(GrandExchangeOfferChanged grandExchangeOfferChanged)
	{
		var itemID = grandExchangeOfferChanged.getOffer().getItemId();
		var quantity = grandExchangeOfferChanged.getOffer().getQuantitySold();
		var spent = grandExchangeOfferChanged.getOffer().getSpent();
		var trade = dataHandler.findTradeByItemId(itemID);

		if(trade == null) return;

		var oldQuantity = trade.getQuantity();
		var oldSpent = trade.getTotalPrice();

		var addQuantity = quantity + oldQuantity;
		var addSpent = spent + oldSpent;
		var subQuantity = quantity - oldQuantity;
		var subSpent = spent - oldSpent;

		if (grandExchangeOfferChanged.getOffer().getState() == GrandExchangeOfferState.BUYING && grandExchangeOfferChanged.getOffer().getSpent() > 0)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Item:" + grandExchangeOfferChanged.getOffer().getItemId() + " Quantity:" + grandExchangeOfferChanged.getOffer().getTotalQuantity() + " Price:" + grandExchangeOfferChanged.getOffer().getSpent(), null);
			dataHandler.addTrade(itemID, addQuantity, addSpent);
		}

		if (grandExchangeOfferChanged.getOffer().getState() == GrandExchangeOfferState.SELLING && grandExchangeOfferChanged.getOffer().getSpent() > 0)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Item:" + grandExchangeOfferChanged.getOffer().getItemId() + " Quantity:" + grandExchangeOfferChanged.getOffer().getTotalQuantity() + " Price:" + grandExchangeOfferChanged.getOffer().getSpent(), null);
			dataHandler.addTrade(itemID, subQuantity, subSpent);
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
