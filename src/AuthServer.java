import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Code by MiLiBlue, At 2022/11/12
 **/
public class AuthServer {

    public static AuthServer instance = new AuthServer();
    public File key = new File("key.txt");
    public File accounts = new File("Account.txt");
    public String runPath = null;
    public static void main(String[] args) throws IOException {
        System.out.printf("Server start init...\n");
        try {
            // ���������socket
            ServerSocket serverSocket = new ServerSocket(54130);

            // �����ͻ���socket
            Socket socket = new Socket();

            //��ȡ����Ŀ¼
            AuthServer.instance.runPath = new File("").getCanonicalPath();
            if(!AuthServer.instance.accounts.exists()){
                instance.accounts.createNewFile();
            }
            if(!AuthServer.instance.key.exists()){
                instance.key.createNewFile();
            }

            System.out.printf("Server init done!");

            //ѭ�������ȴ��ͻ��˵�����
            while(true){
                // �����ͻ���
                socket = serverSocket.accept();

                AuthServerThread thread = new AuthServerThread(socket);
                thread.start();

                InetAddress address=socket.getInetAddress();
                System.out.println("New Client Connect! IP:"+address.getHostAddress());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
