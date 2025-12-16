package com.example.demo.service;

import com.example.demo.model.Item;
import com.example.demo.model.ItemRequest;
import com.example.demo.repository.ItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ItemsService {

    private final ItemRepository repository;

    public ItemsService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> findAll() {
        return repository.findAll();
    }

    public List<Item> findByName(String name) {
        return repository.findByName(name);
    }

    public Item createItem(ItemRequest itemRequest) {
        Item item = new Item(
                null,
                itemRequest.getName(),
                itemRequest.getPrice().intValue(),
                itemRequest.getDescription());
        return repository.save(item);
    }

    public void deleteItem(Long id) {
        repository.deleteById(id);
    }
}
