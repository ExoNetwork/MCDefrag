package de.javakara.manf.mcdefrag;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.javakara.manf.mcdefrag.api.Region;
import de.javakara.manf.mcdefrag.api.manager.PlayerManager;
import de.javakara.manf.mcdefrag.api.manager.RegionManager;
import de.javakara.manf.util.LanguageComplete;
import de.javakara.manf.util.SpacerReplace;
import de.javakara.manf.util.TimeUtilis;

@LanguageComplete
public class DefragCommands implements CommandExecutor {
	final String track = "%track%";
	final String time = "%time%";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,String[] args) {
		if(args.length >= 1 && args[0].startsWith("reload")){
			if (!sender.hasPermission("defrag.admin.reload")) {
				return permissionsDenied((Player) sender);
			}
			if(args[0].endsWith("config")){
				Config.reload();
				return true;
			}else if(args[0].endsWith("language")){
				Language.reload(MCDefrag.getJarLang());
				return true;
			}
		}
		
		if (sender instanceof ConsoleCommandSender) {
			System.out.println(Language.get("consoleunspported")[0]);
			return true;
		}
		Player p = (Player) sender;
		String name = p.getName();
		
		if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
			help(p);
		}
		
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("toggle")) {
				if (p.hasPermission("defrag.admin.create")) {
					String toggle = PlayerManager.toogleSelection(name);
					p.sendMessage(Language.get(toggle));
					return true;
				} else {
					return permissionsDenied(p);
				}
			} else if (args[0].equalsIgnoreCase("create")) {
				if (!p.hasPermission("defrag.admin.create")) {
					return permissionsDenied(p);
				}
				if (args.length >= 2) {
					Region r = PlayerManager.playerSelectionToRegion(name);
					if (r == null) {
						p.sendMessage(Language.get("region.invalid"));
						return true;
					}
					RegionManager.addRegion(args[1], r);
					try {
						RegionManager.save();
					} catch (IOException e) {
						e.printStackTrace();
					}
					SpacerReplace sr = new SpacerReplace();
					sr.addSpacer(track, args[1]);
					p.sendMessage(getArgumentString("create.created", sr));
					PlayerManager.clearSelection(name);
					return true;
				} else {
					p.sendMessage(Language.get("create.invalid"));
					return true;
				}

			} else if (args[0].equalsIgnoreCase("finish")) {
				if (!p.hasPermission("defrag.admin.create")) {
					return permissionsDenied(p);
				}
				if (args.length >= 2) {
					Region r = PlayerManager.playerSelectionToRegion(name);
					if (r == null) {
						p.sendMessage(Language.get("region.invalid"));
						return true;
					}
					RegionManager.setEndRegion(args[1], r);
					SpacerReplace sr = new SpacerReplace();
					sr.addSpacer(track, args[1]);
					p.sendMessage(getArgumentString("finish.created", sr));
					PlayerManager.clearSelection(name);
					return true;
				} else {
					p.sendMessage(Language.get("finish.invalid"));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				showList(p);
				return true;
			} else if (args[0].equalsIgnoreCase("top")) {
				if (!p.hasPermission("defrag.user.top")) {
					return permissionsDenied(p);
				}
				if (args.length >= 2) {
					p.sendMessage(RegionManager.getHighscore(args[1]));
					return true;
				} else {
					showList(p);
					return true;
				}
			} else if (args[0].equalsIgnoreCase("time")) {
				if (!p.hasPermission("defrag.admin.create")) {
					return permissionsDenied(p);
				}
				if (args.length >= 3) {
					if (!is_numeric(args[2])) {
						p.sendMessage(Language.get("time.invalid"));
						return true;
					}

					int multi = 1000;
					if (args.length >= 4) {
						if (args[3].equalsIgnoreCase("m")) {
							multi = 1000 * 60;
						} else if (args[3].equalsIgnoreCase("s")) {
							multi = 1000;
						} else if (args[3].equalsIgnoreCase("ms")) {
							multi = 1;
						}
					}
					Long timelong = Long.valueOf(args[2]) * multi;
					RegionManager.setTime(args[1], timelong);
					SpacerReplace sr = new SpacerReplace();
					String realtime = TimeUtilis.getFormattedMinutes(timelong,
							1000);
					sr.addSpacer(time, realtime);
					p.sendMessage(getArgumentString("time.changed", sr));
					return true;
				} else {
					p.sendMessage(Language.get("commands.help.time"));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("reset")) {
				if (args.length == 1 && PlayerManager.isPlaying(p.getName())) {
					if (!p.hasPermission("defrag.user.reset")) {
						return permissionsDenied(p);
					}
					RegionManager.removePlayingPlayer(p.getName());
					p.sendMessage(Language.get("reset.user"));
					return true;
				} else {
					if(args.length == 1){
						p.sendMessage(Language.get("reset.notplaying"));
						return true;
					}else{
						Player tar = Bukkit.getPlayer(args[2]);
						if(tar != null){
							SpacerReplace sr = new SpacerReplace();
							sr.addSpacer("%target%", tar.getName());
							if(PlayerManager.isPlaying(tar.getName())){
								RegionManager.removePlayingPlayer(tar.getName());
								p.sendMessage(getArgumentString("reset.adminreset", sr));
								tar.sendMessage(Language.get("reset.user"));
								return true;
							}else{
								p.sendMessage(getArgumentString("reset.adminnotplaying", sr));
								return true;
							}
						}else{
							p.sendMessage(Language.get("reset.adminnotfound"));
							return true;
						}
						
					}
				}
			}else if(args[0].equalsIgnoreCase("clearrecords")){
				// clearrecords <name> <id|all>
				if (args.length == 2 || args[2].equalsIgnoreCase("all")) {
					if (!p.hasPermission("defrag.admin.top.clear.all")) {
						return permissionsDenied(p);
					}
					RegionManager.resetRegion(args[1]);
					SpacerReplace sr = new SpacerReplace();
					sr.addSpacer(track, args[1]);
					p.sendMessage(getArgumentString("reset.route", sr));
				}else if (args.length >= 3) {
					if(!p.hasPermission("defrag.admin.top.clear")){
						return permissionsDenied(p);
					}
					if (!is_numeric(args[2])) {
						p.sendMessage(Language.get("highscore.nonumber"));
						return true;
					}
					
					RegionManager.deleteHighscoreScore(args[1], Integer.valueOf(args[2])-1);
				}
			}else if (args[0].equalsIgnoreCase("lock")) {
				if(!p.hasPermission("defrag.admin.lock")){
					return permissionsDenied(p);
				}
				if (args.length >= 2) {
					if (RegionManager.disableRegion(args[1])) {
						p.sendMessage(Language.get("route.disabled"));
					} else {
						p.sendMessage(Language.get("route.noroute"));
					}
				} else {
					p.sendMessage(Language.get("commands.help.lock"));
					return true;
				}
			} else if (args[0].equalsIgnoreCase("unlock")) {
				if(!p.hasPermission("defrag.admin.lock")){
					return permissionsDenied(p);
				}
				if (args.length >= 2) {
					if (RegionManager.enableRegion(args[1])) {
						p.sendMessage(Language.get("route.enabled"));
					} else {
						p.sendMessage(Language.get("route.noroute"));
					}
				} else {
					p.sendMessage(Language.get("commands.help.unlock"));
					return true;
				}
			}
		}
		return true;
	}

	private void showList(Player p) {
		if (!p.hasPermission("defrag.user.list")) {
			permissionsDenied(p);
			return;
		}
		p.sendMessage(RegionManager.getRegions(p.getWorld().getName()));
	}

	private boolean permissionsDenied(Player p) {
		p.sendMessage(Language.get("permissionsdenied"));
		return true;
	}

	private boolean help(Player p){
		if(p.hasPermission("defrag.admin.create")){
			p.sendMessage("toggle");
			p.sendMessage("create");
			p.sendMessage("finish");
			p.sendMessage("time");
		}
		if(p.hasPermission("defrag.user.list")){
			p.sendMessage("list");
		}
		if(p.hasPermission("defrag.user.top")){
			p.sendMessage("top");
		}
		if(p.hasPermission("defrag.user.reset")){
			p.sendMessage("reset");
		}
		if(p.hasPermission("defrag.admin.top.clear.all")){
			p.sendMessage("admin reset all");
		}
		if(p.hasPermission("defrag.admin.top.clear")){
			p.sendMessage("admin reset single");
		}
		if(p.hasPermission("defrag.admin.lock")){
			p.sendMessage("admin lock");
			p.sendMessage("admin unlock");
		}
		return true;
	}
	
	private String[] getArgumentString(String node, SpacerReplace sr) {
		return MCDefrag.getArgumentString(node, sr);
	}

	private boolean is_numeric(String s) {
		for (char c : s.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
}