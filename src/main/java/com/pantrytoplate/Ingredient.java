package com.pantrytoplate;

public class Ingredient {
    private String id;
    private String name;
    private double quantity; // in grams
    private double caloriesPer100g;
    private double proteinPer100g;
    private double carbsPer100g;
    private double fatPer100g;
    private int daysToExpiry;
    private boolean priority;
    private boolean selected;

    public Ingredient(String id, String name, double quantity, double cal, double pro, double carb, double fat, int daysToExpiry, boolean priority) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.caloriesPer100g = cal;
        this.proteinPer100g = pro;
        this.carbsPer100g = carb;
        this.fatPer100g = fat;
        this.daysToExpiry = daysToExpiry;
        this.priority = priority;
        this.selected = true; 
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public double getCaloriesPer100g() { return caloriesPer100g; }
    public double getProteinPer100g() { return proteinPer100g; }
    public double getCarbsPer100g() { return carbsPer100g; }
    public double getFatPer100g() { return fatPer100g; }
    public int getDaysToExpiry() { return daysToExpiry; }
    public boolean isPriority() { return priority; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}