/*
 * Copyright (c) 2023 DenArt Designs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package hopzone.eu.global;

import hopzone.eu.Configurations;
import hopzone.eu.model.GlobalResponse;
import hopzone.eu.util.*;
import hopzone.eu.vote.VDSystem;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * @Author Rationale
 * Base structure credits goes on Rationale Discord: Rationale#7773
 * <p>
 * VDS Stands for: Vote Donation System
 * Script website: https://itopz.com/
 * Partner website: https://hopzone.eu/
 * Script version: 1.6
 * Pack Support: Lucera NO GUI
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * How to install https://www.youtube.com/watch?v=yjCc0HUcErI&list=PLVFjZCVNx9SYzAT4Xp56cV9MKhhI3Sp2z
 */
public class Global
{
	// logger
	private static final Logs _log = new Logs(Global.class.getSimpleName());
	
	// global server vars
	private static int storedVotes, serverVotes, serverRank, serverNeededVotes, serverNextRank;
	private static int responseCode;
	
	// ip array list
	private final List<String> FINGERPRINT = new ArrayList<>();
	
	/**
	 * Global reward main function
	 */
	public Global()
	{
		// check if allowed the HOPZONE reward to start
		if (Configurations.HOPZONE_EU_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("HOPZONE"), 100, Configurations.HOPZONE_EU_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + ": HOPZONE reward started.");
		}
		
		// check if allowed the ITOPZ reward to start
		if (Configurations.ITOPZ_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("ITOPZ"), 100, Configurations.ITOPZ_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + ": ITOPZ reward started.");
		}
		
		// check if allowed the HOPZONENET reward to start
		if (Configurations.HOPZONE_NET_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("HOPZONENET"), 100, Configurations.HOPZONE_NET_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + ": HOPZONENET reward started.");
		}
		
		// check if allowed the L2TOPGAMESERVER reward to start
		if (Configurations.L2TOPGAMESERVER_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("L2TOPGAMESERVER"), 100, Configurations.L2TOPGAMESERVER_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + ": L2TOPGAMESERVER reward started.");
		}
		
		// check if allowed the L2JBRASIL reward to start
		if (Configurations.L2JBRASIL_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("L2JBRASIL"), 100, Configurations.L2JBRASIL_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + ": L2JBRASIL reward started.");
		}
		
		// check if allowed the L2NETWORK reward to start
		if (Configurations.L2NETWORK_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("L2NETWORK"), 100, Configurations.L2NETWORK_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + ": L2NETWORK reward started.");
		}
		
		// check if allowed the HOTSERVERS reward to start
		if (Configurations.HOTSERVERS_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("HOTSERVERS"), 100, Configurations.HOTSERVERS_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + " :HOTSERVERS reward started.");
		}
		
		// check if allowed the L2VOTES reward to start
		if (Configurations.L2VOTES_GLOBAL_REWARD)
		{
			VDSThreadPool.scheduleAtFixedRate(() -> execute("L2VOTES"), 100, Configurations.L2VOTES_VOTE_CHECK_DELAY * 1000);
			_log.info(Global.class.getSimpleName() + ": L2VOTES reward started.");
		}
	}
	
	/**
	 * set server information vars
	 */
	public void execute(String TOPSITE)
	{
		// get response from topsite about this ip address
		Optional.ofNullable(GlobalResponse.OPEN(Url.from(TOPSITE + "_GLOBAL_URL").toString()).connect(TOPSITE, VDSystem.VoteType.GLOBAL)).ifPresent(response ->
		{
			// set variables
			responseCode = response.getResponseCode();
			serverNeededVotes = response.getServerNeededVotes();
			serverNextRank = response.getServerNextRank();
			serverRank = response.getServerRank();
			serverVotes = response.getServerVotes();
		});
		// check topsite response
		if (responseCode != 200 || serverVotes == -2)
		{
			return;
		}
		
		storedVotes = Utilities.selectGlobalVar(TOPSITE, "votes");
		
		// check if default return value is -1 (failed)
		if (storedVotes == -1)
		{
			// save votes
			Utilities.saveGlobalVar(TOPSITE, "votes", serverVotes);
			return;
		}
		
		// check stored votes are lower than server votes
		if (storedVotes < serverVotes)
		{
			// save votes
			Utilities.saveGlobalVar(TOPSITE, "votes", storedVotes);
		}
		
		// monthly reset
		if (storedVotes > serverVotes)
		{
			// save votes
			Utilities.saveGlobalVar(TOPSITE, "votes", serverVotes);
		}
		
		// announce current votes
		switch (TOPSITE)
		{
			case "HOPZONE":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.HOPZONE_EU_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.HOPZONE_EU_VOTE_STEP) + " votes!");
				break;
			case "ITOPZ":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.ITOPZ_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.ITOPZ_VOTE_STEP) + " votes!");
				break;
			case "HOPZONENET":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.HOPZONE_NET_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.HOPZONE_NET_VOTE_STEP) + " votes!");
				break;
			case "L2JBRASIL":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.L2JBRASIL_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.L2JBRASIL_VOTE_STEP) + " votes!");
				break;
			case "L2NETWORK":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.L2NETWORK_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.L2NETWORK_VOTE_STEP) + " votes!");
				break;
			case "L2TOPGAMESERVER":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.L2TOPGAMESERVER_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.L2TOPGAMESERVER_VOTE_STEP) + " votes!");
				break;
			case "HOTSERVERS":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.HOTSERVERS_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.HOTSERVERS_VOTE_STEP) + " votes!");
				break;
			case "L2VOTES":
				// check for vote step reward
				if (serverVotes >= storedVotes + Configurations.L2VOTES_VOTE_STEP)
				{
					// reward all online players
					reward(TOPSITE);
				}
				// announce next reward
				Utilities.announce(TOPSITE, "Next reward at " + (storedVotes + Configurations.L2VOTES_VOTE_STEP) + " votes!");
				break;
		}
	}
	
	/**
	 * reward players
	 */
	private void reward(String TOPSITE)
	{
		// iterate through all players
		for (Player player : GameObjectsStorage.getAllPlayers().stream().filter(Objects::nonNull).collect(Collectors.toList()))
		{
			// set player signature key
			String key = "";
			try
			{
				key = Objects.requireNonNull(player.getNetConnection().getHwid(), Objects.requireNonNull(player.getIP(), player.getName()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
			
			// if key exists ignore player
			if (FINGERPRINT.contains(key))
			{
				continue;
			}
			// add the key on ignore list
			FINGERPRINT.add(key);
			
			for (final int itemId : Rewards.from(TOPSITE + "_GLOBAL_REWARDS").keys())
			{
				// check if the item id exists
				final ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
				if (Objects.nonNull(item))
				{
					// get config values
					final Integer[] values = Rewards.from(TOPSITE + "_GLOBAL_REWARDS").get(itemId);
					// set min count value of received item
					int min = values[0];
					// set max count value of received item
					int max = values[1];
					// set chances of getting the item
					int chance = values[2];
					// set count of each item
					long count = Random.get(min, max);
					// chance for each item
					if (Random.get(100) > chance || chance >= 100)
					{
						// reward item
						Functions.addItem(player, itemId, count);
					}
				}
			}
		}
		
		FINGERPRINT.clear();
		
		// announce the reward
		Utilities.announce(TOPSITE, "Thanks for voting! Players rewarded!");
		// save votes
		Utilities.saveGlobalVar(TOPSITE, "votes", serverVotes);
	}
	
	public static Global getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final Global _instance = new Global();
	}
}