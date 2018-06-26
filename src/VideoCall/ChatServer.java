package VideoCall;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static int uniqueId;
    private ArrayList<ChatServer.ClientThread> clients;
    private int port;
    private boolean keepGoing;

    public ChatServer() {
        this.port = 8000;
        clients = new ArrayList<>();
    }

    public void start() {
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (keepGoing) {
                System.out.println("ChatServer waiting for Clients on port " + port + ".");
                Socket socket = serverSocket.accept();
                if (!keepGoing) {
                    break;
                }
                ChatServer.ClientThread t = new ChatServer.ClientThread(socket);
                clients.add(t);
                t.start();
                send("login~" + t.username + "~" + t.username + " sedang login...~Server~\n");
            }
            try {
                serverSocket.close();
                for (int i = 0; i < clients.size(); ++i) {
                    ChatServer.ClientThread tc = clients.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                    }
                }
            } catch (Exception e) {
                System.out.println("Exception closing the server and clients: " + e);
            }
        } catch (IOException e) {
            String msg = "Exception on new ServerSocket: " + e + "\n";
            System.out.println(msg);
        }
    }

    private synchronized void send(String message) {
        for (int i = clients.size(); --i >= 0;) {
            ChatServer.ClientThread ct = clients.get(i);
            if (!ct.writeMsg(message)) {
                clients.remove(i);
                System.out.println("Disconnected Client " + ct.username + " removed from list.");
            }
        }
    }

    private String getClients() {
        String s = "";
        for (ClientThread clientThread : clients) {
            s += clientThread.username + ":";
        }
        s += "---";
        System.out.println(s);
        return s;
    }

    private synchronized void remove(int id) {
        for (int i = 0; i < clients.size(); ++i) {
            ChatServer.ClientThread ct = clients.get(i);
            if (ct.id == id) {
                clients.remove(i);
                return;
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }

    private class ClientThread extends Thread {

        private Socket socket;
        private ObjectInputStream sInput;
        private ObjectOutputStream sOutput;
        private int id;
        private String username;

        public ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            System.out.println("Menciptakan Object Input/Output Streams");
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                String message = (String) sInput.readObject();
                username = message.split("~")[1];
                System.out.println(username + " masuk.");
            } catch (IOException e) {
                System.out.println("Exception creating new Input/output Streams: " + e);
            } catch (ClassNotFoundException e) {
            }
        }

        @Override
        public void run() {
            while (true) {

                String message;
                try {
                    message = sInput.readObject().toString();
                } catch (IOException e) {
                    System.out.println(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }

                String type = message.split("~")[0];
                String pengirim = message.split("~")[1];
                String text = message.split("~")[2];
                String kepada = message.split("~")[3];
                String response;

                switch (type) {
                    case "postText":
                        response = "recieveText~" + pengirim + "~" + text + "~" + kepada + "~\n";
                        send(response);
                        break;
                    case "postPrivateText":
                        response = "recievePrivateText~" + pengirim + "~" + text + "~" + kepada + "~\n";
                        send(response);
                        break;
                    case "login":
                        response = "login~" + pengirim + "~" + text + "~" + kepada + "~\n";
                        send(response);
                        break;
                    case "logout":
                        response = "logout~" + pengirim + "~" + text + "~" + kepada + "~\n";
                        send(response);
                        break;
                    case "list":
                        response = "list~server~" + getClients() + "~ ~ ~ ~ ~\n";
                        send(response);
                        break;
                }
            }

            remove(id);
            close();
        }

        private void close() {
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (Exception e) {
            }

            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ignored) {
            }
        }

        private boolean writeMsg(String msg) {
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                System.out.println("Error sending message to " + username);
                System.out.println(e.toString());
            }
            return true;
        }
    }
}
