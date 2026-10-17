package org.example.entities;

import javax.persistence.*;
import java.util.ArrayList; // AJOUT
import java.util.List;      // AJOUT

@Entity
@Table(name = "categories")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits = new ArrayList<>();

    // Constructeurs
    public Categorie() {}
    public Categorie(String nom) { this.nom = nom; }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; } // AJOUT
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public List<Produit> getProduits() { return produits; }                       // AJOUT
    public void setProduits(List<Produit> produits) { this.produits = produits; } // AJOUT

    public void addProduit(Produit p) {
        produits.add(p);
        p.setCategorie(this);
    }

    public void removeProduit(Produit p) {
        produits.remove(p);
        p.setCategorie(null);
    }

    @Override
    public String toString() { return "Categorie{id=" + id + ", nom='" + nom + "'}"; }
}