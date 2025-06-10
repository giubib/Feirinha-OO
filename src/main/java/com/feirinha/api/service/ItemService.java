package com.feirinha.api.service;

import com.feirinha.api.model.Item;
import com.feirinha.api.repository.ItemRepository;
import com.feirinha.dto.ItemDTO;
import com.feirinha.exception.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Item create(ItemDTO dto) {
        if (repository.existsByNameIgnoreCase(dto.getName()).isPresent()) {
        if (repository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Item com esse nome já existe.");
        }
        Item item = new Item();
        item.setName(dto.getName());
        item.setQuantity(dto.getQuantity());
        return repository.save(item);
    }

    public List<Item> findAll() {
        return repository.findAll();
    }

    public Item findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Item não encontrado."));
    }

    public Item update(Long id, ItemDTO dto) {
        Item item = findById(id);

        if (!item.getName().equals(dto.getName()) && repository.existsByNameIgnoreCase(dto.getName()).isPresent()) {
        if (!item.getName().equals(dto.getName()) && repository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Já existe item com esse nome.");
        }

        item.setName(dto.getName());
        item.setQuantity(dto.getQuantity());
        return repository.save(item);
    }

    public void delete(Long id) {
        Item item = findById(id);
        repository.delete(item);
    }
}
