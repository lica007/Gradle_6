package steps;

import com.codeborne.selenide.Selenide;
import io.cucumber.java.bg.И;
import page.LoginPage;
import page.PersonalAccountPage;
import page.ReplenishCardPage;
import page.VerificationPage;

import static data.DataHelper.getFirstCardInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateSteps {
    private LoginPage loginPage;
    private VerificationPage verificationPage;
    private PersonalAccountPage personalAccountPage;
    private ReplenishCardPage replenishCardPage;

    @И("пользователь залогинен с именем {string} и паролем {string}")
    public void loginWithNameAndPassword(String login, String password){
        loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        verificationPage = loginPage.loginUserCucumber(login, password);
        personalAccountPage = verificationPage.verificationUserCucumber("12345");
    }

    @И("пользователь переводит {} рублей с карты с номером {} на свою 1 карту с главной страницы")
    public void login(String sum, String number){
        replenishCardPage =  personalAccountPage.getReplenishCard(getFirstCardInfo().getCardId());
        var transfer = replenishCardPage.getMoneyTransfer(sum,number);
    }

    @И("баланс его 1 карты из списка на главной странице должен стать {} рублей")
    public void login2(int sum){
        int balanceFirstCard = personalAccountPage.getBalanceCard(getFirstCardInfo().getCardId());
        assertEquals(sum, balanceFirstCard);
    }
}
