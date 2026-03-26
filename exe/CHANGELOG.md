# 📋 CHANGELOG - История версий

Все изменения APK Signer от версии 1.0 до текущей.

---

## 🆕 [1.2.0] - Март 2026

### ✨ ДОБАВЛЕНО

#### Основное
- **AAB (Android App Bundle) поддержка** в обеих Java версиях
  - Format selector (JComboBox) для выбора APK или AAB
  - Динамическая фильтрация файлов диалога
  - Conditional signing workflow (разные действия для APK и AAB)
  - Format-специфичная информация для маркетов

#### Java Basic (APKSigner.java)
```java
✅ Новые методы:
  - updateFileFilter()         // Динамическая фильтрация по формату
  - selectAppFile()            // Универсальный файловый диалог
  - signAAB()                  // Подпись AAB файлов
  - analyzeAppFile(path, format)  // Анализ обоих форматов

✅ Обновленные методы:
  - setupUI()                  // Добавлена панель формата
  - generateSignatures()       // Условная логика по формату
  - generatePlayMarketSignature(keystorePath, outputDir, format)  // +формат
```

#### Java Advanced (APKSignerAdvanced.java)
```java
✅ Новые методы:
  - selectAppFileAdvanced(format)  // Выбор файла для Tab 1
  - signAPKInternal(path, store, output, isTabOne)  // Dual-tab support
  - signAABInternal(path, store, output, isTabOne)  // Dual-tab support

✅ Обновленные методы:
  - createSigningTab()         // Заголовок: "Подпись APK" → "Подпись APK/AAB"
  - generateSignatures()       // Условная логика по формату
  - generatePlayMarketSignature() // Перегруженный с format параметром
```

#### Документация (новые файлы)
- **AAB_SUPPORT.md** (15 KB)
  - Полное объяснение AAB формата
  - Когда использовать AAB vs APK
  - Технические сведения и примеры
  - Размеры и преимущества

- **QUICK_AAB_START.md** (8 KB)
  - За 5 минут до AAB подписи
  - Пошаговая инструкция для новичков
  - Частые вопросы и решения
  - Безопасность и best practices

- **VERSION_1.2_RELEASE_NOTES.md** (12 KB)
  - Что изменилось в 1.2
  - Тестирование и результаты
  - Известные ограничения
  - Будущие планы развития

#### Документация (обновленные файлы)
- **README.md** - Добавлено про AAB, ссылка на AAB_SUPPORT.md
- **README_JAVA.md** - Обновлены возможности, добавлен AAB
- **REBUILD_GUIDE.md** - Объяснение про AAB в Advanced версии
- **QUICKSTART.md** - Добавлена ссылка на QUICK_AAB_START.md
- **START_HERE.md** - Упоминание AAB поддержки
- **EXAMPLES.md** - Добавлены примеры для AAB (GUI и CLI)
- **INDEX.md** - Добавлены новые документы в таблицу

### 🔄 ИЗМЕНЕНО

#### Код
- **APKSigner.java**
  - Размер увеличился с ~17 KB до ~20 KB (+3 KB)
  - Добавлено ~50 строк нового кода
  - Логика анализа файлов теперь универсальна для APK и AAB

- **APKSignerAdvanced.java**
  - Размер увеличился с ~30 KB до ~35 KB (+5 KB)
  - Добавлено ~80 строк нового кода
  - Вкладка 1 теперь поддерживает оба формата
  - Методы подписи теперь dual-tab aware

#### UI/UX
- Новая панель выбора формата (Format selector)
- Dynamic file dialog filtering (зависит от формата)
- Разные инструкции для разных форматов
- Status label с информацией про выбранный формат

### 🧪 ТЕСТИРОВАНО

```
✅ Компиляция
  - javac -encoding UTF-8 APKSigner.java        ✅ 0 ошибок
  - javac -encoding UTF-8 APKSignerAdvanced.java ✅ 0 ошибок
  - Warnings: unchecked operations (ожидаемо)

✅ Запуск
  - java APKSigner           ✅ Успешно
  - java APKSignerAdvanced   ✅ Успешно
  - GUI компоненты          ✅ Работают
  - File dialogs             ✅ Работают
```

### 📊 Статистика

| Метрика | Значение |
|---------|----------|
| Файлов модифицировано | 2 Java + 7 документов |
| Строк кода добавлено | ~130 |
| Методов добавлено | 6 |
| Документации добавлено | ~35 KB |
| Ошибок компиляции | 0 |
| Warnings | 2 (unchecked operations) |

### ⚠️ ИЗВЕСТНЫЕ ОГРАНИЧЕНИЯ

- Python версии (main.py, cli.py) еще не обновлены с AAB поддержкой
- RuStore не поддерживает AAB (используйте APK)
- Для конвертирования AAB → APK нужен bundletool (не входит)

### 🔮 ЗАПЛАНИРОВАНО НА v1.2.1+

- Python AAB поддержка (main.py и cli.py)
- Bundletool интеграция для локального тестирования

---

## [1.1.0] - Предыдущая версия

### ✨ ОСНОВНЫЕ ВОЗМОЖНОСТИ

- ✅ Загрузка APK файла
- ✅ Создание нового Keystore
- ✅ Подпись APK файла
- ✅ Экспорт сертификата в PEM
- ✅ Генерация подписей для Google Play Market
- ✅ Генерация подписей для RuStore
- ✅ Вычисление SHA256 и SHA1 отпечатков
- ✅ Графический интерфейс (Swing)
- ✅ Python и Java реализации
- ✅ Полная документация

### 📁 СТРУКТУРА v1.1

```
27 файлов:
├─ 2 Java файла (Basic + Advanced)
├─ 2 Python файла (GUI + CLI)  
├─ 4 батника для запуска
├─ 19 файлов документации
```

---

## 🚀 Как обновиться

### С v1.1 на v1.2

```bash
# 1. Скачать новые файлы:
#    - APKSigner.java (обновленный)
#    - APKSignerAdvanced.java (обновленный)
#    - AAB_SUPPORT.md (новый)
#    - QUICK_AAB_START.md (новый)
#    - VERSION_1.2_RELEASE_NOTES.md (новый)

# 2. Заменить старые Java файлы на новые

# 3. Пересомпилировать:
javac -encoding UTF-8 APKSigner.java
javac -encoding UTF-8 APKSignerAdvanced.java

# 4. Старый Keystore по прежнему работает! ✅
#    Не нужно пересоздавать ключи.
```

### Обратная совместимость

✅ **APK подпись работает как раньше**
- Все старые процессы не изменились
- Один Keystore можно использовать для обоих форматов
- RuStore поддержка не изменилась

---

## 📈 Версионирование

```
Семантическое версионирование: MAJOR.MINOR.PATCH

v1.2.0 = 
  MAJOR: 1 (основная версия)
  MINOR: 2 (добавлены новые фишки)
  PATCH: 0 (не было исправлений)

следующие: v1.2.1, v1.3.0, v2.0.0, ...
```

---

## 📝 Важные даты

| Дата | Событие |
|------|---------|
| Март 2026 | v1.2.0 выпущена (AAB поддержка) |
| Февраль 2026 | v1.1.0 выпущена (базовая версия) |
| Декабрь 2025 | Начало разработки |

---

## 🎓 Разработка

### Процесс создания v1.2

1. **Анализ требований** - AAB поддержка нужна
2. **Дизайн** - GUI с format selector
3. **Реализация** - Код для обоих форматов
4. **Тестирование** - Компиляция и запуск
5. **Документация** - Полная инструкция
6. **Выпуск** - v1.2.0 ready

### Инструменты использованные

- Java 11.0.17 - компиляция
- Swing - графический интерфейс
- jarsigner - подпись APK/AAB
- keytool - управление ключами

---

## 💡 Заметки разработчика

### Почему AAB поддержка добавлена именно так?

1. **Format selector** - простой способ выбрать формат
2. **Dynamic filtering** - пользователь видит только нужные файлы
3. **Conditional signing** - разные процессы для разных форматов
4. **Backward compatible** - старые версии продолжают работать

### Почему Python еще не обновлены?

Планируется обновить в v1.2.1:
- main.py - добавить format selector в tkinter GUI
- cli.py - добавить `--format` аргумент

---

## 📞 Поддержка

Вопросы или проблемы?

1. Читай **AAB_SUPPORT.md** (про AAB)
2. Читай **TROUBLESHOOTING.md** (про ошибки)
3. Смотри **EXAMPLES.md** (примеры)

---

**APK Signer v1.2 - полная поддержка APK и AAB! 🚀**
