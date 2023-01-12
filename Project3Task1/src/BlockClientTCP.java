//Name: Tianyi Wang
//Andrew ID: tianyiwa
//Email:tianyiwang@cmu.edu

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;


public class BlockClientTCP {
    public int port;
    public InetAddress host;
    public Socket clientSocket;


    /*Initialize the client*/
    public BlockClientTCP() throws IOException {
        port = 7777;
        host = InetAddress.getByName("localhost");
        clientSocket = new Socket(host,port);
    }

    public void init() {
        System.out.println("0. View basic blockchain status.");
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the blockchain.");
        System.out.println("3. View the blockchain.");
        System.out.println("4. corrupt the chain.");
        System.out.println("5. Hide the corruption by repairing the chain.");
        System.out.println("6. Exit");
    }


    public void clientSend(RequestMessage requestMessage){
        try {

            /*Send message*/
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            String message =requestMessage.toString();
            out.println(message);
            out.flush();
            /*Receive message from the server*/
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            /*json to response object*/
            String ms = in.readLine().split("message=")[1].split(", selection")[0].replace("'","");
            if(requestMessage.getMessage().equals("0")) {
                String[] split = ms.split(";");
                for (String s : split) {
                    System.out.println(s);
                }
            }
            if(!ms.equals("null")) {

                System.out.println(ms);
            }


        }catch (SocketException e) {System.out.println("Socket: " + e.getMessage());
        }catch (IOException e){System.out.println("IO: " + e.getMessage());}
    }

    public static void main(String args[]) throws IOException {
        System.out.println("The client is running.");
        BlockClientTCP client = new BlockClientTCP();
        Scanner scanner = new Scanner(System.in);


        while(true) {
            client.init();
            int choice = Integer.parseInt(scanner.nextLine());
            /*view the status of the blockchain*/
            if(choice==0) {
                RequestMessage requestMessage = new RequestMessage(String.valueOf(choice));
                client.clientSend(requestMessage);
            }
            /*menus*/
            if(choice == 1){
                System.out.println("Enter difficulty > 0");
                int diff = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter transaction");
                String message = scanner.nextLine();
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setMessage("1");
                requestMessage.setData(message);
                requestMessage.setDiff(diff);
                client.clientSend(requestMessage);

            }else if(choice == 2){
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setMessage("2");
                client.clientSend(requestMessage);
            }else if(choice == 3){
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setMessage("3");
                client.clientSend(requestMessage);
            }else if(choice == 4){
                System.out.println("Enter block ID of block to corrupt");
                int nextid = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter new data for block "+nextid);
                String message = scanner.nextLine();
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setMessage("4");
                requestMessage.setData(message);
                requestMessage.setSelection(nextid);
                client.clientSend(requestMessage);
            }else if(choice == 5){
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setMessage("5");
                client.clientSend(requestMessage);
            }else if(choice == 6){
                RequestMessage requestMessage = new RequestMessage();
                requestMessage.setMessage("6");
                client.clientSend(requestMessage);
                break;
            }
        }
    }



}
