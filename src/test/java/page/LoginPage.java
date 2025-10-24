package page;

import com.codeborne.selenide.SelenideElement;
import data.DataHelper;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id='login'] input");
    private final SelenideElement passwordField = $("[data-test-id='password'] input");
    private final SelenideElement buttonLogin = $("[data-test-id='action-login']");

    public VerificationPage loginUser(DataHelper.AuthUser user){
        login(user);
        return new VerificationPage();
    }

    public void login(DataHelper.AuthUser user){
        loginField.setValue(user.getLogin());
        passwordField.setValue(user.getPassword());
        buttonLogin.click();
    }
}
