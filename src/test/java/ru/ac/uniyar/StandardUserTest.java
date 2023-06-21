package ru.ac.uniyar;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


public class StandardUserTest {
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
        WebElement chose = driver.findElement(By.id("user-name"));
        chose.sendKeys(name);
        WebElement chosep = driver.findElement(By.id("password"));
        chosep.sendKeys(password);
        driver.findElement(By.id("login-button")).click();

    }

    //Вход пользователя в аккаунт выполнен
    //=======================================
    //При авторизации пользователя после введения им данных и подтверждения
    //происходит переход на рабочую страницу с продукцией
    //=======================================
    //Пользователь перенаправлен на страницу магазина
    @Test
    void standardUserCanAuthorize(){
        //Авторизация
        login("standard_user", "secret_sauce");
        //Проверить переход по ожидаемому адресу
        assertThat(driver.getCurrentUrl()).isEqualTo("https://www.saucedemo.com/inventory.html");
        //Проверить точно ли направлены на страницу с продукцией
        assertThat(driver.findElement(By.className("title")).getText()).isEqualTo("PRODUCTS");
    }

    //Вход пользователя в аккаунт не выполнен
    //=======================================
    //Авторизация пользователя
    //происходит переход на рабочую страницу с продукцией
    //В боковом меню происходит взаимодействие с кнопкой выхода из аккаунта
    //=======================================
    //Пользователь перенаправлен на начальную страницу сайта
    @Test
    void standardUserCanLogout(){
        //Авторизация
        login("standard_user", "secret_sauce");
        //Нажать кнопку открытия бокового меню
        driver.findElement(By.id("react-burger-menu-btn")).click();
        //Ожидание , тк меню имеет анимацию открытия
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //Нажать кнопку logout
        driver.findElement(By.id("logout_sidebar_link")).click();
        //Проверить , перенаправлен ли пользователь на начальную страницу
        assertThat(driver.getCurrentUrl()).isEqualTo("https://www.saucedemo.com/");


    }

    //Вход пользователя в аккаунт выполнен
    //=======================================
    //При добавлении товаров в корзину есть возможность увидеть это
    //на кнопке корзины и соответственно в самой корзине
    //
    //=======================================
    //Пользователь добавил товары в корзину
    @Test
    void standardUserCanAddItemsToCart(){
        //Авторизация
        login("standard_user", "secret_sauce");
        //Добавить товары по нажатию кнопки
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        //Проверить что на кнопке корзины отображается правильное число добавленных товаров
        assertThat(driver.findElement(By.className("shopping_cart_badge")).getText()).isEqualTo("2");
        //Перейти в корзину
        driver.findElement(By.className("shopping_cart_link")).click();
        //Проверить наличие добавленных товаров на странице корзины пользователя
        assertThat(driver.findElement(By.id("item_4_title_link")).getText()).isEqualTo("Sauce Labs Backpack");
        assertThat(driver.findElement(By.id("item_0_title_link")).getText()).isEqualTo("Sauce Labs Bike Light");

    }

    //Вход пользователя в аккаунт выполнен
    //Товар находится в корзине
    //=======================================
    //
    //При нажатии кнопки remove в окне добавленного товара на странице всех продуктов
    //происходит удаление товара из корзины
    //=======================================
    //На странице корзины не должно остаться товаров
    @Test
    void standardUserCanRemoveItemsFromProductsPage(){
        //Авторизация
        login("standard_user", "secret_sauce");
        //Добавить товар
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        //Удалить товар
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();
        //Перейти на страницу корзины
        driver.findElement(By.className("shopping_cart_link")).click();
        //Проверить что в корзине отсутствует товар
        assertThatThrownBy(()->driver.findElement(By.className("inventory_item_name")));

    }

    //Вход пользователя в аккаунт выполнен
    //Товар находится в корзине
    //=======================================
    //
    //При нажатии кнопки remove на странице всех корзины
    //происходит удаление товара из списка
    //=======================================
    //На странице корзины не должно остаться товаров
    @Test
    void standardUserCanRemoveItemsFromCartPage(){
        //Авторизация
        login("standard_user", "secret_sauce");
        //Добавить товар в корзину
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        //Перейти в корзину
        driver.findElement(By.className("shopping_cart_link")).click();
        //Удалить товар
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();
        //Проверить что нет товаров в корзине(ошибка при попытке найти товар)
        assertThatThrownBy(()->driver.findElement(By.className("inventory_item_name")));

    }

    //Вход пользователя в аккаунт выполнен
    //Товары находятся в корзине
    //=======================================
    //Есть возможность оформить заказ.
    //Пользователь вводит свои данные на странице оформления заказа и подтверждает их
    //
    //=======================================
    //Пользователь получает сообщение об успешном оформлении заказа
    @Test
    void standardUserCanMakeOrder(){
        //Авторизация
        login("standard_user", "secret_sauce");
        //Добавить товары в корзину
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        //Перейти в корзину
        driver.findElement(By.className("shopping_cart_link")).click();
        //Начать оформление заказа нажатием кнопки checkout
        driver.findElement(By.id("checkout")).click();
        //Ввести данные пользователя
        driver.findElement(By.id("first-name")).sendKeys("Pavel");
        driver.findElement(By.id("last-name")).sendKeys("Golkin");
        driver.findElement(By.id("postal-code")).sendKeys("150000");
        //Подтвердить введенные данные
        driver.findElement(By.id("continue")).click();
        //Проверить переход на страницу с всей информацией о заказе
        assertThat(driver.findElement(By.className("title")).getText()).isEqualTo("CHECKOUT: OVERVIEW");
        //Закончить оформление
        driver.findElement(By.id("finish")).click();
        //Проверить успешное выполнение заказа
        assertThat(driver.findElement(By.className("title")).getText()).isEqualTo("CHECKOUT: COMPLETE!");
        assertThat(driver.findElement(By.className("complete-header")).getText()).isEqualTo("THANK YOU FOR YOUR ORDER");

    }

}
