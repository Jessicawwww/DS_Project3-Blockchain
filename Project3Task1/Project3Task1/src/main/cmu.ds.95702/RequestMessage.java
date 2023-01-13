import com.google.gson.Gson;

/**
 * Author: Jingyi(Olivia) Wu
 * Andrew id: jingyiw2
 * Email address: jingyiw2@andrew.cmu.edu
 * This class is used for encapsulating requests from client.
 */

public class RequestMessage {
//    private String message;
    private int selection;
    private String Tx;
    private int diff;
    private int corruptId;

    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /* constructors */
    public RequestMessage(){
        this.selection = 0;
        this.diff = 0;
    }
//    public RequestMessage(String message){
//        this.message = message;
//        this.selection = 0;
//        this.diff = 0;
//    }
    /* getter and setter methods  */
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }

    public int getCorruptId() {
        return corruptId;
    }

    public void setCorruptId(int corruptId) {
        this.corruptId = corruptId;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public String getTx() {
        return Tx;
    }

    public void setTx(String tx) {
        Tx = tx;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }
}
