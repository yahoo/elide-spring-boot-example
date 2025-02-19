package example.service;

import org.springframework.stereotype.Service;

import example.model.Mail;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending mail.
 * <p>
 * This only logs the mail for demonstration purposes. An actual implementation
 * will use either MailSender or JavaMailSender.
 * <p>
 * This is injected into the LifeCycleHook for Mail.
 *
 * @see example.model.Mail
 */
@Service
@Slf4j
public class MailService {
    /**
     * Sends a mail.
     *
     * @param mail the mail to send
     */
	public void send(Mail mail) {
		log.info("Sending mail from {} to {} with content {} with id {} by user {}", mail.getFrom(), mail.getTo(),
				mail.getContent(), mail.getId(), mail.getCreatedBy());
    }
}
