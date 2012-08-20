package de.javakara.manf.mcdefrag.listener;

import java.util.ArrayList;
import java.util.List;

import de.javakara.manf.mcdefrag.Language;
import de.javakara.manf.mcdefrag.Config;
import de.javakara.manf.mcdefrag.api.manager.PlayerManager;
import de.javakara.manf.mcdefrag.api.manager.RegionManager;
import de.javakara.manf.mcdefrag.api.Region;
import de.javakara.manf.mcdefrag.api.ResponseRegion;
import de.javakara.manf.mcdefrag.api.PlayerObserver;
import de.javakara.manf.mcdefrag.api.ShedulerFactory;
import de.javakara.manf.util.LanguageComplete;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@LanguageComplete
public class PlayerListener implements Listener {	
	static ArrayList<String> allowedWorlds = new ArrayList<String>();
	
	static {
		allowedWorlds.add("world");
	}


	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void setRegionA(final BlockBreakEvent event) {
		Player p = event.getPlayer();
		String name = p.getName();
		Block b = event.getBlock();
		Location loc = b.getLocation();
		String[] respond = PlayerManager.updateSelection(name, 0,loc);
		
		if(respond != null){
			event.setCancelled(true);
			p.sendMessage(respond);
		}else{
			if(b.getTypeId() == 14){
				if(p.hasPermission("defrag.admin.delete")){
					return;
				}
				if(RegionManager.isProtected(b)){
					event.setCancelled(true);
					p.sendMessage(Language.get("region.protected")); 
				}else{
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void setRegionB(final PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Location loc = event.getClickedBlock().getLocation();
		Player p = event.getPlayer(); 
		String name = p.getName();
		String[] respond = PlayerManager.updateSelection(name, 1, loc);

		if(respond != null){
			event.setCancelled(true);
			p.sendMessage(respond);
		}else{
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void unregisterOnLeave(final PlayerQuitEvent event) {
		ShedulerFactory.unregister(event.getPlayer().getName());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void checkRegions(final PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (event.isCancelled()) {
			return;
		}
		String world = p.getWorld().getName();
		if (!allowedWorlds.contains(world)) {
			return;
		}
		Location from = event.getFrom();
		Location to = event.getTo();
		if (from.getBlockX() == to.getBlockX()) {
			if (from.getBlockZ() == to.getBlockZ()) {
				return;
			}
		}
		if (!p.hasPermission("defrag.user.join")) {
			return;
		}
		if (PlayerManager.isPlaying(p.getName())) {
			return;
		}
		if (to.getBlock().getTypeId() == 0) {
			to = to.subtract(0, 1, 0);
			if (to.getBlock().getTypeId() == 0) {
				return;
			}
		}
		//p.sendMessage("Block: " + to.getBlock().getTypeId());
		if (to.getBlock().getTypeId() == 14) {
			ResponseRegion response = new ResponseRegion();
			if (RegionManager.areaCheck(world, to.getBlockX(), to.getBlockY(), to.getBlockZ(),response)) {
				Region r = response.getResponse();
				String rname = r.getName();
				if(RegionManager.isBlocked(rname)){
					p.sendMessage(Language.get("region.blocked"));
					return;
				}else{
					RegionManager.addPlayingPlayer(r.getName(),p.getName());
					
					p.sendMessage(Language.get("region.start"));
					PlayerObserver obs = new PlayerObserver(p, RegionManager.getMaxTime(rname), r);
					obs.setInterval(Config.getInt("sendtime") *1000);
					int i = ShedulerFactory.newSheduler(obs, 0, 1);
					ShedulerFactory.register(i,p.getName());
				}
			
			} else {
				return;
			}
		} else {
			return;
		}

	}
	
	public static void addWorld(List<String> list){
		allowedWorlds.addAll(list);
	}
}