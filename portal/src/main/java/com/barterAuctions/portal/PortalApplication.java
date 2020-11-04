package com.barterAuctions.portal;

import com.barterAuctions.portal.services.CategoryService;
import org.apache.catalina.session.StandardSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;

@SpringBootApplication
public class PortalApplication  {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }

}
