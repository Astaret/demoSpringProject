package com.example.demospringproject.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.Bean;

public class HibernateConfiguration {

    @Bean
    public SessionFactory sessionFactory() {
        Configuration configuration = new Configuration();

        configuration.addPackage("com.example.demospringproject")
                .addAnnotatedClass(ReservationEntity.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "root")
                .setProperty("hibernate.show_sql", "true");

        return configuration.buildSessionFactory();
    }
}
