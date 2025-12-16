package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.model.ItemRequest;
import com.example.demo.service.ItemsService;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemsService itemsService;

    public ItemController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @GetMapping("/all")
    public List<Item> getAll() {
        return itemsService.findAll();
    }

    @GetMapping("/search")
    public List<Item> searchByName(@RequestParam("name") String name) {
        return itemsService.findByName(name);
    }

    @PostMapping("/create")
    public Item createItem(@RequestBody ItemRequest itemRequest) {
        return itemsService.createItem(itemRequest);
    }

    @DeleteMapping("/delete")
    public void deleteItem(@RequestParam("id") Long id) {
        itemsService.deleteItem(id);
    }

}
