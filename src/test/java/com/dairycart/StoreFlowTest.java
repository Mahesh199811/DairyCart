package com.dairycart;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class StoreFlowTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void catalogLoadsSeededDairyProducts() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Farm Fresh Milk")))
                .andExpect(content().string(containsString("Pure Cow Ghee")));
    }

    @Test
    void productAdministrationRequiresAdminCredentials() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void administratorCanSignInWithTheLocalCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "localadmin")
                        .param("password", "dairycart-local-only"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void administratorCanAddAProductToTheCatalog() throws Exception {
        mockMvc.perform(post("/admin/products")
                        .with(user("localadmin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Goat Milk")
                        .param("description", "Fresh goat milk from the local farm.")
                        .param("category", "Milk")
                        .param("unit", "500 ml")
                        .param("price", "90.00")
                        .param("stock", "12")
                        .param("imageUrl", "🥛"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        mockMvc.perform(get("/").param("q", "Goat Milk"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Goat Milk")));
    }

    @Test
    void customerCanPlaceAnOrderFromTheBasket() throws Exception {
        MvcResult addResult = mockMvc.perform(post("/cart/add/milk-full-1l").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        MockHttpSession session = (MockHttpSession) addResult.getRequest().getSession(false);

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Farm Fresh Milk")));

        MvcResult orderResult = mockMvc.perform(post("/checkout")
                        .session(session)
                        .with(csrf())
                        .param("customerName", "Test Customer")
                        .param("email", "customer@example.com")
                        .param("phone", "1234567890")
                        .param("deliveryAddress", "1 Farm Road"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/orders/*"))
                .andReturn();

        String redirectUrl = orderResult.getResponse().getRedirectedUrl();
        String orderId = redirectUrl.substring(redirectUrl.lastIndexOf('/') + 1);
        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your order is in")))
                .andExpect(content().string(containsString("Test Customer")));
    }

    @Test
    void changingBasketQuantityRecalculatesLineAndSubtotal() throws Exception {
        MvcResult addResult = mockMvc.perform(post("/cart/add/curd-homestyle-500g").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        MockHttpSession session = (MockHttpSession) addResult.getRequest().getSession(false);

        mockMvc.perform(post("/cart/update")
                        .session(session)
                        .with(csrf())
                        .param("quantity_curd-homestyle-500g", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"2\"")))
                .andExpect(content().string(containsString("110.00")));
    }
}
