import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Author: Jingyi(Olivia) Wu
 * Andrew id: jingyiw2
 * Email address: jingyiw2@andrew.cmu.edu
 * Javadoc for blockchain class: https://www.andrew.cmu.edu/course/95-702/examples/javadoc/blockchaintask0/BlockChain.html
 * This BlockChain has exactly three instance members - an ArrayList to hold Blocks and a chain hash to hold a SHA256 hash of the most recently added Block.
 */

public class BlockChain {
    private ArrayList<Block> ds_chain;
    private String chainHash;
    private int hashesPerSecond;

    // It also maintains an instance variable holding the approximate number of hashes per second on this computer.
    public BlockChain() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        ds_chain = new ArrayList<>();
        chainHash = "";
        long start = System.currentTimeMillis();
        computeHashesPerSecond();
        long end = System.currentTimeMillis();
        long time = end - start;
        hashesPerSecond = (int)(2000000/(time/1000.0));
    }

    /**
     * To add a new block to current blockchain.
     * @param block the new block to be added
     * timing analysis: it takes only 0~4 ms to add a block of difficulty 2; about 6 ms to add a block of difficulty 3;
     *              and 50~180ms to add a block of difficulty 4.
     */
    public void addBlock(Block block){
        // new block's previous hash must hold the hash of the most recently added block.
        block.setPreviousHash(chainHash);
        ds_chain.add(block);
        // new block becomes the most recently added block
        chainHash = block.getHash();
    }

    /**
     * hashes per second is approximated as (2 million / number of seconds).
     * It is run on start up and sets the instance variable hashesPerSecond. It uses a simple string - "00000000" to hash.
     */
    public void computeHashesPerSecond() throws UnsupportedEncodingException, NoSuchAlgorithmException {

        String hashString = "00000000";
        byte[] bytesOfMessage = hashString.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);
        String hashResult = bytesToHex(bigDigest);
        for (int i=1; i< 2000000; i++){
            bytesOfMessage = hashString.getBytes("UTF-8");
            md = MessageDigest.getInstance("SHA-256");
            bigDigest = md.digest(bytesOfMessage);
            hashResult = bytesToHex(bigDigest);
        }
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }
    public String getChainHash() {
        return chainHash;
    }
    public int getChainSize() {
        return ds_chain.size();
    }

    public Block getLatestBlock() {
        return ds_chain.get(ds_chain.size()-1);
    }
    // get current system time
    public Timestamp getTime(){
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        return currentTime;
    }
    // Compute and return the total difficulty of all blocks on the chain. Each block knows its own difficulty.
    public int getTotalDifficulty(){
        int total = 0;
        for (Block block: ds_chain){
            total += block.getDifficulty();
        }
        return total;
    }
    // Compute and return the expected number of hashes required for the entire chain.
    public double getTotalExpectedHashes(){
        double total = 0;
        for (Block block: ds_chain){
            total += 1/Math.pow(1.0/16, block.getDifficulty());
        }
        return total;
    }

    /**
     * If the chain only contains one block, the genesis block at position 0, this routine computes the hash of the block and checks.
     * It also checks that the chain hash is equal to this computed hash.
     * If either check fails, return an error message.
     * Otherwise, return the string "TRUE".
     *
     * If the chain has more blocks than one, begin checking from block one.
     * Continue checking until you have validated the entire chain.
     * The first check will involve a computation of a hash in Block 0 and a comparison with the hash pointer in Block 1.
     * If they match and if the proof of work is correct, go and visit the next block in the chain.
     * At the end, check that the chain hash is also correct.
     *
     * @return "TRUE" if the chain is valid, otherwise return a string with an appropriate error message
     *
     * Timing analysis: For verify validness part, it takes only few ms.
     */
    public String isChainValid(){
        String prev = "";
        for (int i = 0; i< ds_chain.size(); i++){
            Block currBlock =  ds_chain.get(i);
            String curr = currBlock.getHash();
            // check if previous hash matches
            if (!currBlock.getPreviousHash().equals(prev)){
                return "FALSE\nImproper previous hash on node:"+i;
            }
            prev = curr;
            // check proof of work
            char[] charHash = curr.toCharArray();
            int difficulty = currBlock.getDifficulty();
            char[] target = new char[difficulty];
            for (int j = 0; j< difficulty; j++){
                target[j] = '0';
            }
            for (int j = 0; j< difficulty; j++){
                if (charHash[j]!='0'){
                    return "FALSE\nImproper hash on node "+i+" does not begin with "+String.valueOf(target);
                }
            }
        }
        return "TRUE";
    }
    // get block on position i
    public Block getBlock(int i){
        return ds_chain.get(i);
    }

    // menu option0: instead of printing out, return a responseMessage object
    public ResponseMessage displayBlochChain(){
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setSelection(0);
        responseMessage.setSize(getChainSize());
        responseMessage.setChainHash(getChainHash());
        responseMessage.setTotalHashes(getTotalExpectedHashes());
        responseMessage.setTotalDiff(getTotalDifficulty());
        responseMessage.setRecentNonce(getLatestBlock().getNonce());
        responseMessage.setDiff(getLatestBlock().getDifficulty());
        responseMessage.setHps(getHashesPerSecond());
        return responseMessage;
    }

    // menu option1: add a transaction to blockchain
    public void addTransaction(Scanner sc) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        System.out.println("Enter difficulty > 0");
        int difficulty = Integer.parseInt(sc.nextLine());
        System.out.println("Enter transaction");
        String message = sc.nextLine();

        Block block = new Block(getChainSize(), new Timestamp(System.currentTimeMillis()), message, difficulty);
        addBlock(block);
    }
    // menu option2: verify blockchain
    public void verifyChain(){
        System.out.println("Chain verification: "+isChainValid());
    }
    // menu option3: view blockchain
    public void viewChain(){
        System.out.println(this);
    }

    @Override
    public String toString(){
        // Create a Gson object
        Gson gson = new Gson();
        // Serialize to JSON
        String chainMessage = gson.toJson(this);
        return chainMessage;
    }
    // menu option4: corrupt chain
    public void corruptChain(Scanner sc) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        System.out.println("corrupt the Blockchain");
        System.out.println("Enter block ID of block to corrupt");
        int corruptId = Integer.parseInt(sc.nextLine());
        System.out.println("Enter new data for block "+corruptId);
        String message = sc.nextLine();
        Block corruptBlock = getBlock(corruptId);
        corruptBlock.setTx(message);
        corruptBlock.setHash(corruptBlock.calculateHash());
//        corruptBlock.proofOfWork();
        System.out.println("Block "+corruptId+" now holds "+message);
    }
    // menu option5: repair the chain
    /**
     *  It checks the hashes of each block and ensures that any illegal hashes are recomputed.
     *  After this routine is run, the chain will be valid. The routine does not modify any difficulty values.
     *  It computes new proof of work based on the difficulty specified in the Block.
     *
     *  Timing analysis: Total time here is number of blocks*proof of work time for each block
     */
    public void repairChain() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hash = "";
        for (int i = 0; i<getChainSize(); i++){
            Block block = getBlock(i);
            block.setPreviousHash(hash);
            hash = block.proofOfWork();
        }
        this.chainHash = hash;
    }

}
