//Name: Tianyi Wang
//Andrew ID: tianyiwa
//Email:tianyiwang@cmu.edu

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class BlockChain {
    public static ArrayList<Block> blockArrayList;
    /*Store the hash value of the blocks that are added in the chain*/
    public  static String chainHash;

    public int hashesPerSecond;
    public Scanner scanner= new Scanner(System.in);

    /*Constructor*/
    public BlockChain() {
        blockArrayList=new ArrayList<>();
        chainHash= "";
        hashesPerSecond=0;

    }


    public String getChainHash() {
        return chainHash;
    }
    /*Add blocks
    * It takes several ms for adding a block in difficulty 2,
    * 10 to 20 ms for adding a block in difficulty 3
    * 50 ms for adding a block in difficulty 4*/
    public void addBlock(Block block) {
        block.setPreviousHash(chainHash);
        blockArrayList.add(block);
        chainHash = block.getHash();
    }


    /*Calculate the time it takes to calculate 2 million hashes*/
    public void computeHashesPerSecond() {
        String hashString = "00000000";
        byte[] bytesOfMessage;
        MessageDigest md;
        byte[] bigDigest;
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars;
        try{
            for(int count = 0; count < 2000000; count++){
                bytesOfMessage = hashString.getBytes("UTF-8");
                md = MessageDigest.getInstance("SHA-256");
                bigDigest = md.digest(bytesOfMessage);
                hexChars= new char[bigDigest.length * 2];
                for (int j = 0; j < bigDigest.length; j++) {
                    int v = bigDigest[j] & 0xFF;
                    hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                    hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    /*Get the number of blocks*/
    public int getChainSize() {
        return blockArrayList.size();
    }

    /*Hashes per second*/
    public int getHashesPerSecond() {
        long starttime = System.currentTimeMillis();
        computeHashesPerSecond();
        long endtime = System.currentTimeMillis();
        long totaltime = endtime - starttime;
        return (int) (2000000 / (totaltime/1000.0));
    }

    /*Get the info of the last block*/
    public Block getLatestBlock() {
        return blockArrayList.get(blockArrayList.size()-1);
    }

    /*Timestamp to get the system time*/
    public Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }

    /*Total difficulty of the blockchain*/
    public int getTotalDifficulty() {
        int sum =0;
        for (Block block1 : blockArrayList) {
            sum += block1.getDifficulty();
        }
        return sum;
    }
    /*Compute and return the expected number of hashes required for the entire chain*/
    public double getTotalExpectedHashes() {
        double sum = 0;
        for (Block block : blockArrayList) {
            sum += 1 / Math.pow((1.0 / 16), block.getDifficulty());
        }
        return sum;
    }

    /*Verify the block
    * Regardless of the difficulty, it only takes several ms to verify the blocks*/
    public String isChainValid() {
        /*identify whether hash corresponds to its difficulty and whether current hash is the same as its next block's previous hash*/
        String prehash ="";

        for(int i=0;i<blockArrayList.size();i++) {
            Block block = blockArrayList.get(i);
            String hash = block.getHash();
            /*hash correspondence*/
            if(!block.getPreviousHash().equals(prehash)) {
                return "..Improper previous hash on node "+i;
            }
            prehash = hash;
            /*number of 0*/
            Integer difficulty = block.getDifficulty();
            char[] chars = hash.toCharArray();
            int num = 0;
            for (int j = 0; j <chars.length; j++) {
                if(String.valueOf(chars[j]).equals("0")) {
                    num +=1;
                }else {
                    break;
                }
            }
            if (num!=difficulty) {
                return "..Improper hash on node "+i+ "Does not begin with "+difficulty;
            }
        }
        return "TRUE";
    }

    /*get the block according to the index*/
    public Block getBlock(int index) {
        return blockArrayList.get(index);
    }




    /*Menu*/
    public void showBlockchainStatus() {
        System.out.println("Current size of chain:"+getChainSize());
        System.out.println("Difficulty of most recent block:"+getLatestBlock().getDifficulty());
        System.out.println("Total difficulty for all blocks:"+getTotalDifficulty());
        System.out.println("Approximate hashes per second on this machine:"+getHashesPerSecond());
        System.out.println("Expected total hashes required for the whole chain:"+getTotalExpectedHashes());
        System.out.println("Nonce for most recent block:"+getLatestBlock().getNonce());
        System.out.println("Chain hash:"+BlockChain.chainHash);
    }


    public void addtran() {

        System.out.println("Enter difficulty > 0");
        int diff = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter transaction");
        String message = scanner.nextLine();
        long starttime = System.currentTimeMillis();
        Block block = new Block(getChainSize(),new Timestamp(System.currentTimeMillis()),message,diff);
        addBlock(block);
        long endtime = System.currentTimeMillis();
        long times = endtime - starttime;
        System.out.println("Total execution time to add this block was "+times +"milliseconds");
    }


    public void checkChain() {
        System.out.println("Chain verification: "+isChainValid());
    }

    public void showChain() {
        System.out.println(this);
    }

    /*corrupt the chain*/
    public void corruptChain() {
        System.out.println("corrupt the Blockchain");
        System.out.println("Enter block ID of block to corrupt");
        int nextid = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter new data for block "+nextid);
        String message = scanner.nextLine();
        System.out.println(message);
        Block block = blockArrayList.get(nextid);
        block.setData(message);
        block.setHash(block.calculateHash());
        block.proofOfWork();
        System.out.println("Block "+nextid+" now holds "+message);
    }
    /*repair the chain
    * It takes about the number of blocks times the adding time for the blocks to repair the whole chain*/
    public void repireChain() {
        String hash ="";
        for(int i = 0; i < getChainSize(); i++){
            Block block = getBlock(i);
            block.setPreviousHash(hash);
            hash = block.proofOfWork();
        }
        chainHash = hash;
    }
    /*show the menu*/
    public void show() {
        while (true) {
            System.out.println("0. View basic blockchain status.");
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. corrupt the chain.");
            System.out.println("5. Hide the corruption by repairing the chain.");
            System.out.println("6. Exit");
            int input = Integer.parseInt(scanner.nextLine());
            /*show the status of the chain*/
            if(input==0) {
                showBlockchainStatus();
            }
            /*add transactions to the chain*/
            if(input==1) {
                addtran();

            }
            /*verify the chain*/
            if(input==2) {
                long starttime = System.currentTimeMillis();
                checkChain();
                long endtime = System.currentTimeMillis();
                long times = endtime - starttime;
                System.out.println("Total execution time to verify the chain was  "+times+" milliseconds");
            }
            /*show the info*/
            if(input==3) {
                showChain();
            }
            if(input==4) {
                corruptChain();
            }
            if(input == 5) {
                long starttime = System.currentTimeMillis();
                repireChain();
                long endtime = System.currentTimeMillis();
                long times = endtime - starttime;
                System.out.println("Total execution time required to repair the chain was "+times+" milliseconds");
            }
            /*exit*/
            if(input==6) {
                break;
            }

        }

    }
/*Since adding external jar files might lead to confusions, I use hashmap and stringbuffer to concatenate the messages into json*/
    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        for (Block block : blockArrayList) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("index",block.getIndex());
            Date dates = new Date(block.getTimestamp().getTime());
            hashMap.put("time stamp ",simpleDateFormat.format(dates));
            hashMap.put("Tx ",block.getData());
            hashMap.put("PrevHash",block.getPreviousHash());
            hashMap.put("nonce",block.getNonce());
            hashMap.put("difficulty",block.getDifficulty());
            list.add(hashMap);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("chainHash",chainHash);
        map.put("ds_chain",list);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{\n");
        stringBuffer.append("\"chainHash\":"+chainHash+",");
        stringBuffer.append("\"ds_chain\": [");
        for (HashMap<String, Object> mm : list) {
            stringBuffer.append("{");
            stringBuffer.append("\"index\":\""+ mm.get("index")+"\"");
            stringBuffer.append("\"time stamp \":\""+ mm.get("time stamp ")+"\"");
            stringBuffer.append("\"Tx \":\""+ mm.get("Tx ")+"\"");
            stringBuffer.append("\"PrevHash \":\""+ mm.get("PrevHash")+"\"");
            stringBuffer.append("\"nonce \":\""+ mm.get("nonce")+"\"");
            stringBuffer.append("\"difficulty \":\""+ mm.get("difficulty")+"\"");
            stringBuffer.append("},");
        }
        stringBuffer.append("]\n");
        stringBuffer.append("}");


        return stringBuffer.toString();

    }

    public static void main(String[] args) {
        BlockChain blockChain = new BlockChain();
        /*initialize an original block*/
        Block block = new Block(0,new Timestamp(System.currentTimeMillis()),"Genesis",2);
        blockChain.addBlock(block);

        /*show the menu*/
        blockChain.show();
    }



}
