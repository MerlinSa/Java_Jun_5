package ru.saikalb.seminar_5.http;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

public class HttpMain {

    public static void main(String[] args) throws IOException {
        // RestTemplate
        // WebClient
        URLConnection urlConnection = URI.create("https://github.com/golang/go").toURL().openConnection();
        urlConnection.connect();

        // TCP, UDP - протокол для взаимодействия по сети
        // TLS\SSL (Transport Layer Security \ Secure Socket Layer)
        // HTTP, Protobuf, ... - протоколы-форматы для сообщений
        // HTTPS = HTTP + Secure
    }

}

