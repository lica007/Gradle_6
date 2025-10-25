package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.DataHelper;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class ReplenishCardPage {
    private final SelenideElement amountField = $("[data-test-id='amount'] input");
    private final SelenideElement fromField = $("[data-test-id='from'] input");
    private final SelenideElement toField = $("[data-test-id='to'] input");
    private final SelenideElement cancelButton = $("[data-test-id='action-cancel']");
    private final SelenideElement replenishButton = $("[data-test-id='action-transfer']");
    private final SelenideElement replenishCardField = $("[data-test-id='dashboard']");

    public ReplenishCardPage(){
        replenishCardField.should(Condition.visible);
    }

    public PersonalAccountPage getMoneyTransfer(String amount, String cardFromWhere, String cardWhere) {
        moneyTransfer(amount, cardFromWhere, cardWhere);
        return new PersonalAccountPage();
    }

    public void moneyTransfer(String amount, String number, String cardWhere) {
        amountField.setValue(amount);
        fromField.setValue(number);
        toField.shouldHave(Condition.value(cardWhere)).shouldBe(Condition.visible);
        replenishButton.click();
    }

    public void clearField() {
        amountField.press(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        fromField.press(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
    }

    public void buttonCancel() {
        cancelButton.click();
    }
}
