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

package at.pcgamingfreaks.MarriageMaster.Bukkit.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import at.pcgamingfreaks.MarriageMaster.Bukkit.MarriageMaster;

public class RegainHealth implements Listener
{
	private MarriageMaster plugin;

	public RegainHealth(MarriageMaster marriagemaster) 
	{
		plugin = marriagemaster;
	}

	@EventHandler
	public void onHeal(EntityRegainHealthEvent event) 
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if(player != null)
			{
				Player otherPlayer = plugin.DB.GetPlayerPartner(player);
				if(otherPlayer != null && otherPlayer.isOnline())
				{
					if(plugin.InRadius(player, otherPlayer, plugin.config.GetRange("Heal")))
					{
						event.setAmount((double)plugin.config.GetHealthRegainAmount());
					}
				}
			}
		}
	}
}