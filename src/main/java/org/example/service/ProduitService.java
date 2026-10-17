package org.example.service;

import org.example.dao.GenericDAO;
import org.example.entities.Produit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProduitService extends CrudService<Produit, Long> {

    private final EntityManagerFactory emf = GenericDAO.getEmf();

    public ProduitService() {
        super(Produit.class);
    }

    // Named Query
    public List<Produit> findByCategorie(Long catId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("catId", catId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Criteria API dynamique
    public List<Produit> search(String term, BigDecimal minPrix, BigDecimal maxPrix) {
        EntityManager em = emf.createEntityManager();
        try { // CORRECTION : fermeture de l'EntityManager
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Produit> cq = cb.createQuery(Produit.class);
            Root<Produit> root = cq.from(Produit.class);
            List<Predicate> predicates = new ArrayList<>();
            if (term != null && !term.isEmpty()) {
                // CORRECTION : root.<String>get(...) pour que cb.lower compile
                predicates.add(cb.like(cb.lower(root.<String>get("libelle")), "%" + term.toLowerCase() + "%"));
            }
            if (minPrix != null) {
                // CORRECTION : greaterThanOrEqualTo à la place de cb.ge (BigDecimal)
                predicates.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("prix"), minPrix));
            }
            if (maxPrix != null) {
                predicates.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("prix"), maxPrix));
            }
            cq.where(predicates.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    // Pagination JPQL
    public List<Produit> findPage(int pageNumber, int pageSize) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p ORDER BY p.id", Produit.class)
                    .setFirstResult((pageNumber - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // AJOUT : JOIN FETCH pour lire la catégorie après fermeture de l'EntityManager
    public Produit findByIdAvecCategorie(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Produit p JOIN FETCH p.categorie WHERE p.id = :id", Produit.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}