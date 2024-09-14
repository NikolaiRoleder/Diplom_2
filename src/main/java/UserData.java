import pojo.User;

public class UserData {
    public static User getRandomUser() {
        return new User("diplo@yandex.ru", "qwertyui", "nolivochka");
    }
    public static User getChangeUserData() {
        return new User("dildom@yandex.ru", "qwert", "ochka");
    }
}

