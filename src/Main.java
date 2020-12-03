import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//Основной класс
public class Main {
    public static ArrayList<ServerPerson> onlineServers = new ArrayList<>();
    public static ArrayList<String> servers = new ArrayList<>();
    public static int threadsrun = 0;
    public static int numofline = 0;

    public static void main(String[] args) {
        System.out.print("\n");
        if (args.length == 0) System.out.print("Args: -port -rate -masscanoL -ipsInput -ServersOutput\n" +
                "Defaults: -port 25565  -rate 200  -ipsInput ipsinput.txt  -ServersOutput ServersOutput.csv\n");
        boolean masscanfile = false;
        int numofthreads = 200;
        int port = 25565;

        String fileinput = "ipsinput.txt";
        String fileoutput = System.getProperty("user.dir") + "/ServersOutput.csv";
        //Ввод аргументов
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-port")) {
                port = Integer.parseInt(args[i + 1]);
            }
            if (args[i].equalsIgnoreCase("-rate")) {
                numofthreads = Integer.parseInt(args[i + 1]);
            }
            if (args[i].equalsIgnoreCase("-ipsinput")) {
                fileinput = args[i + 1];
            }
            if (args[i].equalsIgnoreCase("-masscanol")) {
                fileinput = args[i + 1];
                masscanfile = true;
            }
            if (args[i].equalsIgnoreCase("-serversoutput")) {
                fileoutput = args[i + 1];
            }
        }
        File file = new File(fileinput);
        if (file.exists()) {
            if (numofthreads < 1) //Проверка на валидность числа потоков
                System.out.println("Invalid arguments!\nArgs: -port -rate -masscanoL -ipsInput -ServersOutput\n" +
                        "Defaults: -port 25565  -rate 200  -ipsInput ipsinput.txt  -ServersOutput ServersOutput.csv\n");
            else {
                System.out.print("-port=" + port + "\n" + "-rate=" + numofthreads + "\n" + "-file=" + fileinput + "\n\n");
                BufferedReader reader;

                try {
                    Thread.sleep(1000);
                    reader = new BufferedReader(new FileReader(file));

                    String ip = returnIp(masscanfile, reader);

                    while (ip != null) {
                        servers.add(ip);
                        ip = returnIp(masscanfile, reader);
                    }
                    putInfo();
                    for (int i = 0; i < servers.size();) { //Многопоточное сканирование
                        if (threadsrun < numofthreads) {
                            threadsrun++;
                            numofline++;
                            Thread t2 = new Thread(new CheckServer(servers.get(i), port, numofline));
                            t2.start(); //Запуск потока опроса
                            i++;
                        }
                        try {
                            Thread.sleep(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    reader.close();

                    //Вывод результатов
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.print("\n\nDone! Save results to file:\n");
                    System.out.println(fileoutput+"\n");
                    Files.deleteIfExists(Paths.get(fileoutput));
                    FileWriter fileWritercsv = new FileWriter(fileoutput, true);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    fileWritercsv.append("IP:PORT;Version;Players;Players Max;Ping;Message\n");
                    for (ServerPerson person : onlineServers) {
                        try {
                            fileWritercsv.append(person.getIP()).append(":").append(person.getPort()).append(";")
                                    .append(person.getVersion()).append(";").append(person.getPlayersCurent()).append(";")
                                    .append(person.getPlayersMaximum()).append(";").append(person.getPing()).append(";")
                                    .append(person.getMessage()).append("\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    fileWritercsv.append(";;;;;\n").append("Results count: ").append(String.valueOf(onlineServers.size())).append(";").append(dateFormat.format(date)).append(";;;;\n");
                    fileWritercsv.close();
                    System.exit(0);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.print("\nInvalid input file!\nFile does not exist!\n\n" +
                    "Args: -port -rate -masscanoL -ipsInput -ServersOutput\n" +
                    "Defaults: -port 25565  -rate 200  -ipsInput ipsinput.txt  -ServersOutput ServersOutput.csv\n");
        }

    }


    //Функция выбора ip адреса из файла отсканированного masscan'ом в формате -oL
    private static String getIPFromFile(BufferedReader reader) {
        try {
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    String[] lines = line.split(" ");
                    if (lines.length == 5) {
                        return lines[3];
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Функция вывода информации в консоль
    private static void putInfo() {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            while(numofline!=servers.size())
            try {
                System.out.printf("\r%s", "Online: " + onlineServers.size() + "   " + numofline + "/" + servers.size() + "   " +
                        String.format("%.2f",(float)numofline / (float)servers.size() * (float)100) + "%   Time left: " +
                        formatter.format((System.currentTimeMillis()-startTime)/numofline*(servers.size()-numofline)));
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("\r%s", "Online: " + onlineServers.size() + "   " + numofline + "/" + servers.size() + "   " +
                    String.format("%.2f",(float)numofline / (float)servers.size() * (float)100) + "%   Total time: " +
                    formatter.format((System.currentTimeMillis()-startTime)) + "\n");
        }).start();
    }

    private static String returnIp(boolean masscanfile, BufferedReader reader) throws IOException {
        if (masscanfile)
            return getIPFromFile(reader);
        else return reader.readLine();
    }

}
