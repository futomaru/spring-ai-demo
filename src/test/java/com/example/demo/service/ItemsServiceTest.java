package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.model.Item;
import com.example.demo.repository.ItemRepository;

@DisplayName("ItemsService テスト")
class ItemsServiceTest {

    @Mock
    private ItemRepository repository;

    @InjectMocks
    private ItemsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("listItems - すべてのアイテムを取得")
    void testListItems() {
        // Arrange
        List<Item> items = List.of(
                new Item(1L, "リンゴ", 150, "新鮮な青森産リンゴ"),
                new Item(2L, "バナナ", 120, "熟した黄色いバナナ"));
        when(repository.findAll()).thenReturn(items);

        // Act
        String result = service.listItems();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("アイテムリスト"));
        assertTrue(result.contains("リンゴ"));
        assertTrue(result.contains("バナナ"));
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("listItems - 空のリストを返す")
    void testListItemsEmpty() {
        // Arrange
        when(repository.findAll()).thenReturn(new ArrayList<>());

        // Act
        String result = service.listItems();

        // Assert
        assertEquals("アイテムリストは空です", result);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("searchItemsByName - 名前で検索")
    void testSearchItemsByName() {
        // Arrange
        List<Item> items = List.of(new Item(1L, "リンゴ", 150, "新鮮な青森産リンゴ"));
        when(repository.findByName("リンゴ")).thenReturn(items);

        // Act
        String result = service.searchItemsByName("リンゴ");

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("「リンゴ」の検索結果"));
        assertTrue(result.contains("リンゴ"));
        verify(repository, times(1)).findByName("リンゴ");
    }

    @Test
    @DisplayName("searchItemsByName - 空の検索キー")
    void testSearchItemsByNameEmpty() {
        // Act
        String result = service.searchItemsByName("");

        // Assert
        assertEquals("検索する名前を入力してください", result);
        verify(repository, never()).findByName(anyString());
    }

    @Test
    @DisplayName("searchItemsByName - nullキー")
    void testSearchItemsByNameNull() {
        // Act
        String result = service.searchItemsByName(null);

        // Assert
        assertEquals("検索する名前を入力してください", result);
        verify(repository, never()).findByName(anyString());
    }

    @Test
    @DisplayName("searchItemsByName - 検索結果なし")
    void testSearchItemsByNameNotFound() {
        // Arrange
        when(repository.findByName("存在しない")).thenReturn(new ArrayList<>());

        // Act
        String result = service.searchItemsByName("存在しない");

        // Assert
        assertEquals("「存在しない」の検索結果は空です", result);
        verify(repository, times(1)).findByName("存在しない");
    }

    @Test
    @DisplayName("registerItem - 正常に登録")
    void testRegisterItem() {
        // Arrange
        Item newItem = new Item(null, "さくらんぼ", 600, "甘い佐藤錦");
        when(repository.save(newItem)).thenReturn(1);

        // Act
        String result = service.registerItem(newItem);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("アイテムを登録しました"));
        assertTrue(result.contains("さくらんぼ"));
        verify(repository, times(1)).save(newItem);
    }

    @Test
    @DisplayName("registerItem - 登録失敗")
    void testRegisterItemFailed() {
        // Arrange
        Item newItem = new Item(null, "テスト", 100, "説明");
        when(repository.save(newItem)).thenReturn(0);

        // Act
        String result = service.registerItem(newItem);

        // Assert
        assertEquals("アイテムの登録に失敗しました", result);
        verify(repository, times(1)).save(newItem);
    }

    @Test
    @DisplayName("removeItem - 正常に削除")
    void testRemoveItem() {
        // Arrange
        Item existingItem = new Item(10L, "なし", 400, "シャリシャリ食感の梨");
        when(repository.findById(10L)).thenReturn(existingItem);
        when(repository.deleteById(10L)).thenReturn(1);

        // Act
        String result = service.removeItem(10L);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("アイテムを削除しました"));
        assertTrue(result.contains("なし"));
        verify(repository, times(1)).findById(10L);
        verify(repository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("removeItem - IDがnull")
    void testRemoveItemNullId() {
        // Act
        String result = service.removeItem(null);

        // Assert
        assertEquals("削除するアイテムのIDを入力してください", result);
        verify(repository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("removeItem - 存在しないID")
    void testRemoveItemNotFound() {
        // Arrange
        when(repository.findById(999L)).thenReturn(null);

        // Act
        String result = service.removeItem(999L);

        // Assert
        assertEquals("IDが 999 のアイテムが見つかりません", result);
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("removeItem - 削除失敗")
    void testRemoveItemDeleteFailed() {
        // Arrange
        Item existingItem = new Item(1L, "リンゴ", 150, "新鮮な青森産リンゴ");
        when(repository.findById(1L)).thenReturn(existingItem);
        when(repository.deleteById(1L)).thenReturn(0);

        // Act
        String result = service.removeItem(1L);

        // Assert
        assertEquals("アイテムの削除に失敗しました", result);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("updateItem - 正常に更新")
    void testUpdateItem() {
        // Arrange
        Item existingItem = new Item(1L, "リンゴ", 150, "新鮮な青森産リンゴ");
        Item updateData = new Item(1L, "青森リンゴ", 180, null);
        when(repository.findById(1L)).thenReturn(existingItem);
        when(repository.update(any())).thenReturn(1);

        // Act
        String result = service.updateItem(updateData);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("アイテムを更新しました"));
        assertTrue(result.contains("青森リンゴ"));
        assertTrue(result.contains("180"));
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).update(any());
    }

    @Test
    @DisplayName("updateItem - IDがnull")
    void testUpdateItemNullId() {
        // Arrange
        Item updateData = new Item(null, "テスト", 100, "説明");

        // Act
        String result = service.updateItem(updateData);

        // Assert
        assertEquals("更新するアイテムのIDを入力してください", result);
        verify(repository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("updateItem - 存在しないID")
    void testUpdateItemNotFound() {
        // Arrange
        Item updateData = new Item(999L, "テスト", 100, "説明");
        when(repository.findById(999L)).thenReturn(null);

        // Act
        String result = service.updateItem(updateData);

        // Assert
        assertEquals("IDが 999 のアイテムが見つかりません", result);
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).update(any());
    }

    @Test
    @DisplayName("updateItem - 部分更新（名前だけ）")
    void testUpdateItemPartialName() {
        // Arrange
        Item existingItem = new Item(1L, "リンゴ", 150, "新鮮な青森産リンゴ");
        Item updateData = new Item(1L, "新しいリンゴ", null, null);
        when(repository.findById(1L)).thenReturn(existingItem);
        when(repository.update(any())).thenReturn(1);

        // Act
        String result = service.updateItem(updateData);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("新しいリンゴ"));
        verify(repository, times(1)).update(any());
    }

    @Test
    @DisplayName("updateItem - 更新失敗")
    void testUpdateItemFailed() {
        // Arrange
        Item existingItem = new Item(1L, "リンゴ", 150, "新鮮な青森産リンゴ");
        Item updateData = new Item(1L, "テスト", 100, null);
        when(repository.findById(1L)).thenReturn(existingItem);
        when(repository.update(any())).thenReturn(0);

        // Act
        String result = service.updateItem(updateData);

        // Assert
        assertEquals("アイテムの更新に失敗しました", result);
    }
}
