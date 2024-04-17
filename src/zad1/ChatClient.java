/**
 *
 *  @author Zadykowicz Piotr S24144
 *
 */

package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ChatClient {

    public String host;
    public int port;
    public String id;
    public ArrayList<String> chat;
    public SocketChannel socket;
    public PrintWriter out;
    public BufferedReader in;

    public ChatClient(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
        this.chat = new ArrayList<>();
        chat.add("=== " + id + " chat view");
    }

    public void login() {
        send("login;" + id);
    }

    public void logout() {
        send("logout;" + id);
    }

    public void text(String message) {
        send("text;" + message);
    }

    public String getChatView() {
           StringBuilder result = new StringBuilder();
        for(String s : chat){
            result.append(s);
            result.append("\n");
        }

        result.append("\n");

        return result.toString();
    }

    public void connection() {
        try {
            socket = SocketChannel.open(new InetSocketAddress(host, port));
            socket.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String send(String req) {
        StringBuilder response = null;
        try {
            response = new StringBuilder();
            ByteBuffer byteBuffer = ByteBuffer.wrap(req.getBytes());
            socket.write(byteBuffer);

            byteBuffer.clear();
            System.out.println("REQUEST " + req);
            if (!req.contains("logout")){
                int toRead = 0;
                while(toRead == 0) {
                   // Thread.sleep(10);
                    toRead = socket.read(byteBuffer);
                }
                String tmp = "";
                while(toRead != 0) {
                    byteBuffer.flip();

                    String tmpv2 = StandardCharsets.UTF_8.decode(byteBuffer).toString();;
                    tmp = tmp + tmpv2;
                    response.append(tmpv2);



                    byteBuffer.clear();

                    toRead = socket.read(byteBuffer);
                }

                //System.out.println(id + " OTRZYMALA " + tmp );

                chat.add(deleteChar(tmp));
            }
            else {
                String[] split = req.split(";");

                chat.add(split[1] + " logged out");
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();//.replace(";", "\n");
    }

    public String deleteChar(String text){
        StringBuilder result = new StringBuilder();
        char currentChar;
        for(int i = 0; i < text.length(); i++){
            currentChar = text.charAt(i);
            if (currentChar != ';'){
                result.append(currentChar);
            }
        }

        return result.toString();
    }

    public String getId() {
        return id;
    }

}