package org.example;

import org.example.dao.GenericDAO;
import org.example.entities.*;
import org.example.service.CrudService;
import org.example.service.ProduitService;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;

public class App {

    public static void main(String[] args) {
        CrudService<Categorie, Long> catService = new CrudService<>(Categorie.class);
        ProduitService produitService = new ProduitService();
        CrudService<Commande, Long> commandeService = new CrudService<>(Commande.class);

        // Insertion d'une catégorie
        Categorie cat = new Categorie("Électronique");
        catService.create(cat);
        System.out.println("Catégorie créée avec l'ID : " + cat.getId());

        Categorie nouvelle = new Categorie("Informatique");
        catService.create(nouvelle);
        System.out.println("Nouvelle catégorie ID = " + nouvelle.getId());

        // Produits
        produitService.create(new Produit("Souris", new BigDecimal("19.90"), nouvelle));
        produitService.create(new Produit("Écran", new BigDecimal("149.00"), nouvelle));
        produitService.create(new Produit("Câble HDMI", new BigDecimal("7.50"), cat));

        // Requêtes
        System.out.println("Par catégorie : " + produitService.findByCategorie(nouvelle.getId()));
        System.out.println("Recherche 'sou' : " + produitService.search("sou", null, null));
        System.out.println("Page 1 (2 éléments) : " + produitService.findPage(1, 2));

        // Héritage JOINED
        CrudService<Client, Long> clientService = new CrudService<>(Client.class);
        Client client = new Client();
        client.setNom("Alice");
        client.setDateInscription(LocalDate.now());
        clientService.create(client);
        System.out.println("Client créé avec l'ID : " + client.getId());

        // Commande avec deux produits (relation N:N)
        Produit p1 = produitService.getById(1L);
        Produit p2 = produitService.getById(2L);

        Commande cmd = new Commande();
        cmd.setDateCommande(LocalDate.now());
        cmd.addProduit(p1, 3);
        cmd.addProduit(p2, 5);
        commandeService.create(cmd);

        System.out.println("Commande créée avec ID = " + cmd.getId());

        // Transaction explicite
        transactionExplicite(cat);

        GenericDAO.getEmf().close();
    }

    private static void transactionExplicite(Categorie categorie) {
        EntityManager em = GenericDAO.getEmf().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Opérations multiples
            Produit p = new Produit("Clavier", new BigDecimal("49.99"), categorie);
            em.persist(p);

            // Simuler une erreur
            if (p.getPrix().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Prix négatif");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Transaction annulée : " + e.getMessage());
        } finally {
            em.close();
        }
    }
}