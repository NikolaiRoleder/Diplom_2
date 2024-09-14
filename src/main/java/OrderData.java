import pojo.Order;

import java.util.List;

public class OrderData {
    // Получаем хеш ингредиентов, что бы использовать в заказе
    public static Order getCorrectIngredientHash() {
        List<String> ingredients = new IngredientsClient().getIngredients().extract().path("data._id");
        String ingredient1 = ingredients.get(0);
        String ingredient2 = ingredients.get(1);
        return new Order(List.of(ingredient1,ingredient2));
    }
    // Создаем некорректные Хеши
    public static Order getIncorrectOrder() {
        return new Order(List.of("61c0c5", "61c0c5aaa70"));
    }
    // Создаем пустой список с Хешами
    public static Order getOrderWithoutIngredients() {
        return new Order(List.of());
    }
}