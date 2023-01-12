//Name: Tianyi Wang
//Andrew ID: tianyiwa
//Email:tianyiwang@cmu.edu

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Scanner;

public class BlockServerTCP {

    public BlockChain blockChain;
    public String option;
    public Socket clientSocket;
    public ServerSocket aSocket;

    
    /*initialize the server*/
    public BlockServerTCP() throws IOException {
        System.out.println("Blockchain server running");
        this.aSocket = new ServerSocket(7777);
        this.blockChain = new BlockChain();
        /*initialize the original block*/
        Block block = new Block(0,new Timestamp(System.currentTimeMillis()),"Genesis",2);
        this.blockChain.addBlock(block);
    }



    /*send message*/
    public void send(ResponseMessage responseMessage) throws IOException {
        PrintWriter out;
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
        String message = responseMessage.toString().replace("\n","").trim();
        System.out.println(message);
        out.println(message);
        out.flush();
    }


    public static void main(String args[]) throws IOException {
        
        BlockServerTCP server = new BlockServerTCP();
        try {
            server.clientSocket = server.aSocket.accept();
            Scanner in = new Scanner(server.clientSocket.getInputStream());
            System.out.println("We have a visitor");
            String result;
            while (true) {
                if(server.clientSocket == null) {
                    /*Initialize the Server*/
                    server.clientSocket = server.aSocket.accept();
                    in = new Scanner(server.clientSocket.getInputStream());
                }
                try {
                    result = in.nextLine();
                }catch (Exception e) {
                    server.clientSocket.close();
                    server.clientSocket = null;
                    continue;
                }
                /*convert requested json to requestMessage object*/
                RequestMessage requestMessage = new RequestMessage();
                String replace = result.replace("{", "").replace("}", "");
                String[] split = replace.split(",");
                for (String s : split) {
                    String[] split1 = s.split(":");
                    String key = split1[0].replace("\"", "").trim();
                    String value = split1[1].replace("\"", "").trim();
                    if(value.equals("null")) {
                        value="";
                    }
                    if(key.equals("message")) {
                        requestMessage.setMessage(value);
                    }
                    if(key.equals("selection")) {
                        requestMessage.setSelection(Integer.parseInt(value));
                    }
                    if(key.equals("data")) {
                        requestMessage.setData(value);
                    }
                    if(key.equals("diff")) {
                        requestMessage.setDiff(Integer.parseInt(value));
                    }
                 }
                if(requestMessage.getMessage().equals("0")) {
                    ResponseMessage responseMessage = server.blockChain.showBlockchainStatus();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Current size of chain:"+server.blockChain.getChainSize()+";");
                    stringBuffer.append("Difficulty of most recent block:"+server.blockChain.getLatestBlock().getDifficulty()+";");
                    stringBuffer.append("Total difficulty for all blocks:"+server.blockChain.getTotalDifficulty()+";");
                    stringBuffer.append("Approximate hashes per second on this machine:"+server.blockChain.getHashesPerSecond()+";");
                    stringBuffer.append("Expected total hashes required for the whole chain:"+server.blockChain.getTotalExpectedHashes()+";");
                    stringBuffer.append("Nonce for most recent block:"+server.blockChain.getLatestBlock().getNonce()+";");
                    stringBuffer.append("Chain hash:"+ server.blockChain.getChainHash()+";");
                    responseMessage.setMessage(stringBuffer.toString());
                    server.send(responseMessage);
                }
                /*add blocks*/
                if(requestMessage.getMessage().equals("1")) {
                    long starttime = System.currentTimeMillis();
                    System.out.println("Adding a block");
                    String data = requestMessage.getData();
                    Integer diff = requestMessage.getDiff();
                    Block block = new Block(server.blockChain.getChainSize(),new Timestamp(System.currentTimeMillis()),data,diff);
                    server.blockChain.addBlock(block);
                    long endtime = System.currentTimeMillis();
                    long times = endtime - starttime;
                    String format = "Setting response to Total execution time to add this block was "+times+" milliseconds";
                    System.out.println(format);
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("null");
                    responseMessage.setSelection(server.blockChain.getChainSize());
                    server.send(responseMessage);
                }
                /*verify the blocks*/
                if(requestMessage.getMessage().equals("2")) {
                    long starttime = System.currentTimeMillis();
                    System.out.println("Verifying entire chain"); 
                    String chainValid = server.blockChain.isChainValid();
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage(chainValid);
                    server.send(responseMessage);
                    long endtime = System.currentTimeMillis();
                    long times = endtime - starttime;
                    System.out.println("Total execution time required to verify the chain was "+times+" milliseconds");
                    System.out.println("Setting response to Total execution time to verify the chain was  "+times+" milliseconds");
                }
                /*view the blockchain*/
                if(requestMessage.getMessage().equals("3")) {
                    System.out.println("View the Blockchain");
                    ResponseMessage responseMessage = new ResponseMessage();
                    String s = server.blockChain.toString();
                    responseMessage.setMessage(s);
                    server.send(responseMessage);
                }
                /*corrupt the blockchain*/
                if(requestMessage.getMessage().equals("4")) {
                    System.out.println("Corrupt the Blockchain");
                    String data = requestMessage.getData();
                    Integer selection = requestMessage.getSelection();
                    Block block = server.blockChain.getBlock(selection);
                    block.setData(data);
                    block.setHash(block.calculateHash());
                    block.proofOfWork();
                    String format = "Block "+selection+" now holds "+data;
                    System.out.println(format);
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("null");
                    server.send(responseMessage);
                }
                /*repaire the blockchain*/
                if(requestMessage.getMessage().equals("5")) {
                    long starttime = System.currentTimeMillis();
                    System.out.println("Repairing the entire chain");
                    server.blockChain.repireChain();
                    long endtime = System.currentTimeMillis();
                    long times = endtime - starttime;
                    String format = "Setting response to Total execution time required to repair the chain was "+times+" milliseconds";
                    System.out.println(format);
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("null");
                    server.send(responseMessage);
                }
                if(requestMessage.getMessage().equals("6")) {
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("null");
                    server.send(responseMessage);
                }


            }
        }catch(SocketException e){
            System.out.println("Socket: " + e.getMessage());
        }catch(IOException e){
            System.out.println("IO: " + e.getMessage());
        }finally{
            if (server.aSocket != null) {
                try {
                    server.aSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
