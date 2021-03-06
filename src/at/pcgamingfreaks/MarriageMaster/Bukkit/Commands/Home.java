/*
 *   Copyright (C) 2014-2015 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.MarriageMaster.Bukkit.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;

public class Home 
{
	private MarriageMaster plugin;
	
	private long delaytime;

	public Home(MarriageMaster marriagemaster) 
	{
		plugin = marriagemaster;
		
		delaytime = plugin.config.TPDelayTime() * 20L;
	}
	
	public void TPAdmin(Player admin, String player)
	{
		Location loc = plugin.DB.GetMarryHome(player);
		if(loc != null)
		{
			admin.teleport(loc);
		}
		else
		{
			admin.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.PlayerNoHome"));
		}
	}

	public void TP(Player player)
	{
		Location loc = plugin.DB.GetMarryHome(player);
		
		if(loc != null)
		{
			if(plugin.economy == null || plugin.economy.HomeTeleport(player))
			{
				TPHome(player, loc);
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.NoHome"));
		}
	}
	
	private void TPHome(Player player, Location loc)
	{
		if(plugin.config.DelayTP() && !plugin.CheckPerm(player, "marry.skiptpdelay", false))
		{
			final Location p_loc = player.getLocation(), toloc = loc;
			final Player p = player;
			final double p_hea = (double)player.getHealth();
			p.sendMessage(ChatColor.GOLD + String.format(plugin.lang.Get("Ingame.TPDontMove"), plugin.config.TPDelayTime()));
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() { @Override public void run() {
				if(p != null && p.isOnline())
				{
					if(p_hea <= p.getHealth() && p_loc.getX() == p.getLocation().getX() && p_loc.getY() == p.getLocation().getY() && p_loc.getZ() == p.getLocation().getZ() && p_loc.getWorld().equals(p.getLocation().getWorld()))
					{
						p.teleport(toloc);
						p.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.HomeTP"));
					}
					else
					{
						p.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.TPMoved"));
					}}}}, delaytime);
		}
		else
		{
			player.teleport(loc);
			player.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.HomeTP"));
		}
	}
	
	public void BungeeHomeDelay(final Player p)
	{
		if(p != null)
		{
			final double p_hea = p.getHealth();
			final Location p_loc = p.getLocation();
			p.sendMessage(ChatColor.GOLD + String.format(plugin.lang.Get("Ingame.TPDontMove"), plugin.config.TPDelayTime()));
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						if(p != null && p.isOnline())
						{
							if(p_hea <= p.getHealth() && p_loc.getX() == p.getLocation().getX() && p_loc.getY() == p.getLocation().getY() && p_loc.getZ() == p.getLocation().getZ() && p_loc.getWorld().equals(p.getLocation().getWorld()))
							{
								plugin.pluginchannel.sendMessage("home|" + p.getName());
							}
							else
							{
								p.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.TPMoved"));
							}
						}
					}}, delaytime);
		}
	}

	public void SetHome(Player player)
	{
		Location home = player.getLocation();
		if(plugin.economy == null || plugin.economy.HomeTeleport(player))
		{
			plugin.DB.SetMarryHome(home, player);
			player.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.HomeSet"));
		}
	}
}
