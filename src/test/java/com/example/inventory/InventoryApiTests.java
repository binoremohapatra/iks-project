package com.example.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryApiTests {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    // ---------- helpers ----------
    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return om.readTree(body).get("token").asText();
    }

    private String adminToken() throws Exception {
        return login("admin", "admin123");
    }

    private long createProduct(String token, String name) throws Exception {
        String json = "{\"name\":\"" + name + "\",\"description\":\"test\",\"price\":100.50,\"stockQuantity\":5}";
        String body = mvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return om.readTree(body).get("id").asLong();
    }

    // ---------- authentication ----------
    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void login_withBlankFields_returns400() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.username").exists());
    }

    @Test
    void products_withoutToken_returns401() throws Exception {
        mvc.perform(get("/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void products_withGarbageToken_returns401() throws Exception {
        mvc.perform(get("/products").header("Authorization", "Bearer not.a.real.token"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- authorization ----------
    @Test
    void normalUser_canRead_butCannotCreate() throws Exception {
        String token = login("user", "user123");

        mvc.perform(get("/products").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"price\":10,\"stockQuantity\":1}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    // ---------- product CRUD ----------
    @Test
    void admin_canCreateAndGetProduct() throws Exception {
        String token = adminToken();
        long id = createProduct(token, "Monitor");

        mvc.perform(get("/products/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Monitor"))
                .andExpect(jsonPath("$.stockQuantity").value(5));
    }

    @Test
    void admin_canUpdateProduct() throws Exception {
        String token = adminToken();
        long id = createProduct(token, "Old Name");

        mvc.perform(put("/products/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Name\",\"description\":\"updated\",\"price\":200,\"stockQuantity\":9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.stockQuantity").value(9));
    }

    @Test
    void admin_canDeleteProduct_thenGets404() throws Exception {
        String token = adminToken();
        long id = createProduct(token, "To Delete");

        mvc.perform(delete("/products/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mvc.perform(get("/products/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // ---------- validation ----------
    @Test
    void createProduct_withInvalidData_returns400WithFieldErrors() throws Exception {
        mvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":-5,\"stockQuantity\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.name").exists())
                .andExpect(jsonPath("$.details.price").exists())
                .andExpect(jsonPath("$.details.stockQuantity").exists());
    }

    @Test
    void getProduct_withNonNumericId_returns400() throws Exception {
        mvc.perform(get("/products/abc").header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isBadRequest());
    }

    // ---------- pagination & sorting ----------
    @Test
    void listProducts_supportsPaginationAndSorting() throws Exception {
        mvc.perform(get("/products?page=0&size=2&sort=price,desc")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void listProducts_withInvalidSortField_returns400() throws Exception {
        mvc.perform(get("/products?sort=doesNotExist")
                        .header("Authorization", "Bearer " + adminToken()))
                .andExpect(status().isBadRequest());
    }
}
