package ru.ac.uniyar;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BlockedUserTest {
    private WebDriver driver;

    @BeforeEach
    void initializeDriver(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterEach
    void finalizeDriver(){
        driver.quit();
    }

    //Вспомогательный метод авторизации
    //При переходе по адресу https://www.saucedemo.com
    //Вводим действительные данные логин и пароль
    //Подтверждаем введенные данные нажатием кнопки
    public void login(String name, String password){
        driver.get("https://www.saucedemo.com");
        WebElement loginField = driver.findElement(By.id("user-name"));
        loginField.sendKeys(name);
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys(password);
        driver.findElement(By.id("login-button")).click();

    }

    //Вход пользователя в аккаунт не выполнен
    //=======================================
    //При авторизации заблокированного пользователя после введения им данных и подтверждения
    //переход на рабочую страницу не происходит
    //=======================================
    //Пользователь остается на странице авторизации.
    //
    //Выбрасывается ошибка о том что пользователь заблокирован.
    @Test
    void blockedUserCanNotAuthorize(){
        //Ввод данных(попыытка авторизации)
        login("locked_out_user","secret_sauce");
        //Проверка того что пользователя не направило на другую страницу
        assertThat(driver.getCurrentUrl()).isEqualTo("https://www.saucedemo.com/");
        //Проверка выдаваемой ошибки
        assertThat(driver.findElement(By.tagName("h3")).getText()).isEqualTo("Epic sadface: Sorry, this user has been locked out.");

    }


}
