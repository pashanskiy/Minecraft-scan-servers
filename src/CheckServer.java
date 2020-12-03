//Класс опроса сервера
public class CheckServer implements Runnable {
    String ip;
    int port;
    int numofline;

    public CheckServer(String ip, int port, int numofline) {
        this.ip = ip;
        this.port = port;
        this.numofline = numofline;
    }

    @Override
    public void run() {
        MineStat ms = new MineStat(ip, port, 1000);
        if (ms.isServerUp()) {
            Main.onlineServers.add(new ServerPerson("Online", ip, String.valueOf(port),
                    ms.getVersion().replace(";",""), ms.getCurrentPlayers(), ms.getMaximumPlayers(),
                    String.valueOf(ms.getLatency()), ms.getMotd().replace(";","")));
        }
            Main.threadsrun--;
    }
}
