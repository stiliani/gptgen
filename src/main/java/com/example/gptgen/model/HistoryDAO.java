package com.example.gptgen.model;

import java.util.List;
import jakarta.persistence.*;

public class HistoryDAO {

    // EntityManagerFactory for creating EntityManager instances
    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("historyPU");

    /**
     * Method to retrieve all history entries for a specific user by user ID.
     * @param userId The ID of the user whose history entries are to be fetched.
     * @return A list of History objects related to the specified user.
     */
    public List<History> getHistoriesByUserId(Long userId) {
        System.out.println("XXX Retrieving history for user ID: " + userId);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            // Creating a query to fetch histories based on userId
            String query = "SELECT h FROM History h WHERE h.userId = :userid";
            TypedQuery<History> typedQuery = entityManager.createQuery(query, History.class);
            typedQuery.setParameter("userid", userId);

            // Returning the list of histories for the given userId
            return typedQuery.getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Method to save a new history entry.
     * @param history The History object to be persisted in the database.
     */
    public void saveHistory(History history) {
        System.out.println("XXX Saving history entry");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(history); // Persisting the history object into the database
            transaction.commit();
        } catch (Exception e) {
            // Rolling back the transaction in case of an error
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("XXX Error saving history");
            e.printStackTrace();
        } finally {
            // Closing the EntityManager to release resources
            entityManager.close();
        }
    }

    /**
     * Method to delete a history entry by its prompt ID.
     * @param promptid The ID of the history entry to be deleted.
     */
    public void deleteHistoryById(Long promptid) {
        System.out.println("XXX Deleting history entry with ID: " + promptid);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            // Finding the history entry by its prompt ID
            History history = entityManager.find(History.class, promptid);

            if (history != null) {
                entityManager.remove(history); // Removing the history entry if found
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("XXX Error deleting history");
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
}

