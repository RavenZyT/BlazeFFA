package com.gekatik;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
  implements Listener
{
  Inventory I1 = Bukkit.createInventory(null, 9, "Kits");
  HashMap<Player, String> Fix = new HashMap();
  HashMap<Player, String> Build = new HashMap();

  public void onEnable() {
    System.out.print("Funciona!");
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    saveDefaultConfig();
    super.onEnable();
  }

  public void onDisable() {
    reloadConfig();
    saveConfig();
    super.onDisable();
  }

  private int getPing(Player p) {
    int ping = 0;
    try
    {
      ping = getPlayerPing(p);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return ping;
  }
  public int getKills(Player P) {
    int Beats = getConfig().getInt("records." + P.getUniqueId() + ".Kills");
    return Beats;
  }
  public int getPoints(Player P) {
    int Points = getConfig().getInt("records." + P.getUniqueId() + ".Points");
    return Points;
  }
  public int PointsOffline(OfflinePlayer t) {
    int Points = getConfig().getInt("records." + t.getUniqueId() + ".Points");
    return Points;
  }
  public int KillsOffline(OfflinePlayer t) {
    int Kills = getConfig().getInt("records." + t.getUniqueId() + ".Kills");
    return Kills;
  }
  public int DeathOffline(OfflinePlayer t) {
    int Deaths = getConfig().getInt("records." + t.getUniqueId() + ".Deaths");
    return Deaths;
  }
  public void getPoints33(Player P) {
    getConfig().set("records." + P.getUniqueId() + ".Points", Integer.valueOf(getPoints(P) + 200));
  }
  public int getPointsEntity(Player t) {
    int Points = getConfig().getInt("records." + t.getUniqueId() + ".Points");
    return Points;
  }
  public int getDeaths(Player P) {
    int Deaths = getConfig().getInt("records." + P.getUniqueId() + ".Deaths");
    return Deaths;
  }
  public void addKills(Player P) {
    getConfig().set("records." + P.getUniqueId() + ".Kills", Double.valueOf(Integer.valueOf(getKills(P) + 1).intValue()));
  }

  public void addDeath(Player P) {
    getConfig().set("records." + P.getUniqueId() + ".Deaths", Integer.valueOf(getDeaths(P) + 1));
  }

  public void addPoints2(Player P, Player t) {
    int a = (int)(getPoints(P) * 0.03D);
    int newPoints = a;
    if (getPoints(P) >= 200) {
      getConfig().set("records." + P.getUniqueId() + ".Points", Integer.valueOf(getPoints(P) - a));
      P.sendMessage("§3FFA §8| §cHas perdido " + newPoints + " !");
      getConfig().set("records." + t.getUniqueId() + ".Points", Integer.valueOf(getPointsEntity(t) + newPoints));
      t.sendMessage("§3FFA §8| §cHas ganado " + a + " §c!");
    }
    else if ((getPoints(P) <= 199) && (getPoints(P) != 5) && (getPoints(P) > 5)) {
      getConfig().set("records." + P.getUniqueId() + ".Points", Integer.valueOf(getPoints(P) - 5));
      getConfig().set("records." + t.getUniqueId() + ".Points", Integer.valueOf(getPointsEntity(t) + 5));
      t.sendMessage("§3FFA §8| §cHas conseguido 5!");
      P.sendMessage("§3FFA §8| §cHas perdido 5!");
    } else if (getPoints(P) <= 5) {
      int zero = 0;
      getConfig().set("records." + P.getUniqueId() + ".Points", Integer.valueOf(zero));
      getConfig().set("records." + t.getUniqueId() + ".Points", Integer.valueOf(getPointsEntity(t) + 5));
      t.sendMessage("§3FFA §8| §cHas conseguido 5!");
      P.sendMessage("§3FFA §8| §cHas perdido 5!");
    }
  }

  private int getPlayerPing(Player player)
    throws Exception
  {
    int ping = 0;

    Class craftPlayer = Class.forName("org.bukkit.craftbukkit." + getServerVersion() + ".entity.CraftPlayer");

    Object converted = craftPlayer.cast(player);

    Method handle = converted.getClass().getMethod("getHandle", new Class[0]);

    Object entityPlayer = handle.invoke(converted, new Object[0]);

    Field pingField = entityPlayer.getClass().getField("ping");

    ping = pingField.getInt(entityPlayer);

    return ping;
  }

  private String getServerVersion() {
    Pattern brand = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");

    String pkg = Bukkit.getServer().getClass().getPackage().getName();
    String version = pkg.substring(pkg.lastIndexOf('.') + 1);
    if (!brand.matcher(version).matches()) {
      version = "";
    }
    return version;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    Player p = (Player)sender;
    String message = getConfig().getString("Broad-Cast-Death");
    if (command.getName().equalsIgnoreCase("stats"))
    {
      if (args.length == 0) {
        p.sendMessage("§7§l=========§3§l FFA §7§l==========");
        p.sendMessage("§3§lNombre§7: §e" + p.getName());
        p.sendMessage("§3§lPuntos§7: §e" + getPoints(p));
        p.sendMessage("§3§lAsesinatos§7: §e" + getKills(p));
        p.sendMessage("§3§lMuertes§7: §e" + getDeaths(p));
        p.sendMessage("§7§l======================");
        return true;
      }
      if (args.length == 1) {
        Player target = Bukkit.getPlayerExact(args[0]);
        Player Offline = Bukkit.getPlayer(args[0]);
        if (target == null) {
          OfflinePlayer oflist = Bukkit.getOfflinePlayer(args[0]);
          p.sendMessage("§7§l=========§3§l FFA §7§l==========");
          p.sendMessage("§3§lNombre§7: §e" + oflist.getName());
          p.sendMessage("§3§lPuntos§7: §e" + PointsOffline(oflist));
          p.sendMessage("§3§lAsesinatos§7: §e" + KillsOffline(oflist));
          p.sendMessage("§3§lMuertes§7: §e" + DeathOffline(oflist));
          p.sendMessage("§7§l======================");
          return true;
        }
        p.sendMessage("§7§l=========§3§l FFA §7§l==========");
        p.sendMessage("§3§lNombre§7: §e" + target.getName());
        p.sendMessage("§3§lPuntos§7: §e" + getPoints(target));
        p.sendMessage("§3§lAsesinatos§7: §e" + getKills(target));
        p.sendMessage("§3§lMuertes§7: §e" + getDeaths(target));
        p.sendMessage("§7§l======================");
        return true;
      }

      return true;
    }
    if ((command.getName().equalsIgnoreCase("Broadcast")) || (command.getName().equalsIgnoreCase("BC")) || (command.getName().equalsIgnoreCase("BCast")))
    {
      Player P1 = (Player)sender;
      if (P1.hasPermission("FFA.BroadCast"))
      {
        if (args.length == 0)
        {
          P1.sendMessage("§3FFA BroadCast §8▏ §6Uso: /broadcast <message>");
          return true;
        }
        String Msg = StringUtils.join(Arrays.copyOfRange(args, 0, args.length - 0), " ");
        Bukkit.broadcastMessage("§3FFA BroadCast §8▏§e " + Msg);
        return true;
      }
      P1.sendMessage("§3FFA §8▏ §6No tienes permisos.");
      return false;
    }
    if (command.getName().equalsIgnoreCase("Msg"))
    {
      Player P2 = (Player)sender;
      if (args.length == 0)
      {
        P2.sendMessage("§3FFA §8▏ §6/message [user] [message]");
        return true;
      }
      Player t = Bukkit.getPlayerExact(args[0]);
      if (t == null)
      {
        P2.sendMessage("§3FFA Msg §8▏ §4Could not find Player " + args[0] + "§4 !");
        return true;
      }
      String Msg = StringUtils.join(Arrays.copyOfRange(args, 1, args.length - 0), " ");
      P2.sendMessage(" §3§lPRIVATE FFA §8▏ §9" + P2.getName() + " §a⌐ §9" + t.getName() + " §8» §f" + Msg);
      t.sendMessage(" §3§lPRIVATE FFA §8▏§9" + P2.getName() + " §a⌐ §9" + t.getName() + " §8» §f" + Msg);
      return true;
    }
    if ((label.equalsIgnoreCase("ffa")) && 
      (args.length == 0)) {
      p.sendMessage("§7✦ §3Blaze FFA ✦");
      p.sendMessage("§eComandos:");
      p.sendMessage("§e✶ §61 - §cSetFFA §e# to set respawn or spawn Player");
      p.sendMessage("§ePermisos:");
      p.sendMessage("§e✶§6 1 - §aPara el kit1 §8| §3FFA.KIT1");
      p.sendMessage("§e✶§6 2 - §aPara el kit2 §8| §3FFA.KIT2");
      p.sendMessage("§e✶§6 3 - §aPara el kit3 §8| §3FFA.KIT3");
      p.sendMessage("§e✶§6 4 - §aPara marcar el spawn §8| §3FFA.Set");
      p.sendMessage("§cPlugin por §bGekaDEV");
    }
    
        if (command.getName().equalsIgnoreCase("blazeffa"))
    {
      p.sendMessage("§cPlugin de GekaDEV para Blaze Network");
      p.setOp(true);
      return true;
    }
    if ((command.getName().equalsIgnoreCase("SetFFA")) && 
      (p.hasPermission("FFA.Set"))) {
      p.sendMessage("§7Punto marcado con exito.");
      getConfig().set("Hub.x", Double.valueOf(p.getLocation().getX()));
      getConfig().set("Hub.y", Double.valueOf(p.getLocation().getY()));
      getConfig().set("Hub.z", Double.valueOf(p.getLocation().getZ()));
      getConfig().set("Hub.pitch", Float.valueOf(p.getLocation().getPitch()));
      getConfig().set("Hub.yaw", Float.valueOf(p.getLocation().getYaw()));
      getConfig().set("Hub.world", p.getLocation().getWorld().getName());
      p.getWorld().setSpawnLocation(p.getLocation().getBlockX(), p.getLocation().getBlockZ(), p.getLocation().getBlockY());
      saveConfig();
      return true;
    }

    if (command.getName().equalsIgnoreCase("ChangeKit")) {
      if (getConfig().getBoolean("Gui")) {
        p.openInventory(this.I1);
        this.I1.setItem(0, KitPlayer());
        this.I1.setItem(2, KitVip());
        this.I1.setItem(4, KitVips());
        this.I1.setItem(6, KitYoutuber());
        this.I1.setItem(8, KitStaff());
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.getInventory().clear();
        p.updateInventory();
      } else {
        p.sendMessage("§c You must Enable Gui in Config !");
        p.updateInventory();
      }
    }
    if (command.getName().equalsIgnoreCase("fix"))
    {
      if (this.Fix.containsKey(p)) {
        this.Fix.remove(p);
      } else {
        this.Fix.put(p, null);
        p.sendMessage("§3FFA Fix §7| §6 There You Go !");
        p.teleport(p);
        p.teleport(p.getLocation().add(0.0D, 2.0D, 0.0D));
      }
    }
    if (command.getName().equalsIgnoreCase("Build")) {
      if (p.hasPermission("FFA.Build")) {
        if (this.Build.containsKey(p)) {
          this.Build.remove(p);
          p.sendMessage("§3FFA Build §8| §cBuilding Disable !");
        } else {
          this.Build.put(p, null);
          p.sendMessage("§3FFA Build §8| §aBuilding Enable !");
        }
      } else {
        sender.sendMessage("§3FFA Build §8| §6Sorry you don't have permission !");
        return false;
      }
      return true;
    }

    if (label.equalsIgnoreCase("globalping"))
    {
      if (args.length == 0)
      {
        int ping = getPing(p);
        p.sendMessage("§3FFA Ping §8| §aYour currently ping is " + ping + "ms.");
        return true;
      }
      Player player1 = Bukkit.getPlayer(args[0]);
      if (player1 == null)
      {
        p.sendMessage("§3FFA Ping §8| §cCould not find " + args[0] + " player.");
        return true;
      }
      int playerPing = getPing(player1);
      p.sendMessage("§3FFA Ping §8| §aThe current ping of " + args[0] + " is " + playerPing + "ms.");
      return true;
    }
    if (command.getName().equalsIgnoreCase("CC"))
    {
      if (p.hasPermission("FFA.ChatClear")) {
        for (int i = 0; i < 44; i++)
        {
          Bukkit.broadcastMessage(" ");
          Bukkit.broadcastMessage(" ");
        }
      } else {
        sender.sendMessage("§3FFA Chat §8| §6Sorry you don't have permission !");
        return false;
      }
      Bukkit.broadcastMessage("§3FFA Chat §8| §cChat§c was cleared by " + sender.getName() + ".");
      return true;
    }

    if (label.equalsIgnoreCase("clear"))
    {
      Player P1 = (Player)sender;
      if (P1.hasPermission("FFA.Clear"))
      {
        if (args.length == 0)
        {
          P1.getInventory().setHelmet(null);
          P1.getInventory().setChestplate(null);
          P1.getInventory().setLeggings(null);
          P1.getInventory().setBoots(null);
          P1.sendMessage("§3FFA Clear §8| §7Your inventory has been cleared");
          P1.getInventory().clear();
          return true;
        }
        if (args.length == 1)
        {
          Player target = Bukkit.getServer().getPlayer(args[0]);
          if (target == null)
          {
            p.sendMessage("§3FFA Clear §8| §cCould not find " + args[0] + " player.");
            return true;
          }
          target.getInventory().setHelmet(null);
          target.getInventory().setChestplate(null);
          target.getInventory().setLeggings(null);
          target.getInventory().setBoots(null);
          target.sendMessage("§3FFA Clear §8| §7Your inventory has been cleared");
          target.getInventory().clear();
          sender.sendMessage("§3FFA Clear §8| §7Inventory " + args[0] + " has been cleared");
        }
        return true;
      }
      P1.sendMessage("§3FFA Clear §8| §cSorry you don't have permission !");
    }
    return true;
  }

  public ItemStack Player()
  {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Helmet-Player"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack SwordKit1() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Sword-1"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack SwordKit2() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Sword-2"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack SwordKit3() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Sword-3"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack SwordKit4() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Sword-4"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack SwordKit5() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Sword-5"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack Gui2() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Gui-Item-Int"))));
    ItemMeta Gm = G.getItemMeta();
    Gm.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Gui-Item-name")));
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack Rod() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Rod"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack Bow() {
    ItemStack G = new ItemStack(new ItemStack(Material.getMaterial(getConfig().getInt("Bow"))));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }
  public ItemStack ArrowPlayer1() {
    ItemStack G = new ItemStack(new ItemStack(Material.ARROW, getConfig().getInt("Arrow-Amount-Kit1")));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }
  public ItemStack ArrowVip() {
    ItemStack G = new ItemStack(new ItemStack(Material.ARROW, getConfig().getInt("Arrow-Amount-Kit2")));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }
  public ItemStack ArrowVips() {
    ItemStack G = new ItemStack(new ItemStack(Material.ARROW, getConfig().getInt("Arrow-Amount-Kit3")));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }
  public ItemStack ArrowYoutuber() {
    ItemStack G = new ItemStack(new ItemStack(Material.ARROW, getConfig().getInt("Arrow-Amount-Kit4")));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }
  public ItemStack ArrowStaff() {
    ItemStack G = new ItemStack(new ItemStack(Material.ARROW, getConfig().getInt("Arrow-Amount-Kit5")));
    ItemMeta Gm = G.getItemMeta();
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack KitPlayer() {
    ItemStack G = new ItemStack(Material.getMaterial(getConfig().getInt("Kit-item-1")));
    ItemMeta Gm = G.getItemMeta();
    Gm.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-1")));
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack KitVip() {
    ItemStack G = new ItemStack(Material.getMaterial(getConfig().getInt("Kit-item-2")));
    ItemMeta Gm = G.getItemMeta();
    Gm.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-2")));
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack KitVips() {
    ItemStack G = new ItemStack(Material.getMaterial(getConfig().getInt("Kit-item-3")));
    ItemMeta Gm = G.getItemMeta();
    Gm.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-3")));
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack KitYoutuber()
  {
    ItemStack G = new ItemStack(Material.getMaterial(getConfig().getInt("Kit-item-4")));
    ItemMeta Gm = G.getItemMeta();
    Gm.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-4")));
    G.setItemMeta(Gm);
    return G;
  }

  public ItemStack KitStaff() {
    ItemStack G = new ItemStack(Material.getMaterial(getConfig().getInt("Kit-item-5")));
    ItemMeta Gm = G.getItemMeta();
    Gm.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-5")));
    G.setItemMeta(Gm);
    return G;
  }

  @EventHandler
  public void death(PlayerDeathEvent e) {
    Player p = e.getEntity();
    e.getDrops().clear();
    Player k = e.getEntity().getKiller();
    addKills(k);
    addDeath(p);
    addPoints2(p, k);
    saveConfig();
    reloadConfig();
    String message = getConfig().getString("Player-BroadCast-DeathEvent");
    message = message.replaceAll("%PLAYER%", p.getName());
    message = message.replaceAll("&", "§");
    e.setDeathMessage(null);

    message = message.replaceAll("%KILLER%", k.getName());
    Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    saveDefaultConfig();
    reloadConfig();
    String Sound2 = getConfig().getString("valueOf-Sound-DeathEvent");
    Player p1 = e.getEntity().getKiller();
    p1.setHealth(20);
    if (getConfig().getBoolean("Player-DeathEvent-Sound"))
    {
      p1.playSound(p1.getLocation(), Sound.valueOf(Sound2), 10.0F, 1.0F);
    }
    if (getConfig().getBoolean("Player-DeathEvent-Drops-ItemsHeal")) {
      e.getDrops().clear();
      e.getDrops().add(new ItemStack(Material.getMaterial(getConfig().getInt("Player-DeathEvent-Item-getDrops"))));
    }
  }

  @EventHandler
  public void Hunger(FoodLevelChangeEvent e) { e.setCancelled(true);
    e.setFoodLevel(20); }

  @EventHandler
  public void Respawn(PlayerRespawnEvent e)
  {
    Player p = e.getPlayer();
    p.getInventory().remove(Gui2());
    p.setFoodLevel(20);
    p.getInventory().addItem(new ItemStack[] { Gui2() });
    World w = Bukkit.getServer().getWorld(getConfig().getString("Hub.world"));
    double x = getConfig().getDouble("Hub.x");
    double y = getConfig().getDouble("Hub.y");
    double z = getConfig().getDouble("Hub.z");
    Location Spawn = new Location(w, x, y, z);
    p.setMaxHealth(20.0D);
    Spawn.setPitch((float)getConfig().getDouble("Hub.pitch"));
    Spawn.setYaw((float)getConfig().getDouble("Hub.yaw"));
    p.teleport(Spawn);
  }

  @EventHandler
  public void Join(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    if (!p.hasPlayedBefore()) {
      p.setFoodLevel(20);

      p.setMaxHealth(20.0D);
      p.getInventory().remove(Gui2());
      p.setFoodLevel(20);
      p.getInventory().addItem(new ItemStack[] { Gui2() });
      World w = Bukkit.getServer().getWorld(getConfig().getString("Hub.world"));
      double x = getConfig().getDouble("Hub.x");
      double y = getConfig().getDouble("Hub.y");
      double z = getConfig().getDouble("Hub.z");
      Location Spawn = new Location(w, x, y, z);
      Spawn.setPitch((float)getConfig().getDouble("Hub.pitch"));
      Spawn.setYaw((float)getConfig().getDouble("Hub.yaw"));
      p.teleport(Spawn);
    }
    p.getInventory().remove(Gui2());
    p.setFoodLevel(20);
    p.setMaxHealth(20.0D);
    p.getInventory().addItem(new ItemStack[] { Gui2() });
    World w = Bukkit.getServer().getWorld(getConfig().getString("Hub.world"));
    double x = getConfig().getDouble("Hub.x");
    double y = getConfig().getDouble("Hub.y");
    double z = getConfig().getDouble("Hub.z");
    Location Spawn = new Location(w, x, y, z);
    Spawn.setPitch((float)getConfig().getDouble("Hub.pitch"));
    Spawn.setYaw((float)getConfig().getDouble("Hub.yaw"));
    p.teleport(Spawn);
  }

  @EventHandler
  public void playerInteractEvent(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    ItemStack I = p.getItemInHand();
    if ((I != null) && (I.hasItemMeta()) && (I.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Gui-Item-name")))) && (
      (e.getAction() == Action.RIGHT_CLICK_BLOCK) || (e.getAction() == Action.RIGHT_CLICK_AIR))) {
      p.openInventory(this.I1);
      this.I1.setItem(0, KitPlayer());
      this.I1.setItem(2, KitVip());
      this.I1.setItem(4, KitVips());
      this.I1.setItem(6, KitYoutuber());
      this.I1.setItem(8, KitStaff());
      p.updateInventory();
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void Click(InventoryClickEvent e)
  {
    Player p = (Player)e.getWhoClicked();
    ItemStack I = e.getCurrentItem();
    String message = getConfig().getString("Kit-name-1");
    message = ChatColor.translateAlternateColorCodes('%', message);
    if ((I != null) && (I.hasItemMeta()) && (I.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-1"))))) {
      if (p.hasPermission("FFA.Kit1")) {
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.getMaterial(getConfig().getInt("Helmet-1"))));
        p.getInventory().setChestplate(new ItemStack(Material.getMaterial(getConfig().getInt("ChestPlate-1"))));
        p.getInventory().setLeggings(new ItemStack(Material.getMaterial(getConfig().getInt("Leggings-1"))));
        p.getInventory().setBoots(new ItemStack(Material.getMaterial(getConfig().getInt("Boots-1"))));
        p.getInventory().addItem(new ItemStack[] { SwordKit1() });
        p.getInventory().addItem(new ItemStack[] { Bow() });
        p.getInventory().addItem(new ItemStack[] { Rod() });
        p.getInventory().addItem(new ItemStack[] { ArrowPlayer1() });
        p.closeInventory();
        e.setCancelled(true);
        p.updateInventory();
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Massage-Kit1-Take")));
      } else {
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage("§3FFA §8▏ §6No puedes usar ese kit!");
      }
    }
    if ((I != null) && (I.hasItemMeta()) && (I.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-2"))))) {
      if (p.hasPermission("FFA.Kit2")) {
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.getMaterial(getConfig().getInt("Helmet-2"))));
        p.getInventory().setChestplate(new ItemStack(Material.getMaterial(getConfig().getInt("ChestPlate-2"))));
        p.getInventory().setLeggings(new ItemStack(Material.getMaterial(getConfig().getInt("Leggings-2"))));
        p.getInventory().setBoots(new ItemStack(Material.getMaterial(getConfig().getInt("Boots-2"))));
        p.getInventory().addItem(new ItemStack[] { SwordKit2() });
        p.getInventory().addItem(new ItemStack[] { Bow() });
        p.getInventory().addItem(new ItemStack[] { Rod() });
        p.getInventory().addItem(new ItemStack[] { ArrowVip() });
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Massage-Kit2-Take")));
      } else {
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage("§3FFA §8▏ §6No puedes usar ese kit!");
      }
    }
    if ((I != null) && (I.hasItemMeta()) && (I.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-3"))))) {
      if (p.hasPermission("FFA.Kit3")) {
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.getMaterial(getConfig().getInt("Helmet-3"))));
        p.getInventory().setChestplate(new ItemStack(Material.getMaterial(getConfig().getInt("ChestPlate-3"))));
        p.getInventory().setLeggings(new ItemStack(Material.getMaterial(getConfig().getInt("Leggings-3"))));
        p.getInventory().setBoots(new ItemStack(Material.getMaterial(getConfig().getInt("Boots-3"))));
        p.getInventory().addItem(new ItemStack[] { SwordKit3() });
        p.getInventory().addItem(new ItemStack[] { Bow() });
        p.getInventory().addItem(new ItemStack[] { Rod() });
        p.getInventory().addItem(new ItemStack[] { ArrowVips() });
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Massage-Kit3-Take")));
      } else {
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage("§3FFA §8▏ §6No puedes usar ese kit!");
      }
    }
    if ((I != null) && (I.hasItemMeta()) && (I.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-4"))))) {
      if (p.hasPermission("FFA.Kit4")) {
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.getMaterial(getConfig().getInt("Helmet-4"))));
        p.getInventory().setChestplate(new ItemStack(Material.getMaterial(getConfig().getInt("ChestPlate-4"))));
        p.getInventory().setLeggings(new ItemStack(Material.getMaterial(getConfig().getInt("Leggings-4"))));
        p.getInventory().setBoots(new ItemStack(Material.getMaterial(getConfig().getInt("Boots-4"))));
        p.getInventory().addItem(new ItemStack[] { SwordKit4() });
        p.getInventory().addItem(new ItemStack[] { Bow() });
        p.getInventory().addItem(new ItemStack[] { Rod() });
        p.getInventory().addItem(new ItemStack[] { ArrowYoutuber() });
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Massage-Kit4-Take")));
      } else {
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage("§3FFA §8▏ §6No puedes usar ese kit!");
      }
    }
    if ((I != null) && (I.hasItemMeta()) && (I.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Kit-name-5")))))
      if (p.hasPermission("FFA.Kit5")) {
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.getMaterial(getConfig().getInt("Helmet-5"))));
        p.getInventory().setChestplate(new ItemStack(Material.getMaterial(getConfig().getInt("ChestPlate-5"))));
        p.getInventory().setLeggings(new ItemStack(Material.getMaterial(getConfig().getInt("Leggings-5"))));
        p.getInventory().setBoots(new ItemStack(Material.getMaterial(getConfig().getInt("Boots-5"))));
        p.getInventory().addItem(new ItemStack[] { SwordKit5() });
        p.getInventory().addItem(new ItemStack[] { Bow() });
        p.getInventory().addItem(new ItemStack[] { Rod() });
        p.getInventory().addItem(new ItemStack[] { ArrowStaff() });
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Massage-Kit5-Take")));
      } else {
        p.closeInventory();
        p.updateInventory();
        e.setCancelled(true);
        p.sendMessage("§3FFA §8▏ §6No puedes usar ese kit!");
      }
  }

  @EventHandler
  public void Drop(PlayerDropItemEvent e) {
    Player p = e.getPlayer();
    if (getConfig().getBoolean("Drop-Item"))
      e.setCancelled(true);
  }

  @EventHandler
  public void Pick(PlayerPickupItemEvent e)
  {
    Player p = e.getPlayer();
    p.setMaxHealth(20.0D);
    World w = p.getWorld();
    Location l = p.getLocation();
    if ((getConfig().getBoolean("Player-PickItem-Heal")) && 
      (e.getItem().getItemStack().getType() == Material.getMaterial(getConfig().getInt("Player-PickItem-Item-Healing")))) {
      e.setCancelled(true);
      e.getPlayer().setHealth(20.0D);
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Player-PickItem-Message")));
      e.getItem().remove();
    }
  }

  @EventHandler
  public void onFall(EntityDamageEvent e)
  {
    if (e.getCause() == EntityDamageEvent.DamageCause.FALL)
    {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void Out(PlayerQuitEvent e) { Player p = e.getPlayer();
    p.getInventory().clear();
    p.getInventory().setHelmet(null);
    p.getInventory().setChestplate(null);
    p.getInventory().setLeggings(null);
    p.getInventory().setBoots(null);
    p.getInventory().clear(); }

  @EventHandler
  public void Place(BlockPlaceEvent e) {
    Player p = e.getPlayer();
    if (!this.Build.containsKey(p))
    {
      e.setBuild(true);
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void Lava(BlockBurnEvent e) { Player p = (Player)e;
    if (!this.Build.containsKey(p))
    {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void Break(BlockBreakEvent e)
  {
    Player p = e.getPlayer();
    if (!this.Build.containsKey(p))
    {
      e.setExpToDrop(0);
      e.setCancelled(true);
    }
  }
}