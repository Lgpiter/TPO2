/**
 *
 *  @author Zadykowicz Piotr S24144
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

public class ChatServer {

    public String host;
    public int port;
    public ArrayList<String> log;
    public Thread threadServer;
    public HashMap<SocketChannel, ChatClientHandling> clients = new HashMap<>();
    public ServerSocketChannel serverSocketChannel;
    public Selector selector;

    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.log = new ArrayList<>();
    }

    public void startServer() {
        System.out.println("Server started");
        threadServer = new Thread(() -> {
            try {
                selector = Selector.open();
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.bind(new InetSocketAddress(host, port));
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, serverSocketChannel.validOps(), null);

                while (!threadServer.isInterrupted()) {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKeys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();

                        if (key.isAcceptable()) {
                            SocketChannel clientSocket = serverSocketChannel.accept();
                            clientSocket.configureBlocking(false);
                            clientSocket.register(selector, SelectionKey.OP_READ);
                            clients.put(clientSocket, new ChatClientHandling(clientSocket));
                        }

                        if (key.isReadable()) {
                            SocketChannel clientSocket = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            clientSocket.read(buffer);

                            String clientRequest = new String(buffer.array()).trim();
                            String[] parts = clientRequest.split(";");

                            if(parts[0].equals("login")) {
                                logIn(clientSocket, parts);

                            }
                            else if(parts[0].equals("logout")) {
                                logOut(clientSocket, parts);
                            }
                            else if(parts[0].equals("text")) {
                                message(clientSocket, parts);
                            }
                        }

                        iter.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        threadServer.start();
    }

    public void stopServer() {
        try {
            threadServer.interrupt();
            Thread.sleep(300);
            serverSocketChannel.close();
            selector.close();
            System.out.println("Server stopped");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void logIn(SocketChannel socketChannel, String[] parts) {
        clients.get(socketChannel).setId_Client(parts[1]);
        String message = parts[1] + " logged in";
        log.add(LocalTime.now() + " " + message + "\n");

        broadcast(message);
    }

    private void logOut(SocketChannel socketChannel, String[] parts) {
        clients.remove(socketChannel);
        String message = parts[1] + " logged out";

        log.add(LocalTime.now() + " " + message + "\n");

        broadcast(message);
    }

    private void message(SocketChannel socketChannel, String[] parts) {
        String id = clients.get(socketChannel).getId_Client();
        String message = id + ": ";
        for(int i = 1; i < parts.length; i++) {
            message = message + parts[i];

            if(i != parts.length - 1) {
                message = message + " ";
            }

        }

        log.add(LocalTime.now() + " " + message + "\n");
        broadcast(message);
    }

    private void broadcast(String message) {

        CharBuffer cB = CharBuffer.wrap(message);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(cB);
            for (SocketChannel socketChannel : clients.keySet()) {
                try {
                    System.out.println("BROADCAST MESSAGE " + message);
                    System.out.println(socketChannel);
                    socketChannel.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }




    public String getServerLog() {
        StringBuilder s = new StringBuilder();

        for (String string : log){
            s.append(string);
        }

        return s.toString();
    }
}

