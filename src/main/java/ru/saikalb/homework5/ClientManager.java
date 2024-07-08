package ru.saikalb.homework5;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientManager implements Runnable {
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;
    public static List<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            broadcastMessage("SERVER: " + name + " has connected to the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (!socket.isClosed()) {
            try {
                messageFromClient = bufferedReader.readLine();

                // В случае, если клиент желает покинуть чат при помощи команды "exit"
                if (Objects.equals(messageFromClient, "exit")) {
                    System.out.println("Client " + name + " is disconnected and left chat!");
                    broadcastMessage("Client " + name + " is disconnected and left chat!");
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;

                    // Персональное сообщение участнику чата
                } else if (messageFromClient.startsWith(name + ": @")) {
                    String[] split = messageFromClient.split("\\s+");
                    String loginTo = split[1].substring(1);
                    String pureMessage = messageFromClient.replace(name + ": @" + loginTo + " ", "");
                    privateMessage(pureMessage, loginTo);

                    // Сообщение всем участникам чата
                } else {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String messageToSend) {
        for (ClientManager client : clients) {
            try {

                // Отправляем сообщение всем участникам чата, кроме себя
                if (!client.name.equals(name)) {
                    client.bufferedWriter.write(messageToSend);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void privateMessage(String messageToSend, String loginTo) {
        for (ClientManager client : clients) {
            try {
                if (client.name.equals(loginTo)) {
                    client.bufferedWriter.write(name + ": " + messageToSend);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
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
            System.out.println(e.getMessage());
        }
    }

    public void removeClient() {
        clients.remove(this);
        System.out.println(name + " left chat.");
        broadcastMessage("SERVER: " + name + " left chat.");
    }

    public String getName() {
        return name;
    }
}
