package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals,
                LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals,
                                                            LocalTime startTime, LocalTime endTime, int caloriesPerDay) {


        Map<LocalDate, Integer> calDay = new HashMap<>();
        for (UserMeal meal : meals) {
            LocalDate mealD = meal.getDateTime().toLocalDate();
            calDay.put(mealD, calDay.getOrDefault(mealD, 0) + meal.getCalories());
        }
        List<UserMealWithExcess> mealwithexcees = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalDateTime ldt = meal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(ldt.toLocalTime(), startTime, endTime)) {
                mealwithexcees.add(new UserMealWithExcess(ldt, meal.getDescription(),
                        meal.getCalories(), calDay.get(ldt.toLocalDate()) > caloriesPerDay));
            }
        }
        return mealwithexcees;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDay = meals.stream().collect(Collectors.groupingBy(o -> o.getDateTime().toLocalDate(),
                Collectors.summingInt(o -> o.getCalories())));
        return meals.stream().filter(o -> TimeUtil.isBetweenHalfOpen(o.getDateTime()
                .toLocalTime(), startTime, endTime))
                .map(o -> new UserMealWithExcess(o.getDateTime(), o.getDescription(), o.getCalories(), caloriesSumByDay.get(o.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());

    }
}
