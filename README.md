# spring-demo

Spring Boot と Spring AI を組み合わせた、AI ツール（MCP）経由で CRUD を操作するサンプルです。

## 目的
ItemsService が提供するツールでアイテム情報を列挙・検索・登録・更新・削除し、MCP フローがどのようにビジネスロジックを呼び出すかを学べる構成です。

## 起動・実行
```sh
./gradlew bootRun
```

もしくは
```sh
./gradlew bootJar
java -jar build/libs/spring-ai-demo-0.0.1-SNAPSHOT.jar
```

起動後、`spring.main.web-application-type=none` にしてあるため REST API やブラウザ画面は存在しません。AI ツールや CLI から呼び出してください。

## ClientStdio でツールを使う
`ClientStdio` は標準入出力経由で MCP サーバー（このアプリ）を立ち上げ、tool を順番に呼び出すサンプルです。

1. `./gradlew bootJar` で jar を作成する（MCP サーバー起動時に同じ jar を利用）
2. `./gradlew --quiet run -PmainClass=com.example.demo.client.ClientStdio` などで `ClientStdio` を実行すると、`listItems`→`searchItemsByName`→`registerItem`→`updateItem`→`removeItem` の順に呼び出します。

## MCP ツール一覧
`ItemsService` が提供するツールはすべて `MethodToolCallbackProvider` 経由で登録されます。

| ツール名 | 説明 | 主な引数 |
| --- | --- | --- |
| `listItems` | 全アイテムを一覧表示します。 | なし |
| `searchItemsByName` | 名前の一部を使って検索します。 | `name` (必須) |
| `registerItem` | 新しいアイテムを登録します。 | `request`（`name`, `price`, `description`） |
| `updateItem` | 既存アイテムを更新します。 | `change`（`id`, `name?`, `price?`, `description?`） |
| `removeItem` | ID からアイテムを削除します。 | `id` (必須) |

## Claude Desktop からこのサーバーを呼び出す
1. `./gradlew bootJar` で jar を生成し、`spring-ai-demo-0.0.1-SNAPSHOT.jar` を作成します。
2. `java -jar build/libs/spring-ai-demo-0.0.1-SNAPSHOT.jar` を実行すると、Spring AI の MCP サーバーが STDIO 経由で起動します（`spring.main.web-application-type=none` のため HTTP は使いません）。
3. Claude Desktop の設定 → Tools → *Add Tool* で「local MCP tool」を追加し、コマンドに `java`、引数に `-jar build/libs/spring-ai-demo-0.0.1-SNAPSHOT.jar` を渡します。
4. Claude 側で `listItems` などのツールを呼び出せば、標準出力/入力で MCP がシリアライズした結果を受け取れます。

## データ初期化
`src/main/resources/schema.sql` で `items` テーブルを作成し、果物データを挿入しています。起動時に H2 に読み込まれるので、すぐにツール操作を試せます。

## 技術スタック
- Java 25
- Spring Boot 4
- Spring Framework 7
- Spring AI（MCP サーバー/ツール連携）
- MyBatis
- H2 Database（組み込みDB）
- Gradle
