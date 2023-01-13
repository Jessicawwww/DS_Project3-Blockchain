import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Author: Jingyi(Olivia) Wu
 * Andrew id: jingyiw2
 * Email address: jingyiw2@andrew.cmu.edu
 * Client focuses on driving menu interaction and communicating with server.
 */
public class ClientTCP {
    private int port;
    private InetAddress aHost;
    private Socket clientSocket;

    public ClientTCP() throws IOException {
        port = 7777;
        aHost = InetAddress.getByName("localhost");
        clientSocket = new Socket(aHost, port);
    }

    public void send(RequestMessage requestMessage) throws IOException {
        // send to server through TCP
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
        out.println(requestMessage.toString());
        out.flush();
    }
    public ResponseMessage receive() throws IOException {
        // receive response from server
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String response = in.readLine();
        Gson gson = new Gson();
        ResponseMessage responseMessage = gson.fromJson(response, ResponseMessage.class);
        return responseMessage;
    }

    public static void main(String[] args) {
        try {
            ClientTCP clientTCP = new ClientTCP();
            System.out.println("The client is running.");
            Scanner sc = new Scanner(System.in);
            boolean flag = true;
            while(flag){
                System.out.println("0. View basic blockchain status.\n" +
                        "1. Add a transaction to the blockchain.\n" +
                        "2. Verify the blockchain.\n" +
                        "3. View the blockchain.\n" +
                        "4. Corrupt the chain.\n" +
                        "5. Hide the corruption by repairing the chain.\n" +
                        "6. Exit");
                int option = Integer.parseInt(sc.nextLine());
                RequestMessage requestMessage;
                ResponseMessage responseMessage;
                switch (option){
                    case 0:
                        requestMessage = new RequestMessage();
                        clientTCP.send(requestMessage);
                        responseMessage = clientTCP.receive();
                        System.out.println("Current size of chain: "+responseMessage.getSize());
                        System.out.println("Difficulty of most recent block: "+responseMessage.getDiff());
                        System.out.println("Total difficulty for all blocks: "+responseMessage.getTotalDiff());
                        System.out.println("Approximate hashes per second on this machine: "+responseMessage.getHps());
                        System.out.println("Expected total hashes required for the whole chain: "+responseMessage.getTotalHashes());
                        System.out.println("Nonce for most recent block: "+responseMessage.getRecentNonce());
                        System.out.println("Chain hash: "+responseMessage.getChainHash());
                        break;
                    case 1:
                        requestMessage = new RequestMessage();
                        System.out.println("Enter difficulty > 0");
                        int diff = Integer.parseInt(sc.nextLine());
                        System.out.println("Enter transaction");
                        String trasactionString = sc.nextLine();
                        requestMessage.setSelection(1);
                        requestMessage.setDiff(diff);
                        requestMessage.setTx(trasactionString);
                        long starttime = System.currentTimeMillis();
                        clientTCP.send(requestMessage);
//                        responseMessage = clientTCP.receive();
                        long endtime = System.currentTimeMillis();
                        long time = endtime-starttime;
                        System.out.println("Total execution time to add this block was "+time+" milliseconds");
                        break;
                    case 2:
                        starttime = System.currentTimeMillis();
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(2);
                        clientTCP.send(requestMessage);
                        responseMessage = clientTCP.receive();
                        endtime = System.currentTimeMillis();
                        System.out.println("Chain verification: "+responseMessage.getBlockChain().isChainValid());
                        time = endtime - starttime;
                        System.out.println("Total execution time to verify the chain was "+time+" milliseconds");
                        break;
                    case 3:
                        System.out.println("View the Blockchain");
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(3);
                        clientTCP.send(requestMessage);

                        responseMessage = clientTCP.receive();
                        System.out.println(responseMessage.getBlockChain());
                        break;
                    case 4:
                        System.out.println("corrupt the Blockchain");
                        System.out.println("Enter block ID of block to corrupt");
                        int corruptId = Integer.parseInt(sc.nextLine());
                        System.out.println("Enter new data for block "+corruptId);
                        trasactionString = sc.nextLine();
                        System.out.println("Block "+corruptId+" now holds "+trasactionString);

                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(4);
                        requestMessage.setCorruptId(corruptId);
                        requestMessage.setTx(trasactionString);
                        clientTCP.send(requestMessage);
                        break;
                    case 5:
                        starttime = System.currentTimeMillis();
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(5);
                        clientTCP.send(requestMessage);
                        responseMessage = clientTCP.receive();
                        endtime = System.currentTimeMillis();
                        time = endtime - starttime;
                        System.out.println("Total execution time required to repair the chain was "+time+" milliseconds");
                        break;
                    case 6:
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(6);
                        clientTCP.send(requestMessage);
                        flag = false;
                        break;
                    default:
                        System.out.println("Invalid input option!");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
