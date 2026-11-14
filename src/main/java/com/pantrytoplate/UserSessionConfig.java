package com.pantrytoplate;

public class UserSessionConfig {
    private double targetCalories = 650;
    private double targetProtein = 45;
    private double targetCarbs = 65;
    private double targetFats = 22;
    private boolean vegan = false;
    private boolean vegetarian = false;
    private boolean glutenFree = false;
    private boolean dairyFree = false;
    private String mealSplitStrategy = "Single meal"; // "Single meal" or "Lunch + Dinner"

    // Getters and Setters
    public double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(double targetCalories) { this.targetCalories = targetCalories; }
    
    public double getTargetProtein() { return targetProtein; }
    public void setTargetProtein(double targetProtein) { this.targetProtein = targetProtein; }
    
    public double getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(double targetCarbs) { this.targetCarbs = targetCarbs; }
    
    public double getTargetFats() { return targetFats; }
    public void setTargetFats(double targetFats) { this.targetFats = targetFats; }

    public String getMealSplitStrategy() { return mealSplitStrategy; }
    public void setMealSplitStrategy(String mealSplitStrategy) { this.mealSplitStrategy = mealSplitStrategy; }

    // Boolean getters (Thymeleaf expects 'is...')
    public boolean isVegan() { return vegan; }
    public void setVegan(boolean vegan) { this.vegan = vegan; }

    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }

    public boolean isGlutenFree() { return glutenFree; }
    public void setGlutenFree(boolean glutenFree) { this.glutenFree = glutenFree; }

    public boolean isDairyFree() { return dairyFree; }
    public void setDairyFree(boolean dairyFree) { this.dairyFree = dairyFree; }
}
