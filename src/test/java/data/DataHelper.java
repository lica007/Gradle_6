package data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.util.Locale;

public class DataHelper {

    private DataHelper(){
    }

    public static AuthUser getAuthUser() {
        return new AuthUser("vasya","qwerty123");
    }

    public static VerificationCode getverificationCode(AuthUser authUser) {
        return new VerificationCode("12345");
    }

    public static CardInfo getFirstCardInfo() {
        return new CardInfo("5559 0000 0000 0001", "92df3f1c-a033-48e6-8390-206f6b1f56c0");
    }

    public static CardInfo getSecondCardInfo() {
        return new CardInfo("5559 0000 0000 0002", "0f3f5c2a-249e-4c3d-8287-09f7a039391d");
    }

    public static String generateNumberCard(Faker faker) {
        return faker.finance().creditCard();
    }

    public static String generateIdCard(Faker faker) {
        return faker.number().digits(36);
    }

    public static class RandomCardInfo {
        private static Faker faker;

        private RandomCardInfo() {
        }

        public static CardInfo generateCardInfo(String locale) {
            faker = new Faker(new Locale(locale));
            return new CardInfo(generateNumberCard(faker), generateIdCard(faker));
        }
    }

    @Value
    public static class AuthUser {
        String login;
        String password;
    }

    @Value
    public static class VerificationCode {
        String verificationCode;
    }

    @Value
    public static class CardInfo {
        String number;
        String cardId;
    }
}
