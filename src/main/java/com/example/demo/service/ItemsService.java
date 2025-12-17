package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.Item;
import com.example.demo.model.ItemRequest;
import com.example.demo.model.ItemUpdateRequest;
import com.example.demo.repository.ItemRepository;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ItemsService {

    private final ItemRepository repository;

    public ItemsService(ItemRepository repository) {
        this.repository = repository;
    }

    @Tool(description = "すべてのアイテムのリストを取得する")
    public String listItems() {
        return formatItems("在庫", repository.findAll());
    }

    @Tool(description = "指定されたテキストが名前に含まれるアイテムを検索する")
    public String searchItemsByName(@ToolParam(description = "検索する名前") String name) {
        String keyword = name == null ? "" : name.trim();
        if (keyword.isBlank()) {
            return "検索する名前を入力してください";
        }
        return formatItems("「" + keyword + "」の検索結果", repository.findByName(keyword));
    }

    @Tool(description = "新しいアイテムを登録する")
    public String registerItem(@ToolParam(description = "登録するアイテムの名前、価格、説明") ItemRequest request) {
        if (request == null || isBlank(request.name()) || request.price() == null || request.price() < 0) {
            return "アイテムの名前、もしくは価格が不正です";
        }
        Item saved = repository.save(new Item(null, request.name().trim(), request.price(), blankToNull(request.description())));
        return "アイテムを登録しました: " + formatItem(saved);
    }

    @Tool(description = "指定されたIDのアイテムを削除する")
    public String removeItem(@ToolParam(description = "削除するアイテムのID") Long id) {
        if (id == null) {
            return "削除するアイテムのIDが必要です";
        }
        Item existing = repository.findById(id);
        if (existing == null) {
            return "IDが " + id + " のアイテムが見つかりません";
        }
        repository.deleteById(id);
        return "アイテムを削除しました: " + formatItem(existing);
    }

    private String formatItems(String heading, List<Item> items) {
        if (items.isEmpty()) {
            return heading + "は空です";
        }
        return heading + ":\n" + items.stream().map(this::formatItem).collect(Collectors.joining("\n"));
    }

    private String formatItem(Item item) {
        String base = String.format("#%d %s (%d円)", item.getId(), item.getName(), item.getPrice());
        return isBlank(item.getDescription()) ? base : base + " - " + item.getDescription().trim();
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private String blankToNull(String text) {
        return isBlank(text) ? null : text.trim();
    }
}
