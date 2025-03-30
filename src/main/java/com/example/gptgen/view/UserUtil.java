package com.example.gptgen.view;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class UserUtil {
    private static final EntityManagerFactory entityManagerFactory =
            Persistence.createEntityManagerFactory("userPU");

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static void shutdown() {
        entityManagerFactory.close();
    }
}