package in.ashokit.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
@Component
public class EmailUtils {
	@Autowired
	private JavaMailSender mailSender;
	
	public boolean sendEmail(String to, String subject, String body) {
		boolean isSet = false;
		try {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body);
		mailSender.send(mimeMessage);
		isSet = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
