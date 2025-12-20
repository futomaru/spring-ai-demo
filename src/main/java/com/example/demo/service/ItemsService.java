package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.Item;
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
        return formatItems("アイテムリスト", repository.findAll());
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
    public String registerItem(@ToolParam(description = "登録するアイテムの名前、価格、説明") Item item) {
        if (repository.save(item) == 0) {
            return "アイテムの登録に失敗しました";
        }
        return "アイテムを登録しました: " + formatItem(item);
    }

    @Tool(description = "指定されたIDのアイテムを削除する")
    public String removeItem(@ToolParam(description = "削除するアイテムのID") Long id) {
        if (id == null) {
            return "削除するアイテムのIDを入力してください";
        }
        Item existing = repository.findById(id);
        if (existing == null) {
            return "IDが " + id + " のアイテムが見つかりません";
        }
        if (repository.deleteById(id) == 0) {
            return "アイテムの削除に失敗しました";
        }
        return "アイテムを削除しました: " + formatItem(existing);
    }

    @Tool(description = "既存のアイテムを更新する")
    public String updateItem(@ToolParam(description = "更新するアイテムのID、名前、価格、説明") Item item) {
        if (item.getId() == null) {
            return "更新するアイテムのIDを入力してください";
        }
        Item existing = repository.findById(item.getId());
        if (existing == null) {
            return "IDが " + item.getId() + " のアイテムが見つかりません";
        }
        Item updated = new Item(
                item.getId(),
                item.getName() != null ? item.getName().trim() : existing.getName(),
                item.getPrice() != null ? item.getPrice() : existing.getPrice(),
                blankToNull(item.getDescription()) != null ? blankToNull(item.getDescription())
                        : existing.getDescription());
        if (repository.update(updated) == 0) {
            return "アイテムの更新に失敗しました";
        }
        return "アイテムを更新しました: " + formatItem(updated);
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
