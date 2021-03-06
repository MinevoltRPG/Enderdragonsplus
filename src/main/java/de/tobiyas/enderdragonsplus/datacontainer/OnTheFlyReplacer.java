package de.tobiyas.enderdragonsplus.datacontainer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedED;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragonVersionManager;

public class OnTheFlyReplacer implements Runnable {

	private EnderdragonsPlus plugin;
	
	public OnTheFlyReplacer(){
		plugin = EnderdragonsPlus.getPlugin();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 10);
	}
	
	@Override
	public void run() {
		if(plugin.interactConfig().getConfig_replaceOnTheFly()){
			for(World world : Bukkit.getWorlds()){
				for(Entity entity : world.getEntities()){
					if(entity.getType() != EntityType.ENDER_DRAGON) continue;
					
					if(entity.isDead()) continue;
					UUID entityID = entity.getUniqueId();
					if(plugin.interactBridgeController().isSpecialDragon((LivingEntity)entity)) continue;
					if(entityID == null) continue;
					boolean isEDPDragon = plugin.getContainer().getDragonById(entityID) != null;
					if(isEDPDragon) continue;
					
					Location loctaion = entity.getLocation();
					entity.remove();
					boolean worked = spawnLimitedEnderDragon(loctaion, entityID) != null;
					if(plugin.interactConfig().getConfig_debugOutput())
						plugin.log("Replaced dragon: " + entityID + " worked: " + worked);
				}
			}
		}
	}
	
	private LimitedED spawnLimitedEnderDragon(Location location, UUID uuid){
		LimitedED dragon = LimitedEnderDragonVersionManager.generate(location);
		dragon.spawn();
		
		dragon.changeUUID(uuid);
		//dragon.setHealth((float) plugin.interactConfig().getConfig_dragonHealth());
		return dragon;
	}

}
