import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 * Author: Jingyi(Olivia) Wu
 * Andrew id: jingyiw2
 * Email address: jingyiw2@andrew.cmu.edu
 * Javadoc for Block class: https://www.andrew.cmu.edu/course/95-702/examples/javadoc/blockchaintask0/Block.html
 *
 */

public class Block {
    // index - the position of the block on the chain
    // difficulty - specifies the minimum number of left most hex digits
    private int index, difficulty;
    // timestamp  - holds the time of the block's creation
    private Timestamp timestamp;
    // data - holding the block's single transaction details.
    // previousHash - the SHA256 hash of a block's parent, hash pointer
    // hash - this block's hash of proper difficulty
    private String Tx, previousHash, hash;
    // nonce - a BigInteger value determined by a proof of work routine. This has to be found by the proof of work logic
    private BigInteger nonce;

    public Block(int index, Timestamp timestamp, String Tx, int difficulty) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.index = index;
        this.timestamp = timestamp;
        this.Tx = Tx;
        this.difficulty = difficulty;
        this.nonce = new BigInteger("0");
        this.previousHash = "";
        this.hash = calculateHash();
        this.hash = proofOfWork();

    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     *
     * @return a String holding Hexadecimal characters
     */
    public String calculateHash() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // compute digest with SHA-256
        String message = index + Long.toString(timestamp.getTime()) + Tx + previousHash + nonce + difficulty;
        byte[] bytesOfMessage = message.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);
        return bytesToHex(bigDigest);

    }

    // Code from stack overflow
    // https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    // Returns a hex string given an array of bytes
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


    /**
     * The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
     *
     * This method calls calculateHash() to compute a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     * If the hash has the appropriate number of leading hex zeroes, it is done and returns that proper hash.
     * If the hash does not have the appropriate number of leading hex zeroes, it increments the nonce by 1 and tries again.
     *
     * @return a String with a hash that has the appropriate number of leading hex zeroes.
     * The difficulty value is already in the block.
     * This is the minimum number of hex 0's a proper hash must have.
     */
    public String proofOfWork() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0,difficulty).equals(target)) {
            nonce = nonce.add(new BigInteger("1"));
            hash = calculateHash();
        }
        return hash;
    }

    // getter method
    public int getIndex() {
        return index;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public String getTx() {
        return Tx;
    }
    public String getPreviousHash() {
        return previousHash;
    }
    public String getHash() {
        return hash;
    }
    public BigInteger getNonce(){
        return nonce;
    }
    // setter methods
    public void setIndex(int index) {
        this.index = index;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public void setTx(String tx) {
        this.Tx = tx;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setNonce(BigInteger nonce){
        this.nonce = nonce;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString(){
        // create a gson object to display class fields
        Gson gson = new Gson();
        // serialize to json
        String blockMessage = gson.toJson(this);
        // display json string
        return blockMessage;
    }
    // main method to check proof of work and nonce
    public static void main(String[] args) {
        Block block = null;
        try {
            block = new Block(0, new Timestamp(System.currentTimeMillis()), "0", 5);
            System.out.println(block.getHash());
            System.out.println(block.getNonce());
            System.out.println(block);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding exception: "+e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such algorithm exception: "+e.getMessage());
        }

    }

}
