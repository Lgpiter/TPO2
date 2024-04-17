package zad1;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class ChatClientHandling {

    public ArrayList<String> log;
    public String id_Client;
    public SocketChannel socketClient;

    public ChatClientHandling(SocketChannel socketClient){
        this.log = new ArrayList<String>();
        this.socketClient = socketClient;
    }

    public ArrayList<String> getLog() {
        return log;
    }

    public String getId_Client() {
        return id_Client;
    }

    public void setId_Client(String id_Client) {
        this.id_Client = id_Client;
    }

    public SocketChannel getSocketClient() {
        return socketClient;
    }

    public void setLog(ArrayList<String> log) {
        this.log = log;
    }

    public void setSocketClient(SocketChannel socketClient) {
        this.socketClient = socketClient;
    }

}
