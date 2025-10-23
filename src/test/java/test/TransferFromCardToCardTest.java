package test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import data.DataHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import page.LoginPage;
import page.PersonalAccountPage;
import page.ReplenishCardPage;

import javax.lang.model.element.ModuleElement;
import java.beans.Transient;
import java.time.Duration;
import java.util.Dictionary;

import static com.codeborne.selenide.Selenide.$;
import static data.DataHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransferFromCardToCardTest {

    @BeforeEach
    void setUpAll() {
        var user = getAuthUser();
        var verificationCode = DataHelper.getverificationCode(user);

        var loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        var verificationPage = loginPage.loginUser(user);
        var personalAcoountPage = verificationPage.verificationUser(verificationCode);
    }

    private void reverseTransaction() {
        var personalAcoountPage = new PersonalAccountPage();
        int currentBalanceFirstCard = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());

        if (currentBalanceFirstCard != 10_000){
            int sum = currentBalanceFirstCard - 10_000;
            if (sum > 0) {
                var replenishCardPage = personalAcoountPage.getReplenishCard(getSecondCardInfo().getCardId());
                replenishCardPage.clearField();
                var transfer = replenishCardPage.getMoneyTransfer(String.valueOf(sum),getFirstCardInfo().getNumber());
            } else {
                var replenishCardPage = personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
                replenishCardPage.clearField();
                var transfer = replenishCardPage.getMoneyTransfer(String.valueOf(-sum),getSecondCardInfo().getNumber());
            }
        }
    }

    @Test
    @DisplayName("Успешный перевод с карты 0001 на карту 0002")
    public void shouldTransferFromTheFirstCardToTheSecond() {
        var personalAcoountPage = new PersonalAccountPage();

        int balanceFirstCard = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
        $("[data-test-id='to'] input")
                .shouldHave(Condition.value("**** **** **** 0001"))
                .shouldBe(Condition.visible);
        var transfer = replenishCardPage.getMoneyTransfer("1000",getSecondCardInfo().getNumber());

        int balanceFirstCardAfterReplenishment = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        assertEquals(balanceFirstCard + 1000, balanceFirstCardAfterReplenishment);
    }

    @Test
    @DisplayName("Перевод суммы больше баланса на карте: ожидаем сообщение об ошибке")
    public void shouldShowErrorWhenTransferAmountExceedsBalance() {
        var personalAcoountPage = new PersonalAccountPage();

        try {
        var replenishCardPage =  personalAcoountPage.getReplenishCard(getSecondCardInfo().getCardId());
        $("[data-test-id='to'] input")
                .shouldHave(Condition.value("**** **** **** 0002"))
                .shouldBe(Condition.visible);
        var transfer = replenishCardPage.getMoneyTransfer("12000",getFirstCardInfo().getNumber());
        $("[data-test-id='error-notification']")
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text("Ошибка!"));
        } finally {
            $("[data-test-id='action-cancel']").click();
        }
    }

    @Test
    @DisplayName("Перевод суммы на не существующую карту: ожидаем сообщение об ошибке")
    public void shouldShowErrorWhenTransferToANonExistentCard() {
        var personalAcoountPage = new PersonalAccountPage();

        try {
            var replenishCardPage =  personalAcoountPage.getReplenishCard(getSecondCardInfo().getCardId());
            $("[data-test-id='to'] input")
                    .shouldHave(Condition.value("**** **** **** 0002"))
                    .shouldBe(Condition.visible);
            var transfer = replenishCardPage.getMoneyTransfer("2000","5559000000000006");
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldBe(Condition.visible)
                    .shouldHave(Condition.text("Ошибка!"));
        } finally {
            $("[data-test-id='action-cancel']").click();
        }
    }

    @Test
    @DisplayName("Перевод c карты 0001 на карту 0001: ожидаем сообщение об ошибке")
    public void shouldShowErrorWhenTransferToTheSameCard() {
        var personalAcoountPage = new PersonalAccountPage();

        try {
        var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
        $("[data-test-id='to'] input")
                .shouldHave(Condition.value("**** **** **** 0001"))
                .shouldBe(Condition.visible);
        var transfer = replenishCardPage.getMoneyTransfer("2000",getFirstCardInfo().getNumber());
        $("[data-test-id='error-notification'] .notification__content")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Ошибка!"));
        } finally {
            $("[data-test-id='action-cancel']").click();
        }
    }

    @Test
    @DisplayName("Два перевода подряд")
    public void shouldMakeSeveralTransfersInARow() {
        var personalAcoountPage = new PersonalAccountPage();

        // Первый перевод
        int balanceFirstCard = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
        $("[data-test-id='to'] input")
                .shouldHave(Condition.value("**** **** **** 0001"))
                .shouldBe(Condition.visible);
        var transfer = replenishCardPage.getMoneyTransfer("1000",getSecondCardInfo().getNumber());

        int balanceFirstCardAfterReplenishment = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        assertEquals(balanceFirstCard + 1000, balanceFirstCardAfterReplenishment);

        // Второй перевод
        int balanceSecondCard = personalAcoountPage.getBalanceCard(getSecondCardInfo().getCardId());
        var replenishCardPage2 =  personalAcoountPage.getReplenishCard(getSecondCardInfo().getCardId());
        $("[data-test-id='to'] input")
                .shouldHave(Condition.value("**** **** **** 0002"))
                .shouldBe(Condition.visible);
        var transfer2 = replenishCardPage2.getMoneyTransfer("1000",getFirstCardInfo().getNumber());

        int balanceSecondCardAfterReplenishment = personalAcoountPage.getBalanceCard(getSecondCardInfo().getCardId());
        assertEquals(balanceSecondCard + 1000, balanceSecondCardAfterReplenishment);

    }

    @Test
    @DisplayName("Перевод с пустыми полями")
    public void shouldShowErrorWhenTransferWithEmptyFields() {
        var personalAcoountPage = new PersonalAccountPage();

        try {
            var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
            $("[data-test-id='to'] input")
                    .shouldHave(Condition.value("**** **** **** 0001"))
                    .shouldBe(Condition.visible);
            var transfer = replenishCardPage.getMoneyTransfer(null,null);
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldBe(Condition.visible)
                    .shouldHave(Condition.text("Ошибка!"));
        } finally {
            $("[data-test-id='action-cancel']").click();
        }
    }

    @Test
    @DisplayName("Перевод с пустым полем номера карты 'Откуда'")
    public void shouldErrorWhenTransferWithAnEmptyCardNumberField() {
        var personalAcoountPage = new PersonalAccountPage();

        try {
            var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
            $("[data-test-id='to'] input")
                    .shouldHave(Condition.value("**** **** **** 0001"))
                    .shouldBe(Condition.visible);
            var transfer = replenishCardPage.getMoneyTransfer("200",null);
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldBe(Condition.visible)
                    .shouldHave(Condition.text("Ошибка!"));
        } finally {
            $("[data-test-id='action-cancel']").click();
        }
    }

    @Test
    @DisplayName("Перевод с пустым полем суммы перевода")
    public void shouldErrorWhenTransferWithAnEmptyAmountField() {
        var personalAcoountPage = new PersonalAccountPage();

        try {
            var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
            $("[data-test-id='to'] input")
                    .shouldHave(Condition.value("**** **** **** 0001"))
                    .shouldBe(Condition.visible);
            var transfer = replenishCardPage.getMoneyTransfer(null,getSecondCardInfo().getNumber());
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldBe(Condition.visible)
                    .shouldHave(Condition.text("Ошибка!"));
        } finally {
            $("[data-test-id='action-cancel']").click();
        }
    }

    @Test
    @DisplayName("Перевод суммы с копейками")
    public void shouldTransferTheAmountWithKopecks() {
        var personalAcoountPage = new PersonalAccountPage();

        int balanceFirstCard = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
        $("[data-test-id='to'] input")
                .shouldHave(Condition.value("**** **** **** 0001"))
                .shouldBe(Condition.visible);
        var transfer = replenishCardPage.getMoneyTransfer("10,57",getSecondCardInfo().getNumber());

        int balanceFirstCardAfterReplenishment = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        assertEquals(balanceFirstCard + 10.57, balanceFirstCardAfterReplenishment);
    }

    @Test
    @DisplayName("Перевод суммы больше 5 символов")
    public void shouldTransferAmountsOfMoreThan5Digits() {
        var personalAcoountPage = new PersonalAccountPage();

        int balanceFirstCard = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        var replenishCardPage =  personalAcoountPage.getReplenishCard(getFirstCardInfo().getCardId());
        $("[data-test-id='to'] input")
                .shouldHave(Condition.value("**** **** **** 0001"))
                .shouldBe(Condition.visible);
        var transfer = replenishCardPage.getMoneyTransfer("9999,57",getSecondCardInfo().getNumber());

        int balanceFirstCardAfterReplenishment = personalAcoountPage.getBalanceCard(getFirstCardInfo().getCardId());
        assertEquals(balanceFirstCard + 9999.57, balanceFirstCardAfterReplenishment);
    }

    @AfterEach
    void clearField() {
        reverseTransaction();
    }
}