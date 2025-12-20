package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.example.demo.service.ItemsService;

@SpringBootTest
@DisplayName("MCP Tool Registration Test")
class McpToolRegistrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ItemsService itemsService;

    @Test
    @DisplayName("MCPツール登録確認 - Beanが存在する")
    void testToolCallbackProviderBean() {
        ToolCallbackProvider provider = applicationContext.getBean(ToolCallbackProvider.class);
        assertNotNull(provider);
    }

    @Test
    @DisplayName("ItemsServiceがSpringコンテキストに登録されている")
    void testItemsServiceBean() {
        ItemsService service = applicationContext.getBean(ItemsService.class);
        assertNotNull(service);
    }

    @Test
    @DisplayName("MethodToolCallbackProvider を使用したツール登録")
    void testMethodToolCallbackProvider() {
        // ToolCallbackProvider を作成
        ToolCallbackProvider provider = MethodToolCallbackProvider.builder()
                .toolObjects(itemsService)
                .build();

        assertNotNull(provider);
    }

    @Test
    @DisplayName("MCPサーバーアプリケーション起動確認")
    void testApplicationStartup() {
        assertNotNull(applicationContext);
        assertTrue(applicationContext.containsBean("itemsService"));
    }

    @Test
    @DisplayName("ItemsServiceの全てのツールメソッドが実装されている")
    void testAllToolMethodsImplemented() {
        // リフレクションでメソッドの存在確認
        try {
            assertNotNull(ItemsService.class.getMethod("listItems"));
            assertNotNull(ItemsService.class.getMethod("searchItemsByName", String.class));
            assertNotNull(ItemsService.class.getMethod("registerItem", com.example.demo.model.Item.class));
            assertNotNull(ItemsService.class.getMethod("updateItem", com.example.demo.model.Item.class));
            assertNotNull(ItemsService.class.getMethod("removeItem", Long.class));
        } catch (NoSuchMethodException e) {
            fail("ツールメソッドが見つかりません: " + e.getMessage());
        }
    }
}
