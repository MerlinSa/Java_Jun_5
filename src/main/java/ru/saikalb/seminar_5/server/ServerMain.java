package ru.saikalb.seminar_5.server;

public class ServerMain {
    public static final int SERVER_PORT = 8181;

    public static void main(String[] args) {
        // TCP - протол передачи данных, который гарантирует доставку
        // IP-адрес
        // TCP-порт -

        new Server().start(SERVER_PORT);

    }

}
