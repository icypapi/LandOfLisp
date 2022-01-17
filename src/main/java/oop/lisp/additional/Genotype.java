package oop.lisp.additional;

import java.util.Arrays;

public class Genotype {
    private static final int numOfGenes = 32; // Length of genotype
    private int[] genes = new int[numOfGenes]; // Array containing 32 genes of integers between 0 and 7

    // Default constructor - genes are generated randomly
    public Genotype() {
        for (int i = 0; i < numOfGenes; i++) {
            genes[i] = (int) (Math.random() * 8);
        }
        Arrays.sort(genes);
    }

    // Private constructor we're using to create children genotype
    private Genotype(int[] genes) {
        this.genes = genes;
    }

    public int[] getGenesArray() {
        return genes;
    }

    // Returns random gene from genotype array which decides what move our Animal will make
    public int getGene() {
        return genes[ (int) (Math.random() * numOfGenes) ];
    }

    @Override
    public String toString() {
        return Arrays.toString(genes).replace(", ", "");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Genotype)) return false;

        Genotype that = (Genotype) other;
        return Arrays.equals(genes, that.getGenesArray());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }

    // Consider 'mom' is the weakest parent. ratio = dadEnergy / (momEnergy + dadEnergy)
    public Genotype getChildGenotype(Genotype mom, double ratio) {  // nie czytelniej zrobić metodę statyczną, przyjmującą dwa genotypy, albo konstruktor?
        int genesFromDad = Math.min((int) (numOfGenes * ratio), numOfGenes);
        int[] childGenes = new int[numOfGenes];
        int[] momGenes = mom.getGenesArray();

        if ( (int) (Math.random() * 2) == 0) {
            // We take the left part of a dad's genes
            if (genesFromDad >= 0)
                System.arraycopy(genes, 0, childGenes, 0, genesFromDad);
            if (numOfGenes - genesFromDad >= 0)
                System.arraycopy(momGenes, genesFromDad, childGenes, genesFromDad, numOfGenes - genesFromDad);
        } else {
            // We take the right part of a dad's genes
            if (numOfGenes - genesFromDad >= 0)
                System.arraycopy(momGenes, 0, childGenes, 0, numOfGenes - genesFromDad);
            if (genesFromDad >= 0)
                System.arraycopy(genes, numOfGenes - genesFromDad, childGenes, numOfGenes - genesFromDad, genesFromDad);
        }

        Arrays.sort(childGenes);
        return new Genotype(childGenes);
    }

}
