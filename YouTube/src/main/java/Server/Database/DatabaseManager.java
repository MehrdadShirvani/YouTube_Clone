package Server.Database;

import Shared.Models.Channel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("mainPersistentUnit");


}
