package de.javakara.manf.mcdefrag.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.javakara.manf.mcdefrag.MCDefrag;
import de.javakara.manf.mcdefrag.Language;
import de.javakara.manf.mcdefrag.api.manager.RegionManager;
import de.javakara.manf.util.SpacerReplace;
import de.javakara.manf.util.TimeUtilis;
import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class PlayerObserver implements Runnable{
	private Player p = null;
	private long maxtime = 0;
	private long start = 0;
	private Location old = null;
	private Region end = null;
	private boolean enabled = false;
	private long lasttime = 0;
	private long interval = 0;
	
	public PlayerObserver(Player p,long max,Region end){
		this.p = p;
		start = System.currentTimeMillis();
		maxtime = start + max;
		this.end = end;
		enabled = true;
	}
	
	public void setInterval(long interval){
		this.interval = interval;
	}
	
	@Override
	public synchronized void run() {
		if(!enabled){
			return;
		}
		if(!(maxtime > System.currentTimeMillis())){
			ShedulerFactory.unregister(p.getName());
			RegionManager.removePlayingPlayer(p.getName());
			p.sendMessage(Language.get("player.toolong"));
			enabled = false;
			return;
		}
		if(lasttime < System.currentTimeMillis() && interval!=0){
			String time = TimeUtilis.getFormattedMinutes(System.currentTimeMillis() - start,1000);
			p.sendMessage(Language.getRaw("time.announce").replace("%time%", time));
			lasttime = System.currentTimeMillis() + interval;
		}
		Location pos = p.getLocation();
		if(old == null){
			old = pos;
		}
		
		if (pos.getBlockX() == old.getBlockX()) {
			if (pos.getBlockZ() == old.getBlockZ()) {
				return;
			}
		}
		
		if (pos.getBlock().getTypeId() == 0) {
			pos = pos.subtract(0, 1, 0);
			if (pos.getBlock().getTypeId() == 0) {
				return;
			}
		}
		
		old = pos;
		if (pos.getBlock().getTypeId() == 14) {
			if(end.playerIsInStartRegion(pos)){
				long time = System.currentTimeMillis() - start;
				SpacerReplace sr = new SpacerReplace();
				sr.addSpacer("%time%", TimeUtilis.getFormattedMinutes(time, 1000));
				p.sendMessage(MCDefrag.getArgumentString("player.won", sr));
				ShedulerFactory.unregister(p.getName());
				RegionManager.removePlayingPlayer(p.getName());
				if(p.hasPermission("defrag.user.competitor")){
					int rank = RegionManager.addStats(end.getName(),p.getName(),time);
					if(rank == 1){
						RegionManager.newRecord(p,end.getName(), time);
					}
				}else{
					p.sendMessage(Language.get("player.nosave"));
				}
				
				enabled = false;
			} else {
				return;
			}
		} else {
			return;
		}
	}
}
