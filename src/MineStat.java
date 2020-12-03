import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

//Класс с методами опроса сервера
public class MineStat {
    public static final byte NUM_FIELDS = 6;     // expected number of fields returned from server after query

    // IP адрес сервера
    private String address;

    // Номер порта сервера
    private int port;

    // Тайм-Аут TCP соединения
    private int timeout;

    // Сервер работает?
    private boolean serverUp;

    // Описание сервера
    private String motd;

    // Версия сервера
    private String version;

    // Число игроков в сети
    private String currentPlayers;

    // Максимальное кол-во игроков
    private String maximumPlayers;

    // Пинг до сервера в милисекундах
    private long latency;

    public MineStat(String address, int port, int timeout) {
        setAddress(address);
        setPort(port);
        setTimeout(timeout);
        refresh();
    }

    // Проверка сервера, возвращает true- если в сети, false- если не в сети
    public boolean refresh() {
        String[] serverData;
        String rawServerData;
        long ping;
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(getAddress(), getPort()), timeout);
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            new Thread(() -> {
                try {
                    Thread.sleep(timeout);
                    clientSocket.close();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            byte[] payload = {(byte) 0xFE, (byte) 0x01};
            ping = System.currentTimeMillis();
            dos.write(payload, 0, payload.length);
            rawServerData = br.readLine();
            br.close();
            dos.close();
            clientSocket.close();
        } catch (Exception e) {
            return false;
        }
        if (rawServerData == null)
            serverUp = false;
        else {
            serverData = rawServerData.split("\u0000\u0000\u0000");
            if (serverData.length >= NUM_FIELDS) {
                latency = System.currentTimeMillis() - ping;
                serverUp = true;
                setVersion(serverData[2].replace("\u0000", ""));
                setMotd(serverData[3].replace("\u0000", ""));
                setCurrentPlayers(serverData[4].replace("\u0000", ""));
                setMaximumPlayers(serverData[5].replace("\u0000", ""));
            } else
                serverUp = false;
        }

        return serverUp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getMotd() {
        return motd;
    }

    public String getVersion() {
        return version;
    }

    public String getCurrentPlayers() {
        return currentPlayers;
    }

    public String getMaximumPlayers() {
        return maximumPlayers;
    }

    public long getLatency() {
        return latency;
    }

    public void setMaximumPlayers(String maximumPlayers) {
        this.maximumPlayers = maximumPlayers;
    }

    public void setCurrentPlayers(String currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isServerUp() {
        return serverUp;
    }
}
