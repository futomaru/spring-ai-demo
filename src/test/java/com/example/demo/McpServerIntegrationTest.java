package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.ItemsService;

@SpringBootTest
@DisplayName("MCP Server Integration Test")
class McpServerIntegrationTest {

    @Autowired
    private ItemsService service;

    @Autowired
    private ItemRepository repository;

    @Test
    @DisplayName("Integration - リスト取得 → 検索 → 登録 → 更新 → 削除")
    void testMcpWorkflow() {
        // Step 1: リスト取得
        String listResult = service.listItems();
        assertNotNull(listResult);
        assertTrue(listResult.contains("アイテムリスト"));
        int initialCount = repository.findAll().size();
        assertTrue(initialCount > 0, "初期データが存在しない");

        // Step 2: 検索
        String searchResult = service.searchItemsByName("リンゴ");
        assertNotNull(searchResult);
        assertTrue(searchResult.contains("リンゴ"));
        assertFalse(searchResult.contains("は空です"));

        // Step 3: 新アイテム登録
        Item newItem = new Item();
        newItem.setName("テストフルーツ");
        newItem.setPrice(500);
        newItem.setDescription("テスト用フルーツ");
        String registerResult = service.registerItem(newItem);
        assertNotNull(registerResult);
        assertTrue(registerResult.contains("登録しました"));

        // 登録後のカウント確認
        int afterRegister = repository.findAll().size();
        assertEquals(initialCount + 1, afterRegister);

        // Step 4: 登録したアイテムを検索
        String searchNewResult = service.searchItemsByName("テストフルーツ");
        assertTrue(searchNewResult.contains("テストフルーツ"));

        // Step 5: 登録したアイテムを取得
        List<Item> foundItems = repository.findByName("テストフルーツ");
        assertEquals(1, foundItems.size());
        Long newItemId = foundItems.get(0).getId();

        // Step 6: アイテムを更新
        Item updateData = new Item();
        updateData.setId(newItemId);
        updateData.setName("更新されたテストフルーツ");
        updateData.setPrice(550);
        String updateResult = service.updateItem(updateData);
        assertTrue(updateResult.contains("更新しました"));

        // 更新確認
        Item updated = repository.findById(newItemId);
        assertEquals("更新されたテストフルーツ", updated.getName());
        assertEquals(550, updated.getPrice());

        // Step 7: アイテムを削除
        String deleteResult = service.removeItem(newItemId);
        assertTrue(deleteResult.contains("削除しました"));

        // 削除確認
        int finalCount = repository.findAll().size();
        assertEquals(initialCount, finalCount);
    }

    @Test
    @DisplayName("Integration - 検索で結果なし")
    void testSearchNoResults() {
        String result = service.searchItemsByName("存在しない果物");
        assertTrue(result.contains("は空です"));
    }

    @Test
    @DisplayName("Integration - 無効な削除")
    void testDeleteNonExistent() {
        String result = service.removeItem(99999L);
        assertTrue(result.contains("見つかりません"));
    }

    @Test
    @DisplayName("Integration - データベースの永続性確認")
    void testDatabasePersistence() {
        // 初期データのいくつかを確認
        Item apple = repository.findById(1L);
        assertNotNull(apple);
        assertEquals("リンゴ", apple.getName());

        Item banana = repository.findById(2L);
        assertNotNull(banana);
        assertEquals("バナナ", banana.getName());
    }

    @Test
    @DisplayName("Integration - listItemsメソッドの出力フォーマット")
    void testListItemsFormat() {
        String result = service.listItems();

        // フォーマット確認
        assertTrue(result.contains("アイテムリスト"));
        assertTrue(result.contains("円)")); // 価格フォーマット確認

        // 複数アイテムが改行で区切られているか確認
        String[] lines = result.split("\n");
        assertTrue(lines.length > 1);
    }

    @Test
    @DisplayName("Integration - 登録→検索→削除の連続操作")
    void testSequentialOperations() {
        // 複数アイテムを連続登録
        for (int i = 1; i <= 3; i++) {
            Item item = new Item();
            item.setName("連続テスト" + i);
            item.setPrice(100 * i);
            item.setDescription("テスト説明" + i);
            String result = service.registerItem(item);
            assertTrue(result.contains("登録しました"));
        }

        // 検索で全件確認
        String searchResult = service.searchItemsByName("連続テスト");
        assertTrue(searchResult.contains("連続テスト1"));
        assertTrue(searchResult.contains("連続テスト2"));
        assertTrue(searchResult.contains("連続テスト3"));

        // 登録したアイテムを削除
        List<Item> items = repository.findByName("連続テスト");
        for (Item item : items) {
            String deleteResult = service.removeItem(item.getId());
            assertTrue(deleteResult.contains("削除しました"));
        }

        // 削除確認
        String searchAfterDelete = service.searchItemsByName("連続テスト");
        assertTrue(searchAfterDelete.contains("は空です"));
    }
}
