package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PersonalAccountPage {
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private final SelenideElement personalAccountField = $("[data-test-id='dashboard']");

    public PersonalAccountPage() {
        personalAccountField.should(Condition.visible);
    }

    public ReplenishCardPage getReplenishCard(String id) {
        cards.findBy(Condition.attribute("data-test-id", id))
                .$("[data-test-id='action-deposit']")
                .click();
        return new ReplenishCardPage();
    }

    public int getBalanceCard(String id) {
        var balance = cards.findBy(Condition.attribute("data-test-id", id)).getText();
        return extractBalance(balance);
    }

    public int extractBalance(String text) {
        var start = text.indexOf(balanceStart);
        var finish = text.indexOf(balanceFinish);
        var value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }
}
