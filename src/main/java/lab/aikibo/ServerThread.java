package lab.aikibo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by tamami on 23/03/17.
 */
public class ServerThread extends Thread {

    public SocketServer server = null;
    public Socket socket = null;
    public int id = -1;
    public ObjectOutputStream streamOut = null;
    public ObjectInputStream streamIn = null;

    public ServerThread(SocketServer server, Socket socket) {
        super();
        this.server = server;
        this.socket = socket;
        id = socket.getPort();
        ui = server.ui;
    }

    public void send(Message msg) {
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public int getID() {
        return id;
    }

    public void run() {
        public void run() {
            //ui.jTextArea1.append("\nServer thread: " + id + " berjalan.");

            while(true) {
                try {
                    Message msg = (Message) streamIn.readObject();
                    server.handle(id, msg);
                }catch(Exception e) {
                    System.out.println(id + " error saat membaca " + e.getMessage());
                    server.remove(id);
                    stop();
                }
            }
        }
    }
}
