package ru.saikalb.seminar_5.client;

import ru.saikalb.seminar_5.server.ServerMain;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        System.out.println("Введите ваш логин: ");
        String login = console.nextLine();

        Socket server = new Socket("localhost", ServerMain.SERVER_PORT);
        System.out.println("Подключение к серверу успешно");
        Scanner in = new Scanner(server.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());

        Map<String, String> msg = new HashMap<>();
        msg.put("login", login);
        out.writeObject(msg);

//        out.println(login);

        // Поток на чтение
        new Thread(() -> {
            while (true) {
                String message = in.nextLine();
                System.out.println("Новое сообщение: " + message);

                // TODO: exit пока не реализовываем
            }
        }).start();

        // Поток на запись
        new Thread(() -> {
            while (true) {
                String inputFromConsole = console.nextLine();
                System.out.println(inputFromConsole);
            }
        }).start();
    }

}
