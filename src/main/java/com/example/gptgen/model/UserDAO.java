package com.example.gptgen.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.persistence.NoResultException;

public class UserDAO {

    // EntityManagerFactory for creating EntityManager instances
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("userPU");
    /**
     * Method to check if the username is already taken.
     * @param username The username to check.
     * @return True if the username is already taken, false otherwise.
     */
    public boolean isUsernameTaken(String username) {

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;  // If count > 0, username is taken
        } catch (Exception e) {
            return false;
        } finally {


            entityManager.close();
        }
    }

    /**
     * Method to check if the email is already taken.
     * @param email The email to check.
     * @return True if the email is already taken, false otherwise.
     */
    public boolean isEmailTaken(String email) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;  // If count > 0, email is taken
        } catch (Exception e) {
            return false;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Method to retrieve the User ID by username.
     * @param username The username whose user ID is to be fetched.
     * @return The ID of the user associated with the given username, or null if not found.
     */
    public Long getUserIdByUsername(String username) {
        System.out.println("XXXXXXXXX TEST BEFORE");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            String query = "SELECT u.id FROM User u WHERE u.username = :username";
            return entityManager.createQuery(query, Long.class)
                    .setParameter("username", username)
                    .getSingleResult();  // Will return a single result or throw NoResultException if not found
        } catch (NoResultException e) {
            // If no result is found, return null (or handle as you prefer)
            return null;
        } finally {
            System.out.println("XXXXXXXXX TEST AFTER");
            entityManager.close();
        }
    }

    /**
     * Method to register a new user.
     * @param user The User object containing user details to be registered.
     * @return True if registration is successful, false if the username or email is already taken or an error occurs.
     */
    public boolean registerUser(User user) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Check if the username or email is already taken
            if (isUsernameTaken(user.getUsername()) || isEmailTaken(user.getEmail())) {
                return false;  // Username or email is already taken
            }

            // Start a transaction to persist the new user
            entityManager.getTransaction().begin();
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); // Hash Passwort
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            return false;
        } finally {
            entityManager.close();
        }
    }

    /**
     * Method to authenticate a user by email and password.
     * @param email The email of the user to authenticate.
     * @param password The password provided by the user.
     * @return The authenticated User object if credentials are correct, or null if authentication fails.
     */
    public User authenticateUser(String email, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Fetch the user by email
            User user = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();

            // Check if the provided password matches the stored password
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user; // Return the user if authentication is successful
            }
            return null; // Return null if password doesn't match
        } catch (Exception e) {
            return null; // Return null if an error occurs
        } finally {
            entityManager.close();
        }
    }
}

