package ru.netology.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.nio.channels.FileChannel.open;
import static ru.netology.web.DataHelper.*;

public class MoneyTransferTest {

    boolean dashboardPage;
    CardInfo firstCardInfo;
    CardInfo secondCardInfo;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.ValidVerify(verificationCode);
        firstCardInfo = getFirstCardInfo();
        secondCardInfo = getSecondCardInfo();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    }

    @Test
    void shouldTransferFromFirstTSecond() {
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage;
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assert equals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assert equals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage;
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы превышащей остаток на карте списания");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assert equals(firstCardBalance, actualBalanceFirstCard);
        assert equals(secondCardBalance, actualBalanceSecondCard);
    }
}
