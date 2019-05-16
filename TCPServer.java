package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {

    static ArrayList<Socket> clients = new ArrayList<>();

    static class HandleMsg implements Runnable {

        Socket clientSocket;

        PrintWriter os = null;
        String msg = null;

        public HandleMsg(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        @Override
        public void run() {
            clients.add(clientSocket);  //添加进客户端线程池
            msg = "欢迎【" + clientSocket.getRemoteSocketAddress() + "】加入聊天室！聊天室当前人数为：" + clients.size();
            sendMsg(msg);
            BufferedReader is = null;
            try {
                //从InputStream中读取客户端发来的数据
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while ((msg = is.readLine()) != null) {
                    if (msg.equals("exit")) {
                        //客户端发送exit退出
                        msg = "【" + clientSocket.getRemoteSocketAddress() + "】下线了.";
                        sendMsg(msg);
                        System.out.println(msg);
                        os = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()));
                        os.println("exit");
                        os.flush();
                        break;
                    } else {
                        msg = "【" + clientSocket.getRemoteSocketAddress() + "】说： " + msg;
                        sendMsg(msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                    clientSocket.close();
                    clients.remove(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void sendMsg(String msg) {
            for (int i = clients.size() - 1; i >= 0; --i) {
                try {
                    os = new PrintWriter(new BufferedOutputStream(clients.get(i).getOutputStream()));
                    os.println(msg);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket ss = null;
        Socket clientSocket = null;
        ExecutorService tp = Executors.newCachedThreadPool();
        ss = new ServerSocket(65000);
        System.out.println("Server " + ss.getLocalPort() + " started!");
        while (true) {
            try {
                clientSocket = ss.accept();
                System.out.println(clientSocket.getRemoteSocketAddress() + " connect!");
                tp.execute(new HandleMsg(clientSocket));
            } catch (IOException e) {
                System.out.println(e);
            }
        }


    }
}
