package lab.aikibo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Created by tamami on 23/03/17.
 */
public class Database {

    public String filePath;

    public Database(String filePath) {
        this.filePath = filePath;
    }

    public boolean userExists(String username) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");

            for(int temp =0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(getTagValue("username", eElement).equals(username)) {
                        return true;
                    }
                }
            }
            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkLogin(String username, String password) {
        if(!userExists(username)) {
            return false;
        }

        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("user");

            for(int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if(nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(getTagValue("username", eElement).equals(username) &&
                            getTagValue("password", eElement).equals(password)) {
                        return true;
                    }
                }
            }
            System.out.println("Yes");
            return false;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addUser(String username, String password) {
        try {
            
        }
    }

}
