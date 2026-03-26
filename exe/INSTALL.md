# Руководство по установке APK Signer

## 🎯 Краткая инструкция

1. **Установить Python 3.7+**
2. **Установить Java Development Kit (JDK)**
3. **Запустить программу**

---

## 📋 Подробная инструкция для Windows

### Шаг 1: Установка Python

#### Способ 1: Официальный сайт
1. Перейти на https://www.python.org/downloads/
2. Скачать **Python 3.11** (или новее)
3. Запустить установщик
4. **ВАЖНО:** Отметить галочку "Add Python to PATH"
5. Нажать "Install Now"
6. Дождаться завершения

#### Способ 2: Microsoft Store
1. Открыть **Microsoft Store**
2. Поискать "Python 3"
3. Установить (3.11 или новее)
4. Нажать "Получить"

#### Проверка установки
Открыть Command Prompt и выполнить:
```bash
python --version
```
Должно вывести версию Python (например: `Python 3.11.0`)

---

### Шаг 2: Установка Java Development Kit

#### Способ 1: Oracle JDK (рекомендуется)
1. Перейти на https://www.oracle.com/java/technologies/downloads/
2. Скачать **JDK 21** (Latest Release)
3. Выбрать установщик для Windows (x64)
4. Запустить установщик
5. Принять лицензионное соглашение
6. Выбрать путь установки (можно оставить по умолчанию)
7. Нажать "Next" и дождаться завершения

#### Способ 2: AdoptOpenJDK
1. Перейти на https://adoptopenjdk.net/
2. Скачать **LTS версию** для Windows
3. Запустить и установить

#### Способ 3: Установка через Chocolatey (если установлен)
```bash
choco install jdk21
```

#### Способ 4: Установка через Windows Package Manager
```bash
winget install Oracle.JDK.21
```

#### Проверка установки
Открыть Command Prompt и выполнить:
```bash
java -version
keytool -help
```

Если вывели информацию о версии Java - всё установлено правильно!

---

### Шаг 3: Добавление Java в PATH (если требуется)

Если `java` или `keytool` не найдены:

1. Открыть **Параметры системы**
   - Нажать **Win + X** → **Параметры системы**
   - Или: Панель управления → Система и безопасность → Система

2. Перейти в **Дополнительные параметры системы**
   - Нажмите на **"Параметры среды"** или **"Environment Variables"**

3. Добавить путь к JDK
   - Нажать **"Переменные среды"** → **Новую переменную**
   - Имя: `JAVA_HOME`
   - Значение: `C:\Program Files\Java\jdk-21` (проверить точный путь!)
   - Нажать **OK**

4. Обновить PATH
   - Дважды кликнуть на переменную `Path`
   - Нажать **Создать**
   - Добавить: `C:\Program Files\Java\jdk-21\bin`
   - Нажать **OK**

5. Перезагрузить компьютер или перезапустить Command Prompt

---

### Шаг 4: Запуск APK Signer

#### Вариант 1: Графический интерфейс
Двойной клик по файлу `run.bat`

Или в Command Prompt:
```bash
python main.py
```

#### Вариант 2: Командная строка
В Command Prompt:
```bash
python cli.py app-release.apk
```

Или:
```bash
cli.bat app-release.apk -o output_folder
```

---

## 🔍 Проверка требований

Запустить скрипт проверки:
```bash
check_requirements.bat
```

Выведется результат проверки всех требований.

---

## ❌ Решение проблем

### Проблема: "Python not found"
**Решение:**
1. Переустановить Python
2. Убедиться, что скалка "Add to PATH" отмечена
3. Перезагрузить компьютер

### Проблема: "Java not found" или "keytool not found"
**Решение:**
1. Убедиться, что JDK установлен (не JRE!)
2. Добавить JDK в PATH (см. выше)
3. Перезагрузить компьютер

### Проблема: "Permission denied" при запуске .bat файла
**Решение:**
1. Открыть Command Prompt от администратора
2. Перейти в папку с программой
3. Запустить: `python main.py`

### Проблема: "Невозможно найти файл AndroidManifest.xml"
**Решение:**
1. Убедиться, что выбран правильный APK файл
2. Переименовать файл (удалить спецсимволы)
3. Скопировать в папку программы

---

## 📱 Установка на других системах

### macOS

```bash
# Установка Python через Homebrew
brew install python@3.11

# Установка Java
brew install openjdk@21

# Запуск программы
python3 main.py
```

### Linux (Ubuntu/Debian)

```bash
# Установка Python
sudo apt update
sudo apt install python3.11 python3-tk

# Установка Java
sudo apt install openjdk-21-jdk

# Запуск программы
python3 main.py
```

### Linux (Fedora/RHEL)

```bash
# Установка Python и Java
sudo dnf install python3.11 java-21-openjdk

# Запуск программы
python3 main.py
```

---

## 🚀 Быстрая инсталляция через скрипты

### Для опытных пользователей

**Windows (PowerShell от администратора):**
```powershell
# Установка Python через Chocolatey
choco install python java

# Проверка
python --version
java -version

# Запуск
python main.py
```

---

## 📚 Полезные ссылки

- **Python**: https://www.python.org/downloads/
- **Oracle JDK**: https://www.oracle.com/java/technologies/downloads/
- **AdoptOpenJDK**: https://adoptopenjdk.net/
- **OpenSSL**: https://slproweb.com/products/Win32OpenSSL.html
- **Android SDK**: https://developer.android.com/studio/command-line/sdkmanager

---

## ✅ Проверка успешной установки

Все требования установлены, если следующие команды работают:

```bash
python --version
# Вывод: Python 3.11.0 (или новее)

java -version
# Выведет информацию о Java

keytool
# Выведет справку по keytool
```

---

## 💡 Советы

1. **Используйте Command Prompt** от имени администратора при установке
2. **Перезагружайте компьютер** после установки Java и добавления в PATH
3. **Проверяйте пути** в переменных окружения если возникают ошибки
4. **Используйте长пути без пробелов** в названиях папок для безопасности

---

## 🆘 Если ничего не помогает

1. Откройте **Command Prompt** и запустите:
   ```bash
   check_requirements.bat
   ```

2. Скопируйте вывод и проверьте какие компоненты не установлены

3. Установите отсутствующие компоненты согласно инструкциям выше

4. Если ошибки остаются, проверьте в интернете вашу конкретную ошибку

---

**Последнее обновление:** Март 2026  
**Для APK Signer v1.0**
