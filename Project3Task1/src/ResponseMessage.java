//Name: Tianyi Wang
//Andrew ID: tianyiwa
//Email:tianyiwang@cmu.edu

public class ResponseMessage {
   public String message;

   public Integer selection;

   public Integer size;

   public String chainHash;

   public double totalHashes;

   public Integer totalDiff;

   public Integer recentNonce;

   public Integer diff;

   public Integer hps;

    @Override
    public String toString() {
        return "ResponseMessage{ message='" + message + '\'' +
                ", selection=" + selection +
                ", size=" + size +
                ", chainHash='" + chainHash + '\'' +
                ", totalHashes=" + totalHashes +
                ", totalDiff=" + totalDiff +
                ", recentNonce=" + recentNonce +
                ", diff=" + diff +
                ", hps=" + hps +
                '}';
    }


    public Integer getSelection() {
        return selection;
    }

    public void setSelection(Integer selection) {
        this.selection = selection;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
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

    public Integer getTotalDiff() {
        return totalDiff;
    }

    public void setTotalDiff(Integer totalDiff) {
        this.totalDiff = totalDiff;
    }

    public Integer getRecentNonce() {
        return recentNonce;
    }

    public void setRecentNonce(Integer recentNonce) {
        this.recentNonce = recentNonce;
    }

    public Integer getDiff() {
        return diff;
    }

    public void setDiff(Integer diff) {
        this.diff = diff;
    }

    public Integer getHps() {
        return hps;
    }

    public void setHps(Integer hps) {
        this.hps = hps;
    }

    public ResponseMessage() {
    }

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

