package jp.example.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Attachement {

    @Id
    private String id;

    private String filename;

    private byte[] attachement;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getAttachement() {
        return attachement;
    }

    public void setAttachement(byte[] attachement) {
        this.attachement = attachement;
    }

}
