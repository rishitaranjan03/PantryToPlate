package com.pantrytoplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pantrytoplate.MacroOptimizer.MealPlanResult;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Controller
public class PantryToPlateWebApplication {

    private final List<Ingredient> pantryMaster = new ArrayList<>();
    private final UserSessionConfig userConfig = new UserSessionConfig();
    private final MacroOptimizer optimizer = new MacroOptimizer();

    public PantryToPlateWebApplication() {
        // Initialize default sample pantry matching the Ingredient constructor (9 arguments)
        pantryMaster.add(new Ingredient("eggs", "Eggs", 300, 143, 12.6, 1.1, 10.0, 2, false));
        pantryMaster.add(new Ingredient("paneer", "Paneer", 200, 265, 18.3, 1.2, 22.0, 3, false));
        pantryMaster.add(new Ingredient("chicken", "Chicken Breast", 250, 120, 22.5, 0.0, 2.6, 2, true));
        pantryMaster.add(new Ingredient("oats", "Rolled Oats", 500, 389, 16.9, 66.3, 6.9, 30, false));
        pantryMaster.add(new Ingredient("rice", "Basmati Rice", 500, 365, 7.1, 79.0, 0.7, 60, false));
        pantryMaster.add(new Ingredient("pb", "Peanut Butter", 200, 588, 25.0, 20.0, 50.0, 45, false));
        pantryMaster.add(new Ingredient("yogurt", "Greek Yogurt", 350, 59, 10.0, 3.6, 0.4, 5, false));
        pantryMaster.add(new Ingredient("avocado", "Avocado", 150, 160, 2.0, 8.5, 14.7, 2, false));
    }

    public static void main(String[] args) {
        SpringApplication.run(PantryToPlateWebApplication.class, args);
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pantry", pantryMaster);
        model.addAttribute("config", userConfig);
        model.addAttribute("plan", null);
        return "index";
    }

    @PostMapping("/optimize")
    public String runOptimizer(
            @RequestParam(required = false) List<String> selectedIds,
            @RequestParam(required = false, defaultValue = "0.0") Double calories,
            @RequestParam(required = false, defaultValue = "0.0") Double protein,
            @RequestParam(required = false, defaultValue = "0.0") Double carbs,
            @RequestParam(required = false, defaultValue = "0.0") Double fats,
            @RequestParam(required = false, defaultValue = "Single meal") String mealSplitStrategy,
            @RequestParam(required = false) String vegan,
            @RequestParam(required = false) String vegetarian,
            @RequestParam(required = false) String glutenFree,
            @RequestParam(required = false) String dairyFree,
            Model model) {

        userConfig.setTargetCalories(calories);
        userConfig.setTargetProtein(protein);
        userConfig.setTargetCarbs(carbs);
        userConfig.setTargetFats(fats);
        userConfig.setMealSplitStrategy(mealSplitStrategy);

        userConfig.setVegan(vegan != null);
        userConfig.setVegetarian(vegetarian != null);
        userConfig.setGlutenFree(glutenFree != null);
        userConfig.setDairyFree(dairyFree != null);

        for (Ingredient ing : pantryMaster) {
            ing.setSelected(selectedIds != null && selectedIds.contains(ing.getId()));
        }

        MealPlanResult plan = optimizer.optimize(
            pantryMaster,
            userConfig.getTargetCalories(),
            userConfig.getTargetProtein(),
            userConfig
        );

        model.addAttribute("pantry", pantryMaster);
        model.addAttribute("config", userConfig);
        model.addAttribute("plan", plan);
        return "index";
    }

    @PostMapping("/add-ingredient")
    public String addIngredient(
            @RequestParam String name,
            @RequestParam double quantity,
            @RequestParam double caloriesPer100g,
            @RequestParam double proteinPer100g,
            @RequestParam double carbsPer100g,
            @RequestParam double fatPer100g,
            @RequestParam int daysToExpiry) {
        
        String id = name.toLowerCase().replaceAll("\\s+", "_");
        // Creating new Ingredient using your 9-argument constructor format
        Ingredient newIng = new Ingredient(id, name, quantity, caloriesPer100g, proteinPer100g, carbsPer100g, fatPer100g, daysToExpiry, false);
        newIng.setSelected(true);
        pantryMaster.add(newIng);
        
        return "redirect:/";
    }
}