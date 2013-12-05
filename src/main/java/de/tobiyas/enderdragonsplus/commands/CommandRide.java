package de.tobiyas.enderdragonsplus.commands;

import java.util.List;






import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.tobiyas.enderdragonsplus.EnderdragonsPlus;
import de.tobiyas.enderdragonsplus.API.DragonAPI;
import de.tobiyas.enderdragonsplus.entity.dragon.LimitedEnderDragon;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.DragonMoveController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.FireballController;
import de.tobiyas.enderdragonsplus.entity.dragon.controllers.TargetController;
import de.tobiyas.enderdragonsplus.permissions.PermissionNode;

public class CommandRide implements CommandExecutor {

private EnderdragonsPlus plugin;
	
	public CommandRide(){
		plugin = EnderdragonsPlus.getPlugin();
		try{
			plugin.getCommand("edpride").setExecutor(this);
		}catch(Exception e){
			plugin.log("Could not register command: /spawnenderdragon");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}
		
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.ride)){
			return true;
		}
		
		int speed = 1;
		
		if(args.length > 0){
			try{
				speed = Integer.parseInt(args[0]);
			}catch(NumberFormatException exp){}
		}
		
		
		Player player = (Player) sender;
		LivingEntity entity = DragonAPI.spawnNewEnderdragon(player.getLocation());
		LimitedEnderDragon dragon = DragonAPI.getDragonByEntity(entity);
		entity.setPassenger(player);
		dragon.setDragonMoveController(new DumbMoveController(dragon, player, speed));
		dragon.setFireballController(new PlayerFireFireballController(dragon.getTargetController(), player));
		
		player.sendMessage(ChatColor.GREEN + "Mounted you on a dragon.");
		
		return true;
	}
	
	
	/**
	 * A simple Move controller forwarding the Player move directions.
	 * 
	 * @author Tobiyas
	 *
	 */
	private class DumbMoveController extends DragonMoveController{

		/**
		 * Speed of the Dragon
		 */
		private final int speed;
		
		
		public DumbMoveController(LimitedEnderDragon dragon, Player player, int speed) {
			super(dragon);
			
			this.speed = speed;
		}

		
		@Override
		public void moveDragon() {
			//remove dragon when no passenger.
			if(dragon.passenger == null){
				dragon.getBukkitEntity().remove();
			}

			for(int i = 0; i < speed; i++){
				dragon.e(0.89f, 0.89f);
			}
		}
		
	}
	
	/**
	 * The Fireball Controller listening to stuff.
	 * 
	 * @author Tobiyas
	 *
	 */
	private class PlayerFireFireballController extends FireballController implements Listener{

		/**
		 * The Player
		 */
		private final Player player;
		
		
		public PlayerFireFireballController(TargetController targetController, Player player) {
			super(targetController);
			
			this.player = player;
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
		
		
		@EventHandler
		public void fireFireball(PlayerDropItemEvent event){
			if(event.getPlayer() != player){
				return;
			}
			
			if(!plugin.getPermissionManager().checkPermissionsSilent(player, PermissionNode.shootFireballs)){
				return;
			}
			
			LimitedEnderDragon dragon = targetController.getDragon();
			if(dragon == null || !dragon.isAlive()){
				HandlerList.unregisterAll(this);
				return;
			}
			
			event.setCancelled(true);
			List<Block> locs = player.getLineOfSight(null, 200);
			for(Block block : locs){
				if(block.getType() != Material.AIR){
					dragon.spitFireBallOnTarget(block.getLocation());
					break;
				}
			}
		}
		
		@EventHandler
		public void playerQuit(PlayerQuitEvent event){
			if(event.getPlayer() == player){
				targetController.getDragon().remove();
				HandlerList.unregisterAll(this);
			}
			
		}

		@EventHandler
		public void playerKicked(PlayerKickEvent event){
			if(event.getPlayer() == player){
				targetController.getDragon().remove();
				HandlerList.unregisterAll(this);
			}
			
		}

	}

}