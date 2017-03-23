package lab.aikibo;

import java.io.Serializable;

/**
 * Created by tamami on 23/03/17.
 */
public class Message implements Serializable {

    public String type, sender, content, recipient;

    public Message(String type, String sender, String content, String recipient) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
    }

    public String toString() {
        return "{type='" + type + "', sender='" + sender + "', content='" + content + "', recipient='" +
            recipient + "'}";
    }

}
