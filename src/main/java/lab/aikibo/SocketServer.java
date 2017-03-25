package lab.aikibo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by tamami on 24/03/17.
 */
public class SocketServer implements Runnable {

    public ServerSocket server = null;
    public Thread thread = null;
    public int port = 8086;
    public ServerFrame ui;
    public ServerThread clients[];
    public Database db;
    public int clientCount = 0;

    public SocketServer(ServerFrame frame) {
        new SocketServer(frame, this.port);
    }

    public SocketServer(ServerFrame frame, int port) {
        clients = new ServerThread[50];
        ui = frame;
        db = new Database(ui.filePath);

        try {
            server = new ServerSocket(port);
            port = server.getLocalPort();
            //ui.jTextArea1.append("Server starter. IP : " + InetAddress.getLocalHost() + ", Port : " +
            //        server.getLocalPort());
            ui.getTaInfo().appendText("Server starter. IP : " + InetAddress.getLocalHost() + ", Port : " +
                        server.getLocalPort());
            start();
        } catch (IOException e) {
            e.printStackTrace();
            //ui.jTextArea1.append("cannot bind to port : " + port + "\nRetrying");
            //ui.retryStart(0);
            ui.getTaInfo().appendText("cannot bind to port : " + port + "\nRetrying");
            ui.retryStart(0);
        }
    }

    public void run() {
        while(thread != null) {
            try {
                //ui.jTextArea1.append("\nWaiting for client...");
                ui.getTaInfo().appendText("\nWaiting for client...");
                addThread(server.accept());
            } catch(Exception e) {
                //ui.jTextArea1.append("\nServer accept error: \n");
                //ui.retryStart(0);
                ui.getTaInfo().appendText("\nServer accept error: \n");
                ui.retryStart(0);
            }
        }
    }

    public void start() {
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if(thread != null) {
            thread.stop();
            thread = null;
        }
    }

    private int findClient(int id) {
        for(int i=0; i<clientCount; i++) {
            if(clients[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void handle(int id, Message msg) {
        if(msg.content.equals(".bye")) {
            announce("signout", "SERVER", msg.sender);
            remove(id);
        } else {
            if(msg.type.equals("login")) {
                if(findUserThread(msg.sender) == null) {
                    if(db.checkLogin(msg.sender, msg.content)) {
                        clients[findClient(id)].username = msg.sender;
                        clients[findClient(id)].send(new Message (
                           "login", "SERVER", "TRUE", msg.sender
                        ));
                        announce("newuser", "SERVER", msg.sender);
                        sendUserList(msg.sender);
                    } else {
                        clients[findClient(id)].send(new Message(
                           "login", "SERVER", "FALSE", msg.sender
                        ));
                    }
                } else {
                    clients[findClient(id)].send(new Message(
                            "login", "SERVER", "FALSE", msg.sender
                    ));
                }
            } else if(msg.type.equals("message")) {
                if(msg.recipient.equals("All")) {
                    announce("message", msg.sender, msg.content);
                } else {
                    findUserThread(msg.recipient).send(new Message(
                            msg.type, msg.sender, msg.content, msg.recipient
                    ));
                    clients[findClient(id)].send(new Message(
                            msg.type, msg.sender, msg.content, msg.recipient
                    ));
                }
            } else if(msg.type.equals("test")) {
                clients[findClient(id)].send(new Message(
                        "test", "SERVER", "OK", msg.sender
                ));
            } else if(msg.type.equals("signup")) {
                if(findUserThread(msg.sender) == null) {
                    if(!db.userExists(msg.sender)) {
                        db.addUser(msg.sender, msg.content);
                        clients[findClient(id)].username = msg.sender;
                        clients[findClient(id)].send(new Message(
                                "signup", "SERVER", "TRUE", msg.sender
                        ));
                        clients[findClient(id)].send(new Message(
                                "login", "SERVER", "TRUE", msg.sender
                        ));
                        announce("newuser", "SERVER", msg.sender);
                        sendUserList(msg.sender);
                    } else {
                        clients[findClient(id)].send(new Message(
                                "signup", "SERVER", "FALSE", msg.sender
                        ));
                    }
                } else {
                    clients[findClient(id)].send(new Message(
                            "signup", "SERVER", "FALSE", msg.sender
                    ));
                }
            } else if(msg.type.equals("upload_req")) {
                if(msg.recipient.equals("All")) {
                    clients[findClient(id)].send(new Message(
                            "message", "SERVER", "Uploading to 'All' forbidden", msg.sender
                    ));
                } else {
                    findUserThread(msg.recipient).send(new Message(
                            "upload_req", msg.sender, msg.content, msg.recipient
                    ));
                }
            } else if(msg.type.equals("upload_res")) {
                if(!msg.content.equals("NO")) {
                    String ip = findUserThread(msg.sender).socket.getInetAddress().getHostAddress();
                    findUserThread(msg.recipient).send(new Message(
                            "upload_res", ip, msg.content, msg.recipient
                    ));
                } else {
                    findUserThread(msg.recipient).send(new Message(
                            "upload_res", msg.sender, msg.content, msg.recipient
                    ));
                }
            }
        }
    }

    public void announce(String type, String sender, String content) {
        Message msg = new Message(type, sender, content, "All");
        for(int i=0; i<clientCount; i++) {
            clients[i].send(msg);
        }
    }

    public void sendUserList(String toWhom) {
        for(int i=0; i<clientCount; i++) {
            findUserThread(toWhom).send(new Message(
                    "newuser", "SERVER", clients[i].username, toWhom
            ));
        }
    }

    public ServerThread findUserThread(String usr) {
        for(int i=0; i<clientCount; i++) {
            if(clients[i].username.equals(usr)) {
                return clients[i];
            }
        }

        return null;
    }

    public synchronized void remove(int id) {
        int pos = findClient(id);

        if(pos >= 0) {
            ServerThread toTerminate = clients[pos];
            //ui.jTextArea1.append("\nRemoving client thread " + id + " at " + pos);
            if(pos < clientCount - 1) {
                for(int i=pos+1; i<clientCount; i++) {
                    clients[i-1] = clients[i];
                }
            }
            clientCount--;
            try {
                toTerminate.close();
            } catch(IOException e) {
                //ui.jTextArea1.append("\nError closing thread: " + e);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
        if(clientCount <  clients.length) {
            //ui.jTextArea1.append("\nclient accepted: " + socket);
            clients[clientCount] = new ServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException e) {
                e.printStackTrace();
                //ui.jTextArea1.append("\nError opening thread : " + e);
            }
        } else {
            //ui.jTextArea1.append("\nclient refused: maximum " + clients.length + " reached.");
        }
    }
}
