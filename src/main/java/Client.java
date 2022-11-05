import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try (
                Scanner scanner = new Scanner(System.in);
                Socket socket = new Socket("127.0.0.1", 8989);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println(in.readLine());
            out.println(scanner.nextLine());
            String s1 = in.readLine();
            while (s1 != null) {
                System.out.println(s1);
                s1 = in.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}