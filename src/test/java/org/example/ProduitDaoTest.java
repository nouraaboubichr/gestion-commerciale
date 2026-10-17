package org.example;

import org.example.entities.Categorie;
import org.example.entities.Produit;
import org.example.service.CrudService;
import org.example.service.ProduitService; // AJOUT
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals; // AJOUT
import static org.junit.Assert.assertNotNull; // AJOUT

public class ProduitDaoTest {

    private static CrudService<Categorie, Long> catService;
    private static ProduitService prodService; // CORRECTION : ProduitService (hérite de CrudService)

    @BeforeClass
    public static void init() {
        catService = new CrudService<>(Categorie.class);
        prodService = new ProduitService();
    }

    @Test
    public void testCreateProduitAvecCategorie() {
        // CORRECTION : nom unique, car la colonne "nom" est unique
        Categorie cat = new Categorie("Papeterie " + System.currentTimeMillis());
        catService.create(cat);

        Produit p = new Produit("Stylo", new BigDecimal("1.20"), cat);
        prodService.create(p);

        assertNotNull(p.getId());

        // CORRECTION : JOIN FETCH pour éviter la LazyInitializationException
        Produit fetched = prodService.findByIdAvecCategorie(p.getId());
        assertEquals(cat.getNom(), fetched.getCategorie().getNom());
    }
}