//Name: Tianyi Wang
//Andrew ID: tianyiwa
//Email:tianyiwang@cmu.edu

import java.security.MessageDigest;
import java.sql.Timestamp;

public class Block {
    /*Index for searching the blocks*/
    private Integer index;
    /*Timestamp for calculating time used*/
    private Timestamp timestamp;
    /*Information about the block*/
    private String data;
    /*the number of 0 in the left*/
    private Integer difficulty;
    /*the hash value of the block*/
    public String hash;
    /*Previous Hash*/
    public String previousHash;

    private int nonce;

    /*Constructor*/
    public Block(int index,
                  Timestamp timestamp,
                  String data,
                  int difficulty) {
        this.index= index;
        this.timestamp = timestamp;
        this.data=data;
        this.difficulty =difficulty;
        this.hash= calculateHash();
        proofOfWork();
    }

    /*set hash of the previous block*/
    public void setPreviousHash(String hash){
        this.previousHash = hash;
    }

    /*Encrypt*/
    public String calculateHash() {
        StringBuffer hexString = new StringBuffer();
        try {
            String input = previousHash + Long.toString(timestamp.getTime()) + Integer.toString(nonce) + data;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }catch (Exception e) {
            return hexString.toString();
        }
        return hexString.toString();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    /*Proof of work*/
    public String proofOfWork() {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }

        return hash;
    }

    public int getNonce() {
        return nonce;
    }

    /*Overwrite toString*/
    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp=" + timestamp +
                ", data='" + data + '\'' +
                ", difficulty=" + difficulty +
                ", hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", nonce=" + nonce +
                '}';
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public static void main(String[] args) {
        Block block = new Block(0, new Timestamp(System.currentTimeMillis()), "0", 5);
        System.out.println(block.proofOfWork());
        System.out.println(block.getNonce());
    }




}
