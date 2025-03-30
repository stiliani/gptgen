package com.example.gptgen.model;

import com.example.gptgen.view.UserUtil;
import jakarta.persistence.EntityManager;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    // Methode zur Überprüfung, ob ein Benutzername bereits existiert

    public User getUserById(Long userId) {
        EntityManager em = UserUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(User.class, userId);
        } finally {
            em.close();
        }
    }

    public boolean isUsernameTaken(String username) {
        EntityManager em = UserUtil.getEntityManagerFactory().createEntityManager();
        try {
            long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;  // Wenn der Count > 0 ist, existiert der Benutzername bereits
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    // Methode zur Überprüfung, ob eine E-Mail-Adresse bereits existiert
    public boolean isEmailTaken(String email) {
        EntityManager em = UserUtil.getEntityManagerFactory().createEntityManager();
        try {
            long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;  // Wenn der Count > 0 ist, existiert die E-Mail-Adresse bereits
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    public boolean registerUser(User user) {
        EntityManager em = UserUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Überprüfe, ob der Benutzername oder die E-Mail bereits existiert
            if (isUsernameTaken(user.getUsername()) || isEmailTaken(user.getEmail())) {
                return false;  // Benutzername oder E-Mail ist bereits vergeben
            }

            em.getTransaction().begin();
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); // Hash Passwort
            em.persist(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public User authenticateUser(String email, String password) {
        EntityManager em = UserUtil.getEntityManagerFactory().createEntityManager();
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }
}
