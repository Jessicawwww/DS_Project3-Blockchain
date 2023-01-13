import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpResponse;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * Author: Jingyi(Olivia) Wu
 * Andrew id: jingyiw2
 * Email address: jingyiw2@andrew.cmu.edu
 * Blockchain exists on the server.
 */
public class ServerTCP {
    private BlockChain blockChain;
    private Socket clientSocket;
    private ServerSocket aSocket;

    public ServerTCP() throws IOException, NoSuchAlgorithmException {
        this.blockChain = new BlockChain();
        Block block0 = new Block(0,new Timestamp(System.currentTimeMillis()),"Genesis",2);
        this.blockChain.addBlock(block0);
        this.aSocket = new ServerSocket(7777);
    }

    public BlockChain getBlockChain() {
        return blockChain;
    }

    // send message back to client
    public void respond(ResponseMessage responseMessage) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
        String response = responseMessage.toString().replace("\n","");
//        System.out.println(response);
        out.println(response);
        out.flush();
    }

    public static void main(String[] args) {
        try {
            ServerTCP server = new ServerTCP();
            System.out.println("Blockchain server running");
            server.clientSocket = server.aSocket.accept();
            Scanner sc = new Scanner(server.clientSocket.getInputStream());
            System.out.println("We have a visitor");

            Gson gson = new Gson();
            String result = "";
            boolean flag = true;
            while (flag){
                // receive from client
                if (server.clientSocket == null){
                    server.clientSocket = server.aSocket.accept();
                    sc = new Scanner(server.clientSocket.getInputStream());
                }
                try{
                    result = sc.nextLine();
                } catch (Exception e){
                    server.clientSocket.close();
                    server.clientSocket = null;
                    continue;
                }
                // extract request message from client json
                RequestMessage requestMessage = gson.fromJson(result, RequestMessage.class);
                int option = requestMessage.getSelection();
                ResponseMessage responseMessage;
                switch (option){
                    case 0:
                        // convert to ResponseMessage Class and print it out
                        responseMessage = server.getBlockChain().displayBlochChain();
                        System.out.println("Response: "+responseMessage);
                        server.respond(responseMessage);
                        break;
                    case 1:
                        // add a block and calculate its execution time
                        long start = System.currentTimeMillis();
                        System.out.println("Adding a block");
                        String transactionString = requestMessage.getTx();
                        int diff = requestMessage.getDiff();
                        Block newBlock = new Block(server.getBlockChain().getChainSize(), new Timestamp(System.currentTimeMillis()),transactionString,diff);
                        server.getBlockChain().addBlock(newBlock);
                        long end = System.currentTimeMillis();
                        long time = end - start;
                        System.out.println("Setting response to Total execution time to add this block was "+time+" milliseconds");
                        // send response to client
                        responseMessage = server.getBlockChain().displayBlochChain();
                        responseMessage.setSelection(1);
                        responseMessage.setBlockChain(server.getBlockChain());
                        System.out.println(responseMessage.getResponse(time));
                        server.respond(responseMessage);
                        break;
                    case 2:
                        // verify blockchain
                        long start2 = System.currentTimeMillis();
                        System.out.println("Verifying entire chain");
                        String isValidChain = server.getBlockChain().isChainValid();
                        System.out.println("Chain verification: "+isValidChain);
                        // send response to client
                        responseMessage = server.getBlockChain().displayBlochChain();
                        responseMessage.setSelection(2);
                        responseMessage.setBlockChain(server.getBlockChain());
                        server.respond(responseMessage);
                        long end2 = System.currentTimeMillis();
                        long time2 = end2-start2;
                        System.out.println("Total execution time required to verify the chain was "+time2+" milliseconds");
                        System.out.println("Setting response to Total execution time to verify the chain was "+time2+" milliseconds");
                        break;
                    case 3:
                        // view blockchain
                        System.out.println("View the Blockchain");
                        System.out.println("Setting response to "+server.getBlockChain());
                        responseMessage = server.getBlockChain().displayBlochChain();
                        responseMessage.setBlockChain(server.getBlockChain());
                        server.respond(responseMessage);
                        break;
                    case 4:
                        // corrupt blockchain
                        System.out.println("Corrupt the Blockchain");
                        transactionString = requestMessage.getTx();
                        int corruptId = requestMessage.getCorruptId();
                        Block block = server.getBlockChain().getBlock(corruptId);
                        block.setTx(transactionString);
                        block.setHash(block.calculateHash());
                        System.out.println("Block "+corruptId+" now holds "+transactionString);
                        System.out.println("Setting response to Block "+corruptId+" now holds "+transactionString);
                        // send back to client
                        responseMessage = server.getBlockChain().displayBlochChain();
                        responseMessage.setBlockChain(server.getBlockChain());
                        server.respond(responseMessage);
                        break;
                    case 5:
                        // repair the blockchain
                        System.out.println("Repairing the entire chain");
                        long start3 = System.currentTimeMillis();
                        server.getBlockChain().repairChain();
                        long end3 = System.currentTimeMillis();
                        long time3 = end3-start3;
                        System.out.println("Setting response to Total execution time required to repair the chain was "+time3+" milliseconds");
                        // send back to client
                        responseMessage = server.getBlockChain().displayBlochChain();
                        responseMessage.setBlockChain(server.getBlockChain());
                        server.respond(responseMessage);
                        break;
                    case 6:
                        flag = false;
                        responseMessage = server.getBlockChain().displayBlochChain();
                        server.respond(responseMessage);
                        break;
                    default:
                        System.out.println("Invalid Option!");
                }
            }
            if(server.aSocket!=null){
                server.aSocket.close();
            }
        } catch (IOException e) {
            System.out.println("IOException message: "+e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException message: "+e.getMessage());
        }
    }
}
