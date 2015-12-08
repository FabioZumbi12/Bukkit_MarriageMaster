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

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Effect_1_7 extends EffectBase
{
	private static Class<?> PacketPlayOutParticle = Reflection.getNMSClass("PacketPlayOutWorldParticles");
	
	public void SpawnParticle(Location loc, Effects type, double visrange, int count, float offsetX, float offsetY, float offsetZ, float speed) throws Exception
	{
		try
		{
			Object packet = PacketPlayOutParticle.getConstructor(new Class[] { String.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, int.class })
					.newInstance(type.getName(), (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), offsetX, offsetY, offsetZ, speed, count);
			Object handle, connection;
			for(Entity entity : loc.getWorld().getEntities())
			{
				if(entity instanceof Player && entity.getLocation().getWorld().equals(loc.getWorld()) && entity.getLocation().distance(loc) < visrange)
				{
					handle = Reflection.getHandle(entity);
					if(handle != null && handle.getClass().getName().endsWith(".EntityPlayer"))
					{
						connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
						Reflection.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, new Object[] { packet });
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to spawn particle " + type.getName() + ". (Version 1.7)");
			e.printStackTrace();
		}
	}
}