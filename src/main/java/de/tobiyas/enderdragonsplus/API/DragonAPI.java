package de.tobiyas.enderdragonsplus.API;

import java.util.UUID;

import net.minecraft.server.v1_4_R1.World;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;

public class DragonAPI {

	
	/**
	 * @param dragon
	 * @param target
	 * @return boolean if it worked
	 */
	public static boolean setTarget(LivingEntity dragon, LivingEntity target){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		
		LimitedEnderDragon lDragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getUniqueId());
		if(lDragon == null) return false;
		lDragon.setTarget(target);
		
		return true;
	}
	
	/**
	 * @param dragon
	 * @param player
	 * @return boolean if it worked
	 */
	public static boolean setTarget(LivingEntity dragon, Player player){
		return setTarget(dragon, (LivingEntity) player);
	}
	
	/**
	 * @param dragon
	 * @param location
	 * @return boolean if it worked
	 */
	public static boolean setTarget(LivingEntity dragon, Location location){
		LimitedEnderDragon LEdragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getUniqueId());
		if(LEdragon == null)
			return false;
		
		LEdragon.goToLocation(location);
		return true;
	}
	
	/**
	 * @param dragon
	 * @return boolean if it worked
	 */
	public static boolean sendHome(LivingEntity dragon){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		
		try{
			UUID id = dragon.getUniqueId();
			EnderdragonsPlus.getPlugin().getContainer().setFlyingHome(id, true);
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param dragon
	 * @param location
	 * @return
	 */
	public static boolean setNewHome(LivingEntity dragon, Location location){
		if(!(dragon.getType() == EntityType.ENDER_DRAGON)) return false;
		UUID id = dragon.getUniqueId();
		return EnderdragonsPlus.getPlugin().getContainer().setHome(id, location);
	}
	
	/**
	 * @param dragon
	 * @param location
	 * @return boolean if it worked
	 */
	public static boolean setNewHomeAndGoTo(LivingEntity dragon, Location location){
		if(!setNewHome(dragon, location)) return false;
		return sendHome(dragon);
	}
	
	/**
	 * @param location
	 * @return
	 */
	public static LivingEntity spawnNewEnderdragon(Location location){
		return spawnNewEnderdragon(location, "Normal");
	}
	
	/**
	 * @param location
	 * @return
	 */
	public static LivingEntity spawnNewEnderdragon(Location location, String ageName){
		World world = ((CraftWorld)location.getWorld()).getHandle();
		LimitedEnderDragon dragon = new LimitedEnderDragon(location, world, ageName);
		dragon.spawn(false);
		
		if(dragon.getBukkitEntity() == null)
			return null;
			
		dragon.setHealth(EnderdragonsPlus.getPlugin().interactConfig().getConfig_dragonHealth());
		return (LivingEntity)dragon.getBukkitEntity();
	}
	
	/**
	 * @param dragon
	 * @param property
	 * @param value
	 * @return boolean if it worked
	 */
	public static boolean setPropertyToDragon(LivingEntity dragon, String property, Object value){
		LimitedEnderDragon LEdragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getUniqueId());
		if(LEdragon == null)
			return false;
		
		UUID dragonID = LEdragon.getBukkitEntity().getUniqueId();
		EnderdragonsPlus.getPlugin().getContainer().setProperty(dragonID, property, value);
		return true;
	}
	
	/**
	 * @param dragon
	 * @param property
	 * @return Object found by property
	 */
	public static Object getPropertyToDragon(LivingEntity dragon, String property){
		LimitedEnderDragon LEdragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getUniqueId());
		if(LEdragon == null)
			return null;
		
		UUID dragonID = LEdragon.getBukkitEntity().getUniqueId();
		return EnderdragonsPlus.getPlugin().getContainer().getProperty(dragonID, property);
	}
	
	/**
	 * Spits a fireball on the target Entity
	 * 
	 * @param dragon
	 * @param target
	 * @return if it worked
	 */
	public static boolean spitFireballOnTarget(LivingEntity dragon, Entity target){
		LimitedEnderDragon LEdragon = EnderdragonsPlus.getPlugin().getContainer().getDragonById(dragon.getUniqueId());
		if(LEdragon == null)
			return false;
		
		return LEdragon.spitFireBallOnTarget((net.minecraft.server.v1_4_R1.Entity) target);
	}
}
