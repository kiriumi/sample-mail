package jp.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang3.RandomStringUtils;

import jp.example.entity.Attachement;
import jp.example.entity.Mail;

public class MainJpa {

    private static EntityManager em;

    public static void main(String[] args) throws IOException {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("sampleUnit");
        em = factory.createEntityManager();

        em.getTransaction().begin();

        insertEntity(
                new File("attachment1.txt"), new File("hoge.pdf"), new File("ver1-1.jpg"), new File("無線通信技術の種類.png"));

        em.getTransaction().commit();
        em.close();
    }

    private static void insertEntity(File... files) throws IOException {

        String id = RandomStringUtils.randomNumeric(2);

        Mail mail = new Mail();
        mail.setId(id);
        em.persist(mail);

        for (File file : files) {

            Attachement attachement = new Attachement();
            attachement.setId(id);
            attachement.setFilename(file.getName());
            attachement.setAttachement(Files.readAllBytes(file.toPath()));

            em.persist(attachement);
        }

    }

}
