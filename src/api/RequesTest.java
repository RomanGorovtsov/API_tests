package api;

import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class RequesTest {
    private static final String URL = "https://reqres.in/";


    //проверить, что email заканчивается на "@reqres.in"
    @Test
    public void checkAvatarAndIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOk200());
        List<UserData> users = given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + "api/users?page=2")
                .then().log().all().extract().body().jsonPath().getList("data", UserData.class);

        users.forEach(x -> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));

        Assert.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));
    }

    //зарегистрироваться на сайте
    @Test
    public void successRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOk200());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Registration user = new Registration("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assert.assertEquals(id, successReg.getId());
        Assert.assertEquals(token, successReg.getToken());
    }

    //неуспешная регистрация на сайте
    @Test
    public void unSuccessRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(400));
        Registration user = new Registration("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assert.assertEquals(unSuccessReg.getError(), "Missing password");
    }


    //сортировака лет по возрастанию
    @Test
    public void sortedYearsTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOk200());
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colors.stream().map(ColorsData::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(years, sortedYears);
    }

    // проверить 204 код, при удалении
    @Test
    public void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }


    //проверить 200 код, при удалении
    @Test
    public void delayedResponse() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(200));
        given()
                .when()
                .delete("api/users?delay=3")
                .then().log().all();
    }

    //проверить 404 код, при получении данных
    @Test
    public void checkStatusCode404() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(404));
        given()
                .when()
                .get(URL + "api/unknown/23")
                .then().log().all();
    }

    //проверить 200 код, при обновлении данных
    @Test
    public void updateUser() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(200));
        given()
                .when()
                .put(URL + "api/users/2")
                .then().log().all();
    }

    //проверить код 201, при отправке данных
    @Test
    public void createUser() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(201));
        given()
                .when()
                .post(URL + "api/users")
                .then().log().all();
    }

    //проверить 400 код, при отправке данных на создание пользователя
    @Test
    public void registerUser() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(400));
        given()
                .when()
                .post(URL + "api/register")
                .then().log().all();
    }
}

