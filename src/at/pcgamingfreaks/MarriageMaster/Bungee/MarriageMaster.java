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

package at.pcgamingfreaks.MarriageMaster.Bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.gravitydevelopment.Updater.Bungee_Updater;
import net.gravitydevelopment.Updater.UpdateResult;
import net.gravitydevelopment.Updater.UpdateType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import org.mcstats.Bungee_Metrics;

import at.pcgamingfreaks.MarriageMaster.Bungee.Commands.*;
import at.pcgamingfreaks.MarriageMaster.Bungee.Database.*;

public class MarriageMaster extends Plugin
{
	public Logger log;
    public Config config;
    public Language lang;
    public Database DB;
    
    // Worker
    public Chat chat = null;
    public Home home = null;
    public TP tp = null;
    
    // EventListener
    public EventListener listener;
    
    // Global Messages
    public BaseComponent[] Message_NoPermission, Message_NotMarried, Message_PartnerOffline;
    
    public void onEnable()
	{
		log = getLogger();
		PluginLoad();
		log.info(lang.getString("Console.Enabled"));
	}
    
    public void PluginLoad()
    {
    	// Loading base Data
		config = new Config(this);
		lang = new Language(this);
		DB = new MySQL(this);
		// Starting Metrics service
		if(config.getUseMetrics())
		{
			try
			{
				Bungee_Metrics metrics = new Bungee_Metrics(this);
			    metrics.start();
			}
			catch (IOException e)
			{
			    log.info(lang.getString("Console.MetricsOffline"));
			}
		}
		// Check for updates
		if(config.getUseUpdater())
		{
			Update();
		}
		// Load Global Messages
		Message_NoPermission	= lang.getReady("Ingame.NoPermission");
		Message_NotMarried		= lang.getReady("NotMarried");
		Message_PartnerOffline	= lang.getReady("Ingame.PartnerOffline");
		// Load Worker
		if(config.getChatGlobal())
		{
			chat = new Chat(this);
		}
		if(config.getHomeGlobal())
		{
			home = new Home(this);
		}
		if(config.getTPGlobal())
		{
			tp = new TP(this);
		}
		// Register Listener
		listener = new EventListener(this);
		getProxy().getPluginManager().registerListener(this, listener);
		getProxy().registerChannel("MarriageMaster");
		// Register Commands
			// We dont have any commands that should only be executed on the bungee, so we use the chat event to catch them and use our own chat worker
			// Register sub Commands for /marry
		if(config.getChatGlobal())
		{
			listener.RegisterMarrySubcommand(chat, "c", "chat", "chattoggle", config.getChatToggleCommand(), "listenchat");
		}
		listener.RegisterMarrySubcommand(new Update(this), "update");
		listener.RegisterMarrySubcommand(new Reload(this), "reload");
		if(config.getHomeGlobal())
		{
			listener.RegisterMarrySubcommand(home, "home");
		}
		if(config.getTPGlobal())
		{
			listener.RegisterMarrySubcommand(tp, "tp");
		}
    }
    
    public void doReload(final ProxiedPlayer sender)
    {
    	getProxy().getScheduler().schedule(this, new Runnable() {
			@Override
			public void run()
			{
				Disable();
				PluginLoad();
				broadcastPluginMessage("reload"); // Send reload through plugin channel to all servers
				sender.sendMessage(new TextComponent(ChatColor.BLUE + "Reloaded!"));
			}}, 1L, TimeUnit.SECONDS);
    }
    
    public void Disable()
    {
    	DB.Disable();
    	DB = null;
    	lang = null;
    	config = null;
    	listener = null;
    	getProxy().getPluginManager().unregisterListeners(this);
    	getProxy().getPluginManager().unregisterCommands(this);
    }
	 
	public void onDisable()
	{
		String disabled = lang.getString("Console.Disabled");
		// Check for updates
		if(config.getUseUpdater())
		{
			Update();
		}
		Disable();
		log.info(disabled);
	}
	
	public void broadcastPluginMessage(String message)
	{
		byte[] outba = null;
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(stream);
	        out.writeUTF(message);
	        out.flush();
	        outba = stream.toByteArray();
	        out.close();
		}
		catch (Exception e)
		{
			log.warning("Faild sending message!");
			e.printStackTrace();
		}
		Set<Entry<String, ServerInfo>> serverlist = getProxy().getServers().entrySet();
		for(Entry<String, ServerInfo> e : serverlist)
		{
			e.getValue().sendData("MarriageMaster", outba, true);
		}
	}
	
	public void sendPluginMessage(String message, ServerInfo server)
	{
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
	        DataOutputStream out = new DataOutputStream(stream);
	        out.writeUTF(message);
	        out.flush();
			server.sendData("MarriageMaster", stream.toByteArray(), true);
			out.close();
		}
		catch (Exception e)
		{
			log.warning("Faild sending message to server: " + server.getName());
			e.printStackTrace();
		}
    }
	
	public boolean Update()
	{
		Bungee_Updater updater = new Bungee_Updater(this, 74734, this.getFile(), UpdateType.DEFAULT, true);
		if(updater.getResult() == UpdateResult.SUCCESS)
		{
			return true;
		}
		return false;
	}
}