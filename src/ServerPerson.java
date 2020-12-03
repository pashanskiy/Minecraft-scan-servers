//Класс хранения данных
public class ServerPerson {
    private final String status;
    private final String ip;
    private final String port;
    private final String version;
    private final String playersCurent;
    private final String playersMaximum;
    private final String ping;
    private final String message;

    public ServerPerson(String status, String ip, String port, String version, String playersCurent,
                        String playersMaximum, String ping, String message)
    {
        this.status = status;
        this.ip = ip;
        this.port = port;
        this.version = version;
        this.playersCurent = playersCurent;
        this.playersMaximum = playersMaximum;
        this.ping = ping;
        this.message = message;
    }

    public String getStatus()
    {
        return this.status;
    }
    public String getIP()
    {
        return this.ip;
    }
    public String getPort()
    {
        return this.port;
    }
    public String getVersion()
    {
        return this.version;
    }
    public String getPlayersCurent()
    {
        return this.playersCurent;
    }
    public String getPlayersMaximum()
    {
        return this.playersMaximum;
    }
    public String getPing()
    {
        return this.ping;
    }
    public String getMessage()
    {
        return this.message;
    }


}
