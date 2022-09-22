package com.joseerivam.libaryapi;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

//  @Autowired
//  private EmailService emailService;

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

//  @Bean
//  public CommandLineRunner runner() {
//    return args -> {
//      List<String> emails = Arrays.asList("cce51314c5-417638@inbox.mailtrap.io");
//      emailService.sendMails("Testando o serviço de emails.", emails);
//      System.out.println("EMAILS ENVIADOS COM SUCESSO!!!");
//    };
//  }

  public static void main(String[] args) {
    SpringApplication.run(LibraryApiApplication.class, args);
  }

}
