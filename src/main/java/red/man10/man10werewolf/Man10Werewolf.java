package red.man10.man10werewolf;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class Man10Werewolf extends JavaPlugin implements Listener {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")) {
                    getServer().getPluginManager().disablePlugin(this);
                    getServer().getPluginManager().enablePlugin(this);
                    getLogger().info("設定を再読み込みしました。");
                    return true;
                }
                getLogger().info("mww reload");
                return true;
            }
        }
        Player p = (Player)sender;
        if(!power&&!p.hasPermission("man10werewolf.ignore")){
            p.sendMessage(prefix+"§4§l現在人狼はOFFになっています");
            return true;
        }
        if(args.length == 0) {
            p.sendMessage("§7========"+prefix+"§7=======");
            p.sendMessage("§7/mww new [金額] : $[金額]で人狼ゲームを始めます。");
            p.sendMessage("§7/mww join : お金を支払いゲームに参加します。");
            p.sendMessage("§7/mww vote [player名]: 処刑投票を[player名]に入れます。");
            if(jointime){
                p.sendMessage("§7§l参加受付中: 合計§e"+join.size()+"§7§l人"+" §e参加費$"+gamemoney);
            }else if(gametime){
                p.sendMessage("§7§lゲーム中: 合計§e"+join.size()+"§7§l人"+" §e合計金額$"+totalmoney);
            }else if(gametime){
                p.sendMessage("§7§l投票中: 合計§e"+join.size()+"§7§l人"+" §e合計金額$"+totalmoney);
            }
            p.sendMessage("§7========"+prefix+"§7=======");
        }else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("join")) {
                if(gametime){
                    p.sendMessage(prefix+"§4§l現在ゲーム中です");
                    return true;
                }else if(!jointime) {
                    p.sendMessage(prefix + "§4§l現在参加受付中ではありません");
                    return true;
                }else if(votetime){
                        p.sendMessage(prefix+"§4§l現在投票受付中です");
                        return true;
                    }
                if(val.getBalance(p.getUniqueId())<gamemoney){
                    p.sendMessage(prefix+"§4§lお金が足りません！");
                    return true;
                }
                if(join.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix+"§4§lすでに参加しています");
                    return true;
                }
                if(join.size()>=6){
                    p.sendMessage(prefix+"§4§lもう定員です");
                    return true;
                }
                val.withdraw(p.getUniqueId(),gamemoney);
                totalmoney = totalmoney + gamemoney;
                join.put(p.getUniqueId(),0);
                Bukkit.broadcastMessage(prefix+"§8§l"+p.getDisplayName()+"§7§lさんが人狼ゲームに参加しました。合計: "+join.size()+"人");
                return true;
            }else if(args[0].equalsIgnoreCase("cancel")) {
                if(!p.hasPermission("man10werewolf.cancel")){
                    p.sendMessage(prefix + "§cあなたには強制終了する権限がありません！");
                    return true;
                }
                forcecancel();
                return true;
            }else if(args[0].equalsIgnoreCase("reload")) {
                if(!p.hasPermission("man10werewolf.reload")){
                    p.sendMessage(prefix + "§cあなたには再起動する権限がありません！");
                    return true;
                }
                getServer().getPluginManager().disablePlugin(this);
                getServer().getPluginManager().enablePlugin(this);
                p.sendMessage(prefix + "§a再起動しました");
                return true;
            }else if(args[0].equalsIgnoreCase("view")) {
                if(!p.hasPermission("man10werewolf.view")){
                    p.sendMessage(prefix + "§cあなたには役職を見る権限がありません！");
                    return true;
                }
                if(!gametime){
                    p.sendMessage(prefix+"§4§l現在ゲーム中ではありません");
                    return true;
                }else if(jointime) {
                    p.sendMessage(prefix + "§4§l現在参加受付中です");
                    return true;
                }else if(votetime){
                    p.sendMessage(prefix+"§4§l現在投票受付中です");
                    return true;
                }
                for(UUID uuid:yaku.keySet()){
                   String name = Bukkit.getPlayer(uuid).getDisplayName();
                    p.sendMessage(prefix +name+ ": §e"+yaku.get(uuid));
                }
                return true;
            }else if(args[0].equalsIgnoreCase("push")) {
                if(!p.hasPermission("man10werewolf.push")){
                    p.sendMessage(prefix + "§cあなたには強制的に次のフェーズに移動させる権限がありません！");
                    return true;
                }
                if(gametime){
                    votestart();
                    return true;
                }else if(jointime) {
                    gamestart();
                    return true;
                }else if(votetime){
                    gameend();
                    return true;
                }else {
                    p.sendMessage(prefix + "§c現在ゲーム中ではありません");
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("voteview")) {
                if(!p.hasPermission("man10werewolf.voteview")){
                    p.sendMessage(prefix + "§cあなたには投票を見る権限がありません！");
                    return true;
                }
                if(gametime){
                    p.sendMessage(prefix+"§4§l現在ゲーム中です");
                    return true;
                }else if(jointime) {
                    p.sendMessage(prefix + "§4§l現在参加受付中です");
                    return true;
                }else if(!votetime){
                    p.sendMessage(prefix+"§4§l現在投票中ではありません");
                    return true;
                }
                for(UUID uuid:join.keySet()){
                    String name = Bukkit.getPlayer(uuid).getDisplayName();
                    p.sendMessage(prefix +name+ ": §e"+join.get(uuid)+"票");
                }
                return true;
            }else if(args[0].equalsIgnoreCase("on")) {
                if(!p.hasPermission("man10werewolf.on")){
                    p.sendMessage(prefix + "§4あなたにはonにする権限がありません！");
                    return true;
                }
                power = true;
                p.sendMessage(prefix + "§aONしました。");
                return true;
            }else if(args[0].equalsIgnoreCase("off")) {
                if(!p.hasPermission("man10werewolf.off")){
                    p.sendMessage(prefix + "§4あなたにはoffにする権限がありません！");
                    return true;
                }
                power = false;
                p.sendMessage(prefix + "§cOFFしました。");
                forcecancel();
                return true;
            }
        }else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("new")) {
                if(gametime){
                    p.sendMessage(prefix+"§4§l現在ゲーム中です");
                    return true;
                }else if(jointime) {
                    p.sendMessage(prefix + "§4§l現在参加受付中です");
                    return true;
                }else if(votetime){
                    p.sendMessage(prefix+"§4§l現在投票受付中です");
                    return true;
                }
                int money = 0;
                try {
                    money = Integer.parseInt(args[1]);

                }
                catch (NumberFormatException e) {
                    p.sendMessage(prefix+"§4§l数字を入力してください");
                    return true;
                }
                if(money < 1000000){
                    p.sendMessage(prefix+"§4§l100万円以上で指定してください");
                    return true;
                }
                if(val.getBalance(p.getUniqueId())<money){
                    p.sendMessage(prefix+"§4§lお金が足りません！");
                    return true;
                }
                val.withdraw(p.getUniqueId(),money);
                gamemoney = money;
                totalmoney = money;
                double hyouzimoney =  (double) money;
                join.put(p.getUniqueId(),0);
                Bukkit.broadcastMessage(prefix+"§7§l人狼ゲームが§e$"+hyouzimoney+"§7§lで参加受付開始されました。");
                Bukkit.broadcastMessage(prefix+"§7§l参加は§8§l/mww join §7§lからどうぞ");
                timer.jointime(120);
                jointime = true;
                return true;
            }else if(args[0].equalsIgnoreCase("vote")) {
                if(gametime){
                    p.sendMessage(prefix+"§4§l現在ゲーム中です");
                    return true;
                }else if(jointime) {
                    p.sendMessage(prefix + "§4§l現在参加受付中です");
                    return true;
                }else if(!votetime){
                    p.sendMessage(prefix+"§4§l現在投票受付中ではありません");
                    return true;
                }
                if(!join.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix + "§4§lあなたは参加者ではありません");
                    return true;
                }
                if(vote.containsKey(p.getUniqueId())){
                    p.sendMessage(prefix + "§4§lあなたはすでに投票しています");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                UUID uuid = target.getUniqueId();
                if(!join.containsKey(uuid)){
                    p.sendMessage(prefix + "§4§lその人は参加者ではありません");
                    return true;
                }
                vote.put(p.getUniqueId(),"true");
                int getvote = join.get(uuid);
                join.put(uuid,getvote+1);
                p.sendMessage(prefix + "§c投票しました。");
                return true;
            }
        }
        return true;
    }
    String prefix = "§4[§dm§fa§an10§7werewolf§4]§r";
    boolean power = true;
    boolean gametime = false;
    boolean jointime = false;
    boolean votetime = false;
    int gamemoney = 0;
    int totalmoney = 0;
    VaultManager val = null;
    Timer timer = null;
    private HashMap<UUID,Integer> join;
    private HashMap<UUID,String> yaku;
    private HashMap<UUID,String> vote;
    @Override
    public void onEnable() {
        getCommand("mww").setExecutor(this);
        val = new VaultManager(this);
        timer = new Timer(this);
        join = new HashMap<>();
        yaku = new HashMap<>();
        vote = new HashMap<>();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
       forcecancel();
    }
    @EventHandler
    public void onExit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if(join.containsKey(p.getUniqueId())){
            join.remove(p.getUniqueId());
            totalmoney = totalmoney - gamemoney;
            val.deposit(p.getUniqueId(),gamemoney);
            Bukkit.broadcastMessage(prefix+"§8§l"+p.getDisplayName()+"§7§lさんがログアウトしたので外されました。合計: "+join.size()+"人");
        }else if(yaku.containsKey(p.getUniqueId())){
            yaku.remove(p.getUniqueId());
            totalmoney = totalmoney - gamemoney;
            val.deposit(p.getUniqueId(),gamemoney);
            Bukkit.broadcastMessage(prefix+"§8§l"+p.getDisplayName()+"§7§lさんがログアウトしたので外されました。合計: "+join.size()+"人");
        }
    }
    public boolean gamestart(){
        if(join.size()<3){
            forcecancel();
            return false;
        }
        jointime = false;
        int maxzinro = 1;
        int maxuranaisi = 1;
        UUID uranaisi = null;
        ArrayList<Integer> shuffleNumber = new ArrayList<Integer>();

        // listに値を入れる。この段階では昇順
        for(int i = 1 ; i <= join.size() ; i++) {
            shuffleNumber.add(i);
        }
        // シャッフルして、順番を変える
        Collections.shuffle(shuffleNumber);
        int i = 0;
        for(UUID uuid:join.keySet()){
            int randomNumber = shuffleNumber.get(i);
            i++;
            if(randomNumber == 1){
                if(maxzinro == 1) {
                    yaku.put(uuid, "人狼");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"あなたは§c人狼§fです");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"§cどんな手を使ってでも生き残ってください。");
                    maxzinro--;
                }else{
                    yaku.put(uuid, "村人");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"あなたは§a村人§fです");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"人狼をあぶり出して、処刑しましょう");
                }
            }else if(randomNumber == 2){
                if(maxuranaisi == 1) {
                    yaku.put(uuid, "吊り人");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"あなたは§d吊り人§fです");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"殺されるよう立ち回ろう！殺された時点で無条件に勝利します");
                    uranaisi = uuid;
                    maxuranaisi--;
                }else{
                    yaku.put(uuid, "村人");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"あなたは§a村人§fです");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"人狼をあぶり出して、処刑しましょう");
                }
            }else if(randomNumber == 3){
                if(maxuranaisi == 1) {
                    yaku.put(uuid, "占い師");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"あなたは§5占い師§fです");
                    uranaisi = uuid;
                    maxuranaisi--;
                }else{
                    yaku.put(uuid, "村人");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"あなたは§a村人§fです");
                    Bukkit.getPlayer(uuid).sendMessage(prefix+"人狼をあぶり出して、処刑しましょう");
                }
            }else {
                yaku.put(uuid, "村人");
                Bukkit.getPlayer(uuid).sendMessage(prefix+"あなたは§a村人§fです");
                Bukkit.getPlayer(uuid).sendMessage(prefix+"人狼をあぶり出して、処刑しましょう");
            }
        }
        if(Bukkit.getPlayer(uranaisi)!=null){
            List<UUID> players = new ArrayList<UUID>();
            for(UUID uuid:join.keySet()) {
                if(!uuid.equals(uranaisi)) {
                    players.add(uuid);
                }
            }
            Player player = Bukkit.getPlayer(players.get(ThreadLocalRandom.current().nextInt(players.size())));
            Bukkit.getPlayer(uranaisi).sendMessage(prefix + "占った結果、" + player.getDisplayName() + "は" + yaku.get(player.getUniqueId()) + "でした");
        }
        Bukkit.broadcastMessage(prefix+"§7§l人狼ゲームが開始されました。2分間の相談タイム開始です。");
        timer.gametime(120);
        gametime = true;
        return true;
    }
    public boolean votestart(){
        votetime = true;
        gametime = false;
        Bukkit.broadcastMessage(prefix+"§7§l相談タイムが終了しました。30秒間の投票タイムを開始します。");
        timer.votetime(30);
        return true;
    }
    public boolean gameend(){
        votetime = false;
        Bukkit.broadcastMessage(prefix+"§7§l投票タイムが終了しました。投票の結果:");
        int vote = 0;
        List<UUID> maxvoteman = new ArrayList<UUID>();
        for(UUID uuid:join.keySet()){
            if(vote < join.get(uuid)) {
                vote = join.get(uuid);
                maxvoteman.clear();
                maxvoteman.add(uuid);
            }else if(vote == join.get(uuid)) {
                maxvoteman.add(uuid);
            }
        }
        Player teruteru = null;
        if(vote == 0){
            Bukkit.broadcastMessage(prefix+"§8§l誰も投票しなかったので平和村と認定されました。");
        }else{
            String[] players = new String[maxvoteman.size()];
            int i = 0;
            for(UUID uuid:maxvoteman){
                if(yaku.get(uuid).equalsIgnoreCase("吊り人")){
                   teruteru = Bukkit.getPlayer(uuid);
                }
                join.remove(uuid);
                yaku.remove(uuid);
                players[i] = Bukkit.getPlayer(uuid).getDisplayName();
                Bukkit.getPlayer(uuid).damage(1000);
                i++;
            }
            Bukkit.broadcastMessage(prefix+"§8§l"+Arrays.toString(players)+"が処刑されました。");
        }
        if(teruteru!=null){
            Bukkit.broadcastMessage(prefix+"§6§l吊り人「"+ teruteru.getDisplayName()+"§6§l」の勝利！！");
            Bukkit.broadcastMessage(prefix+"§e$"+totalmoney+"をゲットした！");
            val.deposit(teruteru.getUniqueId(),totalmoney);
            gamemoney = 0;
            totalmoney = 0;
            join.clear();
            yaku.clear();
            return true;
        }
        List<UUID> murabitos = new ArrayList<UUID>();
        for(UUID uuid:join.keySet()){
            String yakuget = yaku.get(uuid);
            if(yakuget.equalsIgnoreCase("人狼")){
                Bukkit.broadcastMessage(prefix+"§4§l人狼「"+ Bukkit.getPlayer(uuid).getDisplayName()+"§4§l」の勝利！！");
                Bukkit.broadcastMessage(prefix+"§e$"+totalmoney+"をゲットした！");
                val.deposit(uuid,totalmoney);
                gamemoney = 0;
                totalmoney = 0;
                join.clear();
                yaku.clear();
                return true;
            }
            murabitos.add(uuid);
        }
        Bukkit.broadcastMessage(prefix+"§a§l村人チーム§a§lの勝利！！");
        Bukkit.broadcastMessage(prefix+"§e1人$"+totalmoney/murabitos.size()+"をゲットした！");
        for(UUID uuid:murabitos){
            val.deposit(uuid,totalmoney/murabitos.size());
        }
        gamemoney = 0;
        totalmoney = 0;
        join.clear();
        yaku.clear();
        return true;
    }
    public boolean forcecancel(){
        if(!gametime&&!jointime&&!votetime){
            return false;
        }
        gametime = false;
        jointime = false;
        votetime = false;
        Bukkit.broadcastMessage(prefix+"§7§l人狼ゲームが強制キャンセルされました。");
        for(UUID uuid:join.keySet()){
            val.deposit(uuid,gamemoney);
            Bukkit.getPlayer(uuid).sendMessage(prefix+"返金されました");
        }
        gamemoney = 0;
        totalmoney = 0;
        join.clear();
        yaku.clear();
        vote.clear();
        return true;
    }
}
