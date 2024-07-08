package ru.saikalb.homework5;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int CLIENT_PORT = 1300;
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final String name;
    // флаговая переменная для завершения второго потока
    private volatile boolean isClosed;

    public Client(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        isClosed = false;
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (!isClosed) {
                String message = scanner.nextLine();

                // Команда покинуть чат: "exit"
                if (Objects.equals(message.toLowerCase(), "exit")) {
                    isClosed = true;
                    bufferedWriter.write("exit");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                }
                bufferedWriter.write(name + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            scanner.close();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
        closeEverything(socket, bufferedReader, bufferedWriter);
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String messageFromGroup;
            while (!isClosed) {
                try {
                    messageFromGroup = bufferedReader.readLine();
                    System.out.println(messageFromGroup);
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person("Alisa", 35);
        mapper.writeValue(new File("person.json"),person );

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name, please.");
        String name = scanner.nextLine();
        try (Socket socket = new Socket(HOST, CLIENT_PORT)) {
            Client client = new Client(socket, name);
            InetAddress inetAddress = socket.getInetAddress();
            System.out.println("InetAddress: " + inetAddress);
            String remoteIp = inetAddress.getHostAddress();
            System.out.println("Remote IP: " + remoteIp);
            System.out.println("Local port: " + socket.getLocalPort());

            client.listenForMessage();
            client.sendMessage();
            client.closeEverything(socket, client.bufferedReader, client.bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
