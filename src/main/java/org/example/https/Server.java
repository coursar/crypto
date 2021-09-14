package org.example.https;

import lombok.extern.java.Log;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

@Log
public class Server {
  public static void main(String[] args) {
    System.setProperty("javax.net.ssl.keyStore", "certs/server.jks");
    System.setProperty("javax.net.ssl.keyStorePassword", "passphrase");
    System.setProperty("javax.net.ssl.trustStore", "certs/truststore.jks");
    System.setProperty("javax.net.ssl.trustStorePassword", "passphrase");

    final var server = new Server();
    server.listen(9999);
  }

  public void listen(int port) {
    // TLS/SSL

    final var factory = SSLServerSocketFactory.getDefault();

    try (
        final var serverSocket = ((SSLServerSocket) factory.createServerSocket(port));
    ) {
      serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
      serverSocket.setNeedClientAuth(true);

      while (true) {
        try (
            final var socket = ((SSLSocket) serverSocket.accept());
            final var out = socket.getOutputStream();
        ) {
          socket.addHandshakeCompletedListener(event -> {
            try {
              log.log(Level.INFO, event.getPeerPrincipal().toString());
            } catch (SSLPeerUnverifiedException e) {
              e.printStackTrace();
            }
          });

          final var body = "Ok";
          out.write(
              (
                  // language=HTTP
                  "HTTP/1.1 200 OK\r\n" +
                  "Content-Length: " + body.length() + "\r\n" +
                  "Content-Type: application/json\r\n" +
                  "Connection: close\r\n" +
                  "\r\n" +
                  body
              ).getBytes(StandardCharsets.UTF_8)
          );
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
