package com.pantrytoplate;

import java.util.ArrayList;
import java.util.List;

public class MacroOptimizer {

    public static class MealPlanResult {
        private List<SelectedPortion> lunchPortions = new ArrayList<>();
        private List<SelectedPortion> dinnerPortions = new ArrayList<>();
        private List<SelectedPortion> portions = new ArrayList<>(); // Legacy fallback
        private double totalCalories;
        private double totalProtein;
        private double totalCarbs;
        private double totalFat;
        private boolean isSplitMeal;

        public void addPortion(String name, double grams, double cal, double pro, double carb, double fat, boolean isDinner) {
            SelectedPortion portion = new SelectedPortion(name, grams, cal, pro, carb, fat);
            portions.add(portion);
            if (isDinner) {
                dinnerPortions.add(portion);
            } else {
                lunchPortions.add(portion);
            }
            totalCalories += cal;
            totalProtein += pro;
            totalCarbs += carb;
            totalFat += fat;
        }

        public List<SelectedPortion> getPortions() { return portions; }
        public List<SelectedPortion> getLunchPortions() { return lunchPortions; }
        public List<SelectedPortion> getDinnerPortions() { return dinnerPortions; }
        public double getTotalCalories() { return totalCalories; }
        public double getTotalProtein() { return totalProtein; }
        public double getTotalCarbs() { return totalCarbs; }
        public double getTotalFat() { return totalFat; }
        public boolean isSplitMeal() { return isSplitMeal; }
        public void setSplitMeal(boolean splitMeal) { isSplitMeal = splitMeal; }
    }

    public static class SelectedPortion {
        private String name;
        private double grams;
        private double calories;
        private double protein;
        private double carbs;
        private double fat;

        public SelectedPortion(String name, double grams, double calories, double protein, double carbs, double fat) {
            this.name = name;
            this.grams = grams;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }

        public String getName() { return name; }
        public double getGrams() { return grams; }
        public double getCalories() { return calories; }
        public double getProtein() { return protein; }
        public double getCarbs() { return carbs; }
        public double getFat() { return fat; }
    }

    public MealPlanResult optimize(List<Ingredient> pantry, double targetCalories, double targetProtein) {
        return optimize(pantry, targetCalories, targetProtein, null);
    }

    public MealPlanResult optimize(List<Ingredient> pantry, double targetCalories, double targetProtein, UserSessionConfig config) {
        MealPlanResult result = new MealPlanResult();
        
        boolean isSplit = config != null && "Lunch + Dinner".equals(config.getMealSplitStrategy());
        result.setSplitMeal(isSplit);

        List<Ingredient> activeItems = new ArrayList<>();
        for (Ingredient ing : pantry) {
            if (ing.isSelected()) {
                if (config != null) {
                    String name = ing.getName().toLowerCase();
                    
                    // Filter Vegan
                    if (config.isVegan() && (name.contains("egg") || name.contains("chicken") || name.contains("paneer") || name.contains("yogurt") || name.contains("milk") || name.contains("cheese") || name.contains("butter"))) {
                        continue;
                    }
                    // Filter Vegetarian
                    if (config.isVegetarian() && (name.contains("chicken") || name.contains("meat") || name.contains("egg") || name.contains("fish") || name.contains("prawn") || name.contains("beef") || name.contains("mutton"))) {
                        continue;
                    }
                    // Filter Dairy-Free
                    if (config.isDairyFree() && (name.contains("paneer") || name.contains("yogurt") || name.contains("milk") || name.contains("cheese") || name.contains("butter") || name.contains("ghee"))) {
                        continue;
                    }
                }
                activeItems.add(ing);
            }
        }
        if (activeItems.isEmpty()) return result;

        List<Ingredient> sortedByUrgency = new ArrayList<>(activeItems);
        sortedByUrgency.sort((a, b) -> Integer.compare(a.getDaysToExpiry(), b.getDaysToExpiry()));

        if (isSplit) {
            double halfCalories = targetCalories / 2.0;
            
            // --- LUNCH ALLOCATION ---
            double remainingLunchCalories = halfCalories;
            for (Ingredient item : sortedByUrgency) {
                if (remainingLunchCalories <= 0) break;
                
                double maxUseGrams = Math.min(item.getQuantity(), (remainingLunchCalories / item.getCaloriesPer100g()) * 100);
                if (maxUseGrams < 20) maxUseGrams = Math.min(item.getQuantity(), 30);

                double cal = (maxUseGrams / 100.0) * item.getCaloriesPer100g();
                double pro = (maxUseGrams / 100.0) * item.getProteinPer100g();
                double carb = (maxUseGrams / 100.0) * item.getCarbsPer100g();
                double fat = (maxUseGrams / 100.0) * item.getFatPer100g();

                result.addPortion(item.getName(), maxUseGrams, cal, pro, carb, fat, false);
                remainingLunchCalories -= cal;
            }

            // --- DINNER ALLOCATION ---
            double remainingDinnerCalories = halfCalories;
            for (Ingredient item : sortedByUrgency) {
                if (remainingDinnerCalories <= 0) break;

                // Calculate how much of this ingredient was already used in lunch
                double usedInLunch = 0;
                for (SelectedPortion p : result.getLunchPortions()) {
                    if (p.getName().equals(item.getName())) {
                        usedInLunch = p.getGrams();
                    }
                }

                double availableQuantity = item.getQuantity() - usedInLunch;
                if (availableQuantity <= 0) continue; // Skip if pantry stock is fully exhausted

                double maxUseGrams = Math.min(availableQuantity, (remainingDinnerCalories / item.getCaloriesPer100g()) * 100);
                if (maxUseGrams < 20) maxUseGrams = Math.min(availableQuantity, 30);

                double cal = (maxUseGrams / 100.0) * item.getCaloriesPer100g();
                double pro = (maxUseGrams / 100.0) * item.getProteinPer100g();
                double carb = (maxUseGrams / 100.0) * item.getCarbsPer100g();
                double fat = (maxUseGrams / 100.0) * item.getFatPer100g();

                result.addPortion(item.getName(), maxUseGrams, cal, pro, carb, fat, true);
                remainingDinnerCalories -= cal;
            }

        } else {
            // Single Meal Allocation
            double remainingCalories = targetCalories;
            for (Ingredient item : sortedByUrgency) {
                if (remainingCalories <= 0) break;
                
                double maxUseGrams = Math.min(item.getQuantity(), (remainingCalories / item.getCaloriesPer100g()) * 100);
                if (maxUseGrams < 20) maxUseGrams = Math.min(item.getQuantity(), 50);

                double cal = (maxUseGrams / 100.0) * item.getCaloriesPer100g();
                double pro = (maxUseGrams / 100.0) * item.getProteinPer100g();
                double carb = (maxUseGrams / 100.0) * item.getCarbsPer100g();
                double fat = (maxUseGrams / 100.0) * item.getFatPer100g();

                result.addPortion(item.getName(), maxUseGrams, cal, pro, carb, fat, false);
                remainingCalories -= cal;
            }
        }

        return result;
    }
}