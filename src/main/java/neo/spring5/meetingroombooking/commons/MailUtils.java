package neo.spring5.meetingroombooking.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailUtils {

        @Value("${spring.mail.username}")
        private String from;

        @Autowired
        JavaMailSender emailSender;

        public void sendEmail(String to, String mailSubject, String body) {

            log.info("MailUtils - Inside sendSimpleMessage method");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to.split(","));
            message.setSubject(mailSubject);
            message.setText(body);

            emailSender.send(message);
        }
}
