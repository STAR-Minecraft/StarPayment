# StarPayment
Плагин, который позволит игрокам покупать донат-валюту внутри игры с привязкой к сайту и оплаты EasyDonate

## Команды

| Команда | Описание | Права |
| -------- | -------- | -------- |
| /donate     | Открывает игроку меню для покупки доната    | -    |
| /givedonate `[Игрок]` `[Сумма]`     | Выдает игроку донат-валюту с учетом системы бонусов и рефералов    | `starpayment.give`     |

## Установка

После установки плагина на сервер, настройте подключение к базе данных, поддерживаемые типы: `SQLite`, `MySQL`, `PostgreSQL`. Настроить подключение можно в конфиге (`config.yml`) в секции `database`

После настройки подключения к базе данных перейдите к настройке платежей, все платежи обрабатываются [EasyDonate](https://easydonate.ru) при помощи библиотеки [EasyDonate4J](https://github.com/EasyDonate/EasyDonate4J)

```yaml
# Настройка оплаты
payment:
  accessKey: "6789a89480c170ea1ccac30b3979cae9" # ключ доступа
  productId: 168829 # id продукта из EasyDonate
  serverId: 26654 # id сервера из EasyDonate
  redirectURL: "https://vk.com/osk115" # ссылка, которая откроется после оплаты
  currency: "dollars" # валюта из PEconomy
  minSum: 50 # минимальная сумма для создания оплаты
```

| Поле      | Описание  | Дополнительно |
| --------- | -------- | -------- |
| `accessKey`     | Ключ доступа к магазину, можно получить в панели управления магазином     | ![](https://i.imgur.com/5wmSPNI.png) |
| `productId` | Уникальное ID товара, который будет покупаться | ![](https://i.imgur.com/H8opjRc.png) |
| `serverId` | Уникальный ID сервера, на котором будет производится выдача товара | ![](https://i.imgur.com/AWk9le6.png) |
| `redirectURL` | Ссылка на которую будет переадресован игрок после оплаты | Моежете указать любую ссылку, например приглашение на ваш Discord-сервер |
| `currency` | Валюта из `PEconomy` которая будет выдана игроку в качестве донат-валюты | - |
| `minSum` | Минимальная сумма платежа | Если игрок укажет сумму меньше, чем значение `minSum`, ссылка на оплату не создастся |
