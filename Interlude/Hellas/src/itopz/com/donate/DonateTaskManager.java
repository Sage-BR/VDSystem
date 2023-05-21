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
package itopz.com.donate;

import com.l2jhellas.gameserver.datatables.sql.ItemTable;
import com.l2jhellas.gameserver.model.L2World;
import com.l2jhellas.gameserver.model.actor.instance.L2PcInstance;
import com.l2jhellas.gameserver.model.actor.item.L2Item;
import com.l2jhellas.gameserver.network.serverpackets.ActionFailed;
import com.l2jhellas.util.database.L2DatabaseFactory;
import itopz.com.gui.Gui;
import itopz.com.util.Logs;
import itopz.com.util.Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * @Author Rationale
 * Base structure credits goes on Rationale Discord: Rationale#7773
 * <p>
 * VDS Stands for: Vote Donation System
 * Script website: https://itopz.com/
 * Partner website: https://hopzone.eu/
 * Script version: 1.5
 * Pack Support: L2JHellas 562 https://app.assembla.com/spaces/l2hellas/subversion/source
 * <p>
 * Freemium Donate Panel V4: https://www.denart-designs.com/
 * Download: https://mega.nz/folder/6oxUyaIJ#qQDUXeoXlPvBjbPMDYzu-g
 * Buy: https://shop.denart-designs.com/product/auto-donate-panel-v4/
 *
 * How to install https://www.youtube.com/watch?v=yjCc0HUcErI&list=PLVFjZCVNx9SYzAT4Xp56cV9MKhhI3Sp2z
 */
public class DonateTaskManager implements Runnable
{
	// logger
	private static final Logs _log = new Logs(DonateTaskManager.class.getSimpleName());

	private final String DELETE = "DELETE FROM donate_holder WHERE no=? LIMIT 1";
	private final String SELECT = "SELECT no, id, count, playername FROM donate_holder";

	@Override
	public void run()
	{
		start();
	}

	/**
	 * reward player if donation is received
	 */
	private void start()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
		     PreparedStatement statement = con.prepareStatement(SELECT);
		     ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(rset.getString("playername"));
				final int no = rset.getInt("no");
				final int id = rset.getInt("id");
				final int count = rset.getInt("count");

				Optional.ofNullable(player).ifPresent(s ->
				{
					if (removeDonation(no))
					{
						final L2Item item = ItemTable.getInstance().getTemplate(id);

						if (Objects.nonNull(item))
						{
							Gui.getInstance().ConsoleWrite("Donation: " + player.getName() + " received " + count + "x " + item.getItemName());
							player.addItem("", id, count, player, true);
							player.sendPacket(new ActionFailed());
						}
					}
				});
			}
		} catch (final Exception e)
		{
			String error = e.getMessage();
			_log.warn("Check donate items failed. " + error);

			if (error.contains("doesn't exist") && error.contains("donate_holder"))
			{
				Utilities.deleteTable(Utilities.DELETE_DONATE_TABLE, "Donate");
				Utilities.createTable(Utilities.CREATE_DONATE_TABLE, "Donate");
			}
		}
	}

	/**
	 * Remove donation from database
	 *
	 * @param id int
	 * @return boolean
	 */
	private boolean removeDonation(int id)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
		     PreparedStatement statement = con.prepareStatement(DELETE))
		{
			statement.setInt(1, id);
			statement.execute();
			return true;
		} catch (SQLException e)
		{
			_log.warn("Failed to remove donation from database of donation id: " + id);
			_log.warn(e.getMessage());
		}

		return false;
	}
}