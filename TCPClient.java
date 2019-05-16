package socket;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

    Socket clientSocket = null;
    BufferedReader reader = null;
    private static final int PORT = 65000;

    public static void main(String[] args) throws IOException {
        new TCPClient();
        System.exit(0);
    }

    public TCPClient() {
        clientSocket = new Socket();
        try {
            clientSocket.connect(new InetSocketAddress("localhost", PORT));
            new getInputStream().start();
            System.out.println("Client started!");
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String receiveStr = null;
            while ((receiveStr = reader.readLine()) != null) {
                //接收服务端发来的数据
                if (receiveStr.equals("exit")) {
                    break;
                }
                System.out.println(receiveStr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class getInputStream extends Thread {
        @Override
        public void run() {
            BufferedReader is = null;
            PrintWriter os = null;
            try {
                is = new BufferedReader(new InputStreamReader(System.in));
                os = new PrintWriter(new BufferedOutputStream(clientSocket.getOutputStream()));
                String str = null;
                while ((str = is.readLine()) != null) {
                    os.println(str);
                    os.flush();
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
