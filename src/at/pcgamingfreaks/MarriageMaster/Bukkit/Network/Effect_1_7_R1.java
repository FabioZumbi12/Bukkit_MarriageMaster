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

package at.pcgamingfreaks.MarriageMaster.Bukkit.Network;

import net.minecraft.server.v1_7_R1.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;

public class Effect_1_7_R1 extends EffectBase
{
	public void SpawnParticle(Location loc, Effects type, double visrange, int count, float random1, float random2, float random3, float random4) throws Exception
	{
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
		Reflection.setValue(packet, "a", type.getName());
		Reflection.setValue(packet, "b", (float) loc.getX());
		Reflection.setValue(packet, "c", (float) loc.getY());
		Reflection.setValue(packet, "d", (float) loc.getZ());
		Reflection.setValue(packet, "e", random1);
		Reflection.setValue(packet, "f", random2);
		Reflection.setValue(packet, "g", random3);
		Reflection.setValue(packet, "h", random4);
		Reflection.setValue(packet, "i", count);
		for(Entity entity : loc.getWorld().getEntities())
		{
			if(entity instanceof CraftPlayer && entity.getLocation().getWorld().equals(loc.getWorld()) && entity.getLocation().distance(loc) < visrange)
			{
				((CraftPlayer)entity).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}
}