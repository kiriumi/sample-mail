package jp.example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;

import jp.example.dto.Attachement;
import jp.example.dto.Mail;
import jp.example.repository.AttachementMapper;
import jp.example.repository.MailMapper;

public class MainMyBatis {

    public static void main(String[] args) throws AddressException, MessagingException, IOException {

        initSqlSession();

        String id = insertAttachment(
                new File("attachment1.txt"), new File("hoge.pdf"), new File("ver1-1.jpg"), new File("無線通信技術の種類.png"));

        sendMail("attach@example.com", "テスト添付メール", "テスト添付メールだよ", "56");

        sqlSession.close();

        return;
    }

    static SqlSession sqlSession = null;

    private static void initSqlSession() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        sqlSession = factory.openSession();
    }

    public static String insertAttachment(File... files) throws IOException {

        AttachementMapper attachmentMapper = sqlSession.getMapper(AttachementMapper.class);
        MailMapper mailMapper = sqlSession.getMapper(MailMapper.class);

        String id = RandomStringUtils.randomNumeric(4);

        Mail mail = new Mail();
        mail.setId(id);
        mailMapper.insert(mail);

        for (File file : files) {
            Attachement attachment = new Attachement();
            attachment.setId(id);
            attachment.setFilename(file.getName());
            attachment.setAttachement(Files.readAllBytes(file.toPath()));

            attachmentMapper.insert(attachment);
        }

        sqlSession.commit();

        return id;
    }

    private static String smtpHost = ResourceBundle.getBundle("ApplicationConfig").getString("mail.smtp.host");

    public static void sendMail(String email, String title, String message)
            throws MessagingException, AddressException {

        Properties property = new Properties();
        property.put("mail.smtp.host", smtpHost);

        Session session = Session.getInstance(property, null);
        MimeMessage mimeMessage = new MimeMessage(session);

        InternetAddress toAddress = new InternetAddress(email);
        mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);

        InternetAddress fromAddress = new InternetAddress("sender@example.com");
        mimeMessage.setFrom(fromAddress);

        mimeMessage.setSubject(title, "UTF-8");
        mimeMessage.setText(message, "UTF-8");

        Transport.send(mimeMessage);
    }

    public static void sendMail(String email, String title, String message, String id)
            throws MessagingException, AddressException, IOException {

        Properties property = new Properties();
        property.put("mail.smtp.host", smtpHost);

        Session session = Session.getInstance(property, null);

        MimeMessage mimeMessage = new MimeMessage(session);

        InternetAddress toAddress = new InternetAddress(email);
        mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);

        InternetAddress fromAddress = new InternetAddress("sender@example.com");
        mimeMessage.setFrom(fromAddress);
        mimeMessage.setSubject(title, "UTF-8");

        MimeMultipart multiPart = new MimeMultipart();

        MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setText(message, "UTF-8");
        multiPart.addBodyPart(messagePart);

        MailMapper mapper = sqlSession.getMapper(MailMapper.class);
        Mail record = mapper.selectMail(id);
        sqlSession.close();

        for (Attachement attachement : record.getAttachments()) {

            if (attachement.getAttachement() == null) {
                continue;
            }

            TikaInputStream stream = TikaInputStream.get(attachement.getAttachement());
            String contentType = new Tika().detect(stream);

            DataSource dataSource = new ByteArrayDataSource(attachement.getAttachement(), contentType);
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setDataHandler(new DataHandler(dataSource));
            attachmentPart.setFileName(attachement.getFilename());

            multiPart.addBodyPart(attachmentPart);
        }

        mimeMessage.setContent(multiPart);
        Transport.send(mimeMessage);
    }

}
