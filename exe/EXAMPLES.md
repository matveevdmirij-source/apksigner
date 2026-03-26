# Примеры использования APK Signer

## 📖 Графический интерфейс (GUI)

### Запуск

**Способ 1: Двойной клик**
```
Дважды кликните по файлу "run.bat"
```

**Способ 2: Command Prompt**
```bash
python main.py
```

### Пример использования

1. **Запустить программу**
   ```bash
   run.bat
   ```

2. **Выбрать формат** (ново!)
   - Из выпадающего списка "Формат": выбрать "APK" или "AAB"
   - Подходящее описание появится автоматически

3. **Выбрать APK/AAB файл**
   - Нажать кнопку "Обзор" 
   - Диалог будет фильтровать файлы по выбранному формату

4. **Выбрать папку для сохранения**
   - Нажать кнопку "Обзор" рядом с "Папка для сохранения файлов"
   - Выбрать `C:\signed_apps`

5. **Нажать "Генерировать ключи и подписи"**

6. **Получить файлы:**
   - `C:\signed_apps\app-signed.apk` (если выбран формат APK)
   - `C:\signed_apps\app-signed.aab` (если выбран формат AAB)
   - `C:\signed_apps\certificate.pem` - сертификат
   - `C:\signed_apps\playmarket_info.txt` - инфа для Play Market
   - `C:\signed_apps\rustore_info.txt` - инфа для RuStore (только если APK)

---

## 🖥️ Командная строка (CLI)

### Базовое использование

#### Пример 1: Простая подпись APK
```bash
python cli.py C:\Downloads\app-release.apk
```

Результаты будут сохранены в той же папке, что и APK.

#### Пример 1b: Простая подпись AAB (новое!)
```bash
python cli.py C:\Downloads\app-release.aab --format aab
```

Результаты будут сохранены в той же папке, что и AAB.

#### Пример 2: Указать папку вывода
```bash
python cli.py C:\Downloads\app-release.apk -o C:\signed_apps
```

#### Пример 2b: AAB с папкой вывода (новое!)
```bash
python cli.py C:\Downloads\app-release.aab --format aab -o C:\signed_apps
```

#### Пример 3: Использовать существующий keystore
```bash
python cli.py app-release.apk -k my-keystore.jks -o output_folder
```

#### Pример 3b: AAB с существующим keystore (новое!)
```bash
python cli.py app-release.aab --format aab -k my-keystore.jks -o output_folder
```

#### Пример 4: Все параметры сразу
```bash
python cli.py "C:\Users\User\Downloads\my-app.apk" ^
  -k "C:\keystore\app-key.jks" ^
  -o "C:\signed_apps"
```

#### Пример 4b: AAB со всеми параметрами (новое!)
```bash
python cli.py "C:\Users\User\Downloads\my-app.aab" ^
  --format aab ^
  -k "C:\keystore\app-key.jks" ^
  -o "C:\signed_apps"
```

### Вывод программы

```
[12:34:56] CHECK    Проверка требований
============================================================
[12:34:57] ✓ OK     Java Development Kit найден
============================================================
[12:34:58] INFO     ℹ INFO     Проверка APK: C:\Downloads\app.apk
[12:34:58] ✓ OK     APK файл корректен (45.23 MB)
[12:34:59] INFO     Создание Keystore: C:\signed_apps\app-release-key.jks
[12:34:59] ✓ OK     Keystore создан успешно
[12:34:59] INFO     Пароль keystore: android_key_password
[12:35:00] INFO     Подпись APK файла...
[12:35:01] ✓ OK     APK подписан (jarsigner)
[12:35:02] INFO     Экспорт сертификата в PEM...
[12:35:02] ✓ OK     Сертификат экспортирован
[12:35:02] INFO     SHA256: abc123...
[12:35:03] INFO     Подготовка файлов для Play Market...
[12:35:03] ✓ OK     Информация для Play Market сохранена
[12:35:04] ✓ OK     Генерация завершена успешно!
============================================================
```

---

## 🎬 Полный рабочий процесс

### Сценарий 1: Первая загрузка на Play Market

```bash
# Шаг 1: Подписать APK
python cli.py my-app.apk -o signed_app

# Шаг 2: Откройте Google Play Console
# https://play.google.com/console

# Шаг 3: Создайте новое приложение

# Шаг 4: На странице подписи:
# - Загрузите подписанный APK: signed_app/app-signed.apk
# - Скопируйте SHA256 из: signed_app/playmarket_info.txt
# - Вставьте в платформу

# Шаг 5: Завершите процесс загрузки
```

### Сценарий 2: Загрузка на RuStore

```bash
# Шаг 1: Подписать APK
python cli.py my-app.apk -o signed_app

# Шаг 2: Откройте RuStore Developer Console
# https://developer.rustore.ru/

# Шаг 3: Создайте новое приложение

# Шаг 4: На странице подписи загрузите:
# - APK файл: signed_app/app-signed.apk
# - PEM сертификат: signed_app/certificate.pem

# Шаг 5: Проверьте SHA256 соответствие в signed_app/rustore_info.txt

# Шаг 6: Завершите загрузку
```

### Сценарий 3: Обновление существующего приложения

```bash
# Используйте ТОТ ЖЕ keystore для обновления!

# Первая загрузка:
python cli.py app-v1.apk -o signed_v1
# Сохраните файл app-release-key.jks

# Обновление приложения:
python cli.py app-v2.apk ^
  -k signed_v1/app-release-key.jks ^
  -o signed_v2

# ВАЖНО: Никогда не создавайте новый keystore для обновления!
# Иначе платформы не узнают обновление.
```

---

## 🛠️ Примеры в разных папках

### Linux/macOS

```bash
# Простое использование
python3 cli.py ~/Downloads/app.apk

# С параметрами
python3 cli.py ~/Downloads/app.apk \
  -k ~/keystore/app-key.jks \
  -o ~/signed_apps
```

### Windows PowerShell

```powershell
# Простое использование
python cli.py "C:\Downloads\app.apk"

# С параметрами
python cli.py "C:\Downloads\app.apk" `
  -k "C:\keystore\app-key.jks" `
  -o "C:\signed_apps"
```

### Windows Command Prompt

```batch
REM Простое использование
python cli.py C:\Downloads\app.apk

REM С параметрами
python cli.py C:\Downloads\app.apk ^
  -k C:\keystore\app-key.jks ^
  -o C:\signed_apps
```

---

## 📋 Параметры CLI

```
Использование: python cli.py APK_FILE [-k KEYSTORE] [-o OUTPUT]

Обязательные:
  APK_FILE              Путь к APK файлу

Опциональные:
  -k, --keystore PATH   Путь к файлу keystore
                        (создастся новый, если не указан)
  -o, --output PATH     Папка для сохранения файлов
                        (по умолчанию папка APK)

Примеры:
  python cli.py app.apk
  python cli.py app.apk -o ./signed
  python cli.py app.apk -k my.jks -o ./signed
```

---

## 🔄 Автоматизация (пакетная обработка)

### Обработка нескольких APK файлов (Windows batch)

Создайте файл `process_all.bat`:
```batch
@echo off
for %%f in (*.apk) do (
    echo Processing %%f...
    python cli.py "%%f" -o "signed_%%~nf"
)
```

Запуск:
```bash
process_all.bat
```

### Обработка нескольких APK файлов (PowerShell)

```powershell
Get-ChildItem -Filter "*.apk" | ForEach-Object {
    Write-Host "Processing $($_.Name)..."
    python cli.py $_.FullName -o "signed_$($_.BaseName)"
}
```

---

## 🐛 Отладка ошибок

### Включение подробного вывода

```bash
# Проверить требования
python cli.py app.apk --help

# Проверить систему
check_requirements.bat
```

### Сохранение лога в файл

```bash
# Windows
python cli.py app.apk > log.txt 2>&1

# Linux/macOS
python3 cli.py app.apk | tee log.txt
```

---

## 💾 Сохранение важных данных

После успешной подписи:

```bash
# Сохраните keystore в безопасном месте
cp signed_app/app-release-key.jks ../backup/app-release-key.jks

# Сохраните информацию о подписи
cp signed_app/playmarket_info.txt ../backup/playmarket_info.txt
cp signed_app/rustore_info.txt ../backup/rustore_info.txt
```

---

## ⚠️ Важные замечания

1. **Никогда не теряйте keystore файл!**
   ```
   Без keystore вы не сможете обновлять приложение
   ```

2. **Один keystore на одно приложение**
   ```
   Используйте один и тот же keystore для всех версий
   ```

3. **Идентичные подписи для обновлений**
   ```
   SHA256 должны совпадать между версиями
   ```

4. **Резервные копии**
   ```
   Сохраняйте keystore в нескольких местах
   ```

---

**Версия:** 1.0  
**Последнее обновление:** Март 2026
