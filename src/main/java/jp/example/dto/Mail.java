package jp.example.dto;

import java.util.List;

/**
 *
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table public.mail
 */
public class Mail {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column public.mail.id
     *
     * @mbg.generated
     */
    private String id;

    private List<Attachement> attachments;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column public.mail.id
     *
     * @return the value of public.mail.id
     *
     * @mbg.generated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column public.mail.id
     *
     * @param id the value for public.mail.id
     *
     * @mbg.generated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public List<Attachement> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachement> attachments) {
        this.attachments = attachments;
    }
}
