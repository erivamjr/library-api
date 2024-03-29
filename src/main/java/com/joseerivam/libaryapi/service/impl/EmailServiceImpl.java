package com.joseerivam.libaryapi.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.joseerivam.libaryapi.service.EmailService;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  @Value("${application.mail.default-remetent}")
  private String remetent;

  @Autowired
  private final JavaMailSender javaMailSender;

  @Override
  public void sendMails(String message, List<String> mailsList) {
    // TODO Auto-generated method stub

    String[] mails = mailsList.toArray(new String[mailsList.size()]);

    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setFrom(remetent);
    mailMessage.setSubject("Livro com empréstimo atrasado");
    mailMessage.setText(message);
    mailMessage.setTo(mails);
    javaMailSender.send(mailMessage);

  }

}
