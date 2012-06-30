/*
 * StopEnderdragonPortals - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.enderdragonsplus;


import net.minecraft.server.EntityTypes;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;

import de.tobiyas.enderdragonsplus.bridges.BridgeController;
//import de.tobiyas.enderdragonsplus.commands.CommandDEBUGGOTO;
import de.tobiyas.enderdragonsplus.commands.CommandGoHome;
import de.tobiyas.enderdragonsplus.commands.CommandInfo;
import de.tobiyas.enderdragonsplus.commands.CommandKillEnderDragon;
import de.tobiyas.enderdragonsplus.commands.CommandLoadAll;
import de.tobiyas.enderdragonsplus.commands.CommandReloadConfig;
import de.tobiyas.enderdragonsplus.commands.CommandRespawner;
import de.tobiyas.enderdragonsplus.commands.CommandSpawnEnderDragon;
import de.tobiyas.enderdragonsplus.commands.CommandUnloadAll;
import de.tobiyas.enderdragonsplus.configuration.Config;
import de.tobiyas.enderdragonsplus.configuration.ConfigTemplate;
import de.tobiyas.enderdragonsplus.datacontainer.Container;
import de.tobiyas.enderdragonsplus.datacontainer.DragonLogicTicker;
import de.tobiyas.enderdragonsplus.datacontainer.OnTheFlyReplacer;
import de.tobiyas.enderdragonsplus.entity.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.listeners.Listener_Entity;
import de.tobiyas.enderdragonsplus.listeners.Listener_Plugins;
import de.tobiyas.enderdragonsplus.listeners.Listener_Sign;
import de.tobiyas.enderdragonsplus.listeners.Listener_World;
import de.tobiyas.enderdragonsplus.spawner.DragonSpawnerManager;
import de.tobiyas.util.debug.logger.DebugLogger;
import de.tobiyas.util.metrics.SendMetrics;
import de.tobiyas.util.permissions.PermissionManager;


public class EnderdragonsPlus extends JavaPlugin{
	private Logger log;
	private DebugLogger debugLogger;
	private PluginDescriptionFile description;

	private String prefix;
	private Config config;
	
	private PermissionManager permissionManager;
	private Container container;
	
	private BridgeController bridgeController;
	private DragonSpawnerManager dragonSpawnerManager;
	
	private static EnderdragonsPlus plugin;

	
	@Override
	public void onEnable(){
		plugin = this;
		log = Logger.getLogger("Minecraft");
		debugLogger = new DebugLogger(this);
		
		description = getDescription();
		prefix = "["+description.getName()+"] ";
		
		injectDragon();
		permissionManager = new PermissionManager(this);
		
		setupConfiguration();
		container = new Container();
		
		checkDepends();
		registerEvents();
		registerCommands();
		
		container.loadContainer();
		registerTasks();
		
		registerManagers();
		initMetrics();
		
		log(description.getFullName() + " fully loaded with: " + permissionManager.getPermissionsName());
	}
	
	private void injectDragon(){
		try
	    {
	      Method method = EntityTypes.class.getDeclaredMethod("a", new Class[] { Class.class, String.class, Integer.TYPE });
	      method.setAccessible(true);
	      method.invoke(
	    		  EntityTypes.class, new Object[] { 
	    	  		LimitedEnderDragon.class, 
	    	  		"LimitedEnderDragon", 
	    	  		Integer.valueOf(63) 
	    	  	});
	            
	    } catch (Exception e) {
	      log("Could not inject LimitedEnderDragon. Disabling Plugin.");
	      e.printStackTrace();
	      Bukkit.getPluginManager().disablePlugin(this);
	    }
	}
	
	private void checkDepends(){
		bridgeController = new BridgeController();
	}
	
	@Override
	public void onDisable(){
		container.saveContainer();
		dragonSpawnerManager.saveList();
		debugLogger.shutDown();
		log("disabled "+description.getFullName());

	}
	public void log(String message){
		log.info(prefix+message);
	}


	private void registerEvents(){
		new Listener_Entity();
		new Listener_World();
		new Listener_Plugins();
		new Listener_Sign();
	}
	
	private void registerCommands(){
		new CommandKillEnderDragon();
		new CommandSpawnEnderDragon();
		new CommandReloadConfig();
		new CommandGoHome();
		new CommandInfo();
		new CommandUnloadAll();
		new CommandLoadAll();
		new CommandRespawner();
		
		//new CommandDEBUGGOTO();
	}
	
	private void registerTasks(){
		new DragonLogicTicker();
		new OnTheFlyReplacer();
	}
	
	private void registerManagers(){
		dragonSpawnerManager = new DragonSpawnerManager();
		dragonSpawnerManager.init();
	}


	private void setupConfiguration(){
		config = new Config(this);
		ConfigTemplate template = new ConfigTemplate();
		if(template.isOldConfigVersion())
			template.writeTemplate();
	}
	
	private void initMetrics(){
		SendMetrics.sendMetrics(this, false);
	}

	
	public Config interactConfig(){
		return config;
	}
	
	public static EnderdragonsPlus getPlugin(){
		return plugin;
	}
	
	public PermissionManager getPermissionManager(){
		return permissionManager;
	}
	
	public Container getContainer(){
		return container;
	}
	
	public BridgeController interactBridgeController(){
		return bridgeController;
	}
	
	public DragonSpawnerManager getDragonSpawnerManager(){
		return dragonSpawnerManager;
	}
	
	public DebugLogger getDebugLogger(){
		return debugLogger;
	}

}
