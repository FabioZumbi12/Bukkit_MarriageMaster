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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;

public class MarryTp 
{
	private MarriageMaster plugin;
	
	private long delaytime;
	
	public MarryTp(MarriageMaster marriagemaster)
	{
		plugin = marriagemaster;
		
		delaytime = plugin.config.TPDelayTime() * 20L;
	}

	@SuppressWarnings("deprecation")
	public void TP(Player player)
	{
		String partner = plugin.DB.GetPartner(player);
		if(partner != null && !partner.isEmpty())
		{
			Player otherPlayer = plugin.getServer().getPlayer(partner);
			if(otherPlayer != null && otherPlayer.isOnline())
			{
				if(player.canSee(otherPlayer))
				{
					if(!plugin.config.GetBlacklistedWorlds().contains(otherPlayer.getWorld().getName()))
					{
						if(plugin.economy == null || plugin.economy.Teleport(player))
						{
							DoTP(player, otherPlayer);
						}
					}
					else
					{
						player.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.WorldNotAllowed"));
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.NoTPInVanish"));
				}
			}
			else
			{
				player.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.PartnerOffline"));
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.NotMarried"));
		}
	}

	private void DoTP(Player player, Player otherPlayer) 
	{
		if(plugin.config.DelayTP() && !plugin.CheckPerm(player, "marry.skiptpdelay", false))
		{
			final Location p_loc = player.getLocation();
			final Player p = player, otp = otherPlayer;
			final double p_hea = (double)player.getHealth();
			p.sendMessage(ChatColor.GOLD + String.format(plugin.lang.Get("Ingame.TPDontMove"), plugin.config.TPDelayTime()));
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() { @Override public void run() {
				if(p != null && p.isOnline())
				{
					if(otp != null && otp.isOnline())
					{
						if(p_hea <= p.getHealth() && p_loc.getX() == p.getLocation().getX() && p_loc.getY() == p.getLocation().getY() && p_loc.getZ() == p.getLocation().getZ() && p_loc.getWorld().equals(p.getLocation().getWorld()))
						{
							if(!plugin.config.getCheckTPSafety() || p.isFlying() || !otp.isFlying())
							{
								p.teleport(otp);
								p.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.TP"));
								otp.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.TPto"));
							}
							else
							{
								Location l = getSaveLoc(otp.getLocation());
								if(l != null)
								{
									p.teleport(l);
									p.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.TP"));
									otp.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.TPto"));
								}
								else
								{
									p.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.TPUnsafe"));
									otp.sendMessage(ChatColor.GOLD + plugin.lang.Get("Ingame.TPtoUnsafe"));
								}
							}
						}
						else
						{
							p.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.TPMoved"));
						}
					}
					else
					{
						p.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.PartnerOffline"));
				}}}}, delaytime);
		}
		else
		{
			player.teleport(otherPlayer);
			player.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.TP"));
			otherPlayer.sendMessage(ChatColor.GREEN + plugin.lang.Get("Ingame.TPto"));
		}
	}
	
	private Location getSaveLoc(Location loc)
	{
		World w = loc.getWorld();
		Material mat;
		int x = loc.getBlockX(), y = loc.getBlockY() - 1, z = loc.getBlockZ(), miny = -1;
		Block b, b1 = w.getBlockAt(x, y + 1, z), b2 = w.getBlockAt(x, y + 2, z);
		loc = null;
		for(; y > 0 && y > miny && loc == null; y--)
		{
			b = w.getBlockAt(x, y, z);
			if(b != null && !b.isEmpty())
			{
				if(miny == -1)
				{
					miny = y - 10;
				}
				mat = b.getType();
				if(!(mat.equals(Material.FIRE) || mat.equals(Material.AIR) || mat.equals(Material.LAVA) || mat.equals(Material.CACTUS)) && ((b1 == null || b1.isEmpty()) && (b2 == null || b2.isEmpty())))
				{
					loc = b.getLocation();
					loc.setY(loc.getY() + 1);
				}
			}
			b2 = b1;
			b1 = b;
		}
		return loc;
	}
	
	public void BungeeTPDelay(final Player p)
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
								plugin.pluginchannel.sendMessage("tp|" + p.getName());
							}
							else
							{
								p.sendMessage(ChatColor.RED + plugin.lang.Get("Ingame.TPMoved"));
							}
						}
					}}, delaytime);
		}
	}
}
