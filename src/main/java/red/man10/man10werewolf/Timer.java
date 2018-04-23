package red.man10.man10werewolf;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
    private Man10Werewolf plugin;
    int time;

    int timer;
    public Timer(Man10Werewolf plugin){
        this.plugin = plugin;
    }
    public void jointime(int times){

        time = times;
        new BukkitRunnable(){
            @Override
            public void run() {
                if (!plugin.jointime){
                    time = 0;
                    cancel();
                    return;
                }
                if (time == 0){
                    time = 0;
                    plugin.gamestart();
                    cancel();
                    return;
                }
                if (time % 86400 == 0){
                    Bukkit.broadcastMessage(plugin.prefix + "§7参加受付終了まで残り§e§l" + time/86400 + "日");
                }else if (time % 3600 == 0&&86400 > time){
                    Bukkit.broadcastMessage(plugin.prefix + "§7参加受付終了まで残り§e§l" + time/3600 + "時間");
                }else if (time % 60 == 0&&3600 > time){
                    Bukkit.broadcastMessage(plugin.prefix + "§7参加受付終了まで残り§e§l" + time/60 + "分");
                }else if ((time % 10 == 0&&60 > time) || (time <= 5&&60 > time) ){
                    Bukkit.broadcastMessage(plugin.prefix + "§7参加受付終了まで残り§e§l" + time + "秒");
                }else if(time == times) {
                    int zikan = time % 86400;
                    int hun = zikan % 3600;
                    int byou = hun % 60;
                    Bukkit.broadcastMessage(plugin.prefix + "§7参加受付終了まで残り§e§l" + time/86400 + "日" + time/3600 + "時間"+ hun/60 + "分"+ byou + "秒");
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }
    public void gametime(int times){

        time = times;
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!plugin.gametime) {
                    time = 0;
                    cancel();
                    return;
                }
                if (time == 0){
                    time = 0;
                    plugin.votestart();
                    cancel();
                    return;
                }
                if (time % 86400 == 0){
                    Bukkit.broadcastMessage(plugin.prefix + "§7相談タイム終了まで残り§e§l" + time/86400 + "日");
                }else if (time % 3600 == 0&&86400 > time){
                    Bukkit.broadcastMessage(plugin.prefix + "§7相談タイム終了まで残り§e§l" + time/3600 + "時間");
                }else if (time % 60 == 0&&3600 > time){
                    Bukkit.broadcastMessage(plugin.prefix + "§7相談タイム終了まで残り§e§l" + time/60 + "分");
                }else if ((time % 10 == 0&&60 > time) || (time <= 5&&60 > time) ){
                    Bukkit.broadcastMessage(plugin.prefix + "§7相談タイム終了まで残り§e§l" + time + "秒");
                }else if(time == times) {
                    int zikan = time % 86400;
                    int hun = zikan % 3600;
                    int byou = hun % 60;
                    Bukkit.broadcastMessage(plugin.prefix + "§7相談タイム終了まで残り§e§l" + time/86400 + "日" + time/3600 + "時間"+ hun/60 + "分"+ byou + "秒");
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }
    public void votetime(int times){

        time = times;
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!plugin.votetime) {
                    time = 0;
                    cancel();
                    return;
                }
                if (time == 0){
                    time = 0;
                    plugin.gameend();
                    cancel();
                    return;
                }
                if (time % 86400 == 0){
                    Bukkit.broadcastMessage(plugin.prefix + "§7投票受付終了まで残り§e§l" + time/86400 + "日");
                }else if (time % 3600 == 0&&86400 > time){
                    Bukkit.broadcastMessage(plugin.prefix + "§7投票受付終了まで残り§e§l" + time/3600 + "時間");
                }else if (time % 60 == 0&&3600 > time){
                    Bukkit.broadcastMessage(plugin.prefix + "§7投票受付終了まで残り§e§l" + time/60 + "分");
                }else if ((time % 10 == 0&&60 > time) || (time <= 5&&60 > time) ){
                    Bukkit.broadcastMessage(plugin.prefix + "§7投票受付終了まで残り§e§l" + time + "秒");
                }else if(time == times) {
                    int zikan = time % 86400;
                    int hun = zikan % 3600;
                    int byou = hun % 60;
                    Bukkit.broadcastMessage(plugin.prefix + "§7投票受付終了まで残り§e§l" + time/86400 + "日" + time/3600 + "時間"+ hun/60 + "分"+ byou + "秒");
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }
}
