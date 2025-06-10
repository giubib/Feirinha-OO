package com.feirinha.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feirinha.api.model.Item;
import com.feirinha.api.repository.ItemRepository;
import com.feirinha.dto.ItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void createItem_success() throws Exception {
        ItemDTO dto = new ItemDTO("Banana", 10);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Banana"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    void createItem_duplicate() throws Exception {
        ItemDTO dto = new ItemDTO("Apple", 5);
        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllItems() throws Exception {
        Item grape = new Item();
        grape.setName("Grape");
        grape.setQuantity(2);
        repository.save(grape);

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Grape"));
    }

    @Test
    void getItemById() throws Exception {
        Item item = new Item();
        item.setName("Orange");
        item.setQuantity(3);
        item = repository.save(item);

        mockMvc.perform(get("/items/" + item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Orange"));
    }

    @Test
    void getItemById_invalidFormat() throws Exception {
        mockMvc.perform(get("/items/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem() throws Exception {
        Item item = new Item();
        item.setName("Pear");
        item.setQuantity(4);
        item = repository.save(item);
        ItemDTO dto = new ItemDTO("Pear", 8);

        mockMvc.perform(put("/items/" + item.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(8));
    }

    @Test
    void updateItem_notFound() throws Exception {
        ItemDTO dto = new ItemDTO("Kiwi", 1);

        mockMvc.perform(put("/items/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItem() throws Exception {
        Item item = new Item();
        item.setName("Tomato");
        item.setQuantity(1);
        item = repository.save(item);

        mockMvc.perform(delete("/items/" + item.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteItem_notFound() throws Exception {
        mockMvc.perform(delete("/items/999"))
                .andExpect(status().isNotFound());
    }
}
