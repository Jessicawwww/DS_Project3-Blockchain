import java.math.BigInteger;
import com.google.gson.Gson;
/**
 * Author: Jingyi(Olivia) Wu
 * Andrew id: jingyiw2
 * Email address: jingyiw2@andrew.cmu.edu
 * This class is used for encapsulating responses from server.
 */

public class ResponseMessage {
//    private String message;
    private int selection, size;
    private String chainHash;
    private double totalHashes;
    private int totalDiff;
    private BigInteger recentNonce;
    private int diff, hps;
    private BlockChain blockChain ;
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public BlockChain getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    public String getResponse(long time){
        String response = "ResponseMessage: {\"selection\": "+selection+",\"response\":\"Total execution time to add this block was "+time+" milliseconds\"}";
        return response;
    }
    /* constructors */
    public ResponseMessage(){

    }
//    public ResponseMessage(String message){
//        this.message = message;
//    }
    /* getter and setter methods  */
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getChainHash() {
        return chainHash;
    }

    public void setChainHash(String chainHash) {
        this.chainHash = chainHash;
    }

    public double getTotalHashes() {
        return totalHashes;
    }

    public void setTotalHashes(double totalHashes) {
        this.totalHashes = totalHashes;
    }

    public int getTotalDiff() {
        return totalDiff;
    }

    public void setTotalDiff(int totalDiff) {
        this.totalDiff = totalDiff;
    }

    public BigInteger getRecentNonce() {
        return recentNonce;
    }

    public void setRecentNonce(BigInteger recentNonce) {
        this.recentNonce = recentNonce;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public int getHps() {
        return hps;
    }

    public void setHps(int hps) {
        this.hps = hps;
    }
}
