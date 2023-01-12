//Name: Tianyi Wang
//Andrew ID: tianyiwa
//Email:tianyiwang@cmu.edu

public class RequestMessage {

    public String message;
    /*index*/
    public Integer selection = 0;

    /*Data*/
    public String data;

    /*Difficulties*/
    public Integer diff=0;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getDiff() {
        return diff;
    }

    public void setDiff(Integer diff) {
        this.diff = diff;
    }

    public Integer getSelection() {
        return selection;
    }

    public void setSelection(Integer selection) {
        this.selection = selection;
    }

    public RequestMessage() {
    }

    public RequestMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return
                "{\"message\":\"" + message + "\"" +
                ", \"selection\":\"" + selection+ "\""+
                ", \"data\":\"" + data +  "\"" +
                ", \"diff\":\"" + diff +"\""+
                '}';
    }
}
