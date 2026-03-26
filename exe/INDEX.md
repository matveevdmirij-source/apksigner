# 📚 APK Signer - Полная документация и навигация

Вся информация о программе разбита по файлам для удобства навигации.

## 🎯 Выбери свою версию

### ✅ Java Basic версия (рекомендуется для начинающих)
```
для: подпись готовых APK файлов
запуск: run_java.bat
время: 1-2 минуты до результата  
требует: только Java (уже есть!)
```
➜ Начните с [QUICKSTART.md](QUICKSTART.md)

### ⭐ Java Advanced версия (для профессионалов)  
```
для: пересборка Debug→Release + подпись
запуск: run_advanced.bat
время: пересборка + подпись (зависит от проекта)
требует: Java + Gradle + Android проект
новое: REBUILD_GUIDE.md (полная инструкция)
```
➜ Начните с [REBUILD_GUIDE.md](REBUILD_GUIDE.md)

### 🐍 Python версия (для разработчиков)
```
для: полный контроль, кроссплатформа
запуск: run_python.bat
требует: Python 3.6+
особенность: можно кастомизировать код
```
➜ Начните с [INSTALL.md](INSTALL.md)

## 📖 Полный каталог документации

| 📄 Документ | 🎯 Назначение | 👥 Для | ⏱️ Время |
|------------|-------------|--------|---------|
| [QUICKSTART.md](QUICKSTART.md) | ⚡ За 5 минут к результату | Все | 5 мин |
| [QUICK_AAB_START.md](QUICK_AAB_START.md) | 🆕 AAB за 5 минут | Все | 5 мин |
| [README.md](README.md) | 📘 Полный гайд (Python) | Разработчики | 15 мин |
| [README_JAVA.md](README_JAVA.md) | ☕ Полный гайд (Java) | Java пользователи | 10 мин |
| [REBUILD_GUIDE.md](REBUILD_GUIDE.md) | 🔨 Пересборка Debug→Release | Pro пользователи | 20 мин |
| [AAB_SUPPORT.md](AAB_SUPPORT.md) | 🆕 🎁 Полная информация про AAB | Все | 10 мин |
| [VERSION_1.2_RELEASE_NOTES.md](VERSION_1.2_RELEASE_NOTES.md) | 🆕 📦 Что нового в v1.2 | Все | 5 мин |
| [INSTALL.md](INSTALL.md) | 🔧 Установка софта | Новички | 10 мин |
| [EXAMPLES.md](EXAMPLES.md) | 📝 Готовые примеры | Все | 5 мин |
| [TROUBLESHOOTING.md](TROUBLESHOOTING.md) | 🆘 Решение проблем | При ошибках | 5-10 мин |

## 🚀 Быстрый старт по версиям

### 👉 Я новичок с APK файлом
```
1. Прочитайте: QUICKSTART.md
2. Запустите: run_java.bat
3. Выберите: готовый APK файл  
4. Подпишите: нажмите кнопку
5. Готово! ✓
```

### 👉 Я профессионал с кодом Android
```
1. Прочитайте: REBUILD_GUIDE.md
2. Запустите: run_advanced.bat
3. Выберите: папку Android проекта
4. Пересоберите: нажмите кнопку
5. Получите: готовый signed APK ✓
```

### 👉 Я разработчик Python
```
1. Установите: Python (смотрите INSTALL.md)
2. Запустите: pip install -r requirements.txt
3. Запустите: python main.py
4. Опробуйте: функциональность
5. Кастомизируйте: под свои нужды ✓
```

## 📋 Документация по разделам

### 🟢 ДЛЯ НАЧИНАЮЩИХ
- **[QUICKSTART.md](QUICKSTART.md)** - За 5 минут до результата!
  - Самый короткий путь
  - Пошаговые скриншоты
  - Готово вам придётся подождать 2 минуты
  
### 🟡 ДЛЯ УСТАНОВКИ
- **[INSTALL.md](INSTALL.md)** - Установка Python, Java, и всего остального
  - Windows, macOS, Linux инструкции
  - Проверка установок
  - Решение типичных проблем
  - PATH и переменные окружения

### 🟣 ДЛЯ JAVA ПОЛЬЗОВАТЕЛЕЙ
- **[README_JAVA.md](README_JAVA.md)** - Всё о Java версии
  - Функции Java GUI
  - Запуск и использование
  - Ошибки и их решение
  - Преимущества и недостатки
  
- **[REBUILD_GUIDE.md](REBUILD_GUIDE.md)** - НОВОЕ! Пересборка Android проектов
  - Что такое Debug и Release
  - Как использовать gradle
  - Этапы пересборки
  - Частые проблемы при пересборке

### 🔴 ДЛЯ PYTHON РАЗРАБОТЧИКОВ  
- **[README.md](README.md)** - Полный гайд Python версии
  - Особенности каждой функции
  - Как запустить CLI и GUI
  - Примеры кода
  - Безопасность и советы

### 🔵 ДЛЯ ПРИМЕРОВ
- **[EXAMPLES.md](EXAMPLES.md)** - Готовые рабочие процессы
  - GUI: пошаговое подписание
  - CLI: команды командной строки
  - Полные примеры для реальных случаев
  - Как отладить и логировать

### 🟣 ДЛЯ РЕШЕНИЯ ПРОБЛЕМ
- **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** - 14+ типов ошибок
  - Полное описание каждой ошибки
  - Почему она возникает
  - Как её исправить
  - Примеры решений
  - Инструменты диагностики

## 🗂️ Полная структура всех файлов

```
c:\Users\Admin\Desktop\exe\
│
├── 🚀 ЗАПУСК (Двойной клик!)
│   ├── run_java.bat                 ← Основная Java версия
│   ├── run_advanced.bat             ← Advanced версия (НОВАЯ!)
│   ├── run_python.bat               ← Python версия
│   ├── run_cli.bat                  ← Python CLI версия
│   └── check_requirements.bat       ← Проверка установок
│
├── ☕ JAVA ИСХОДНЫЙ КОД И КЛАССЫ
│   ├── APKSigner.java               ← Базовая Java версия (400 строк)
│   ├── APKSigner.class              ← Скомпилированный класс
│   ├── APKSignerAdvanced.java       ← Advanced версия (600 строк) - НОВАЯ!
│   ├── APKSignerAdvanced.class      ← Скомпилированный класс
│   │
│   └── Методы включены:
│       ├── createKeystore()
│       ├── signAPK()
│       ├── exportCertificate()
│       ├── generatePlayMarketSignature()
│       ├── generateRuStoreSignature()
│       ├── rebuildProject() ← НОВЫЙ метод!
│       ├── runGradleCommand() ← НОВЫЙ метод!
│       └── findReleaseApk() ← НОВЫЙ метод!
│
├── 🐍 PYTHON ИСХОДНЫЙ КОД
│   ├── main.py                      ← Python GUI приложение (500 строк)
│   ├── cli.py                       ← Python CLI программа (300 строк)
│   ├── __init__.py                  ← Метаинформация проекта
│   └── requirements.txt             ← Требуемые пакеты Python
│
└── 📚 ДОКУМЕНТАЦИЯ (ВЫ ЗДЕСЬ!)
    ├── INDEX.md                     ← Этот файл - навигация
    ├── QUICKSTART.md                ← ⭐ Начните отсюда!
    ├── README.md                    ← Полный гайд Python версии
    ├── README_JAVA.md               ← Полный гайд Java версии
    ├── REBUILD_GUIDE.md             ← Пересборка Debug→Release - НОВАЯ!
    ├── INSTALL.md                   ← Установка Python/Java/инструментов
    ├── EXAMPLES.md                  ← Готовые примеры использования
    ├── TROUBLESHOOTING.md           ← Решение 14+ типов ошибок
    ├── LICENSE                      ← MIT License
    └── requirements.txt             ← Зависимости для Python
```

## 🎓 Пути обучения

### 🆕 Путь новичка (15 минут)
```
1. QUICKSTART.md          (5 мин - результат!)
2. Запустить run_java.bat (2 мин - видеть интерфейс)
3. Выбрать APK и подписать (3 мин - практика)
4. Посмотреть результаты  (1 мин - готово!)
5. Читать документацию при необходимости
```

### 💼 Путь профессионала (45 минут)
```
1. INSTALL.md             (10 мин - убедиться что все установлено)
2. README_JAVA.md         (10 мин - понять архитектуру)
3. REBUILD_GUIDE.md       (15 мин - изучить новые функции)
4. Запустить run_advanced.bat + опробовать (10 мин)
5. TROUBLESHOOTING.md     (при необходимости)
```

### 🧑‍💻 Путь разработчика (60 минут)
```
1. INSTALL.md             (10 мин - установить Python)
2. README.md              (15 мин - понять код Python)
3. main.py / cli.py       (15 мин - изучить исходный код)
4. Запустить python main.py + CLI (10 мин - тестировать)
5. Модифицировать под свои нужды (10 мин - кастомизация)
```

## 📊 Сравнение версий

| Функция | Basic | Advanced | Python |
|---------|-------|----------|--------|
| Запуск | run_java.bat | run_advanced.bat | python main.py |
| Подпись готового APK | ✅ | ✅ | ✅ |
| Пересборка Debug→Release | ❌ | ✅ | ❌ |
| Gradle интеграция | ❌ | ✅ | ❌ |
| Две вкладки | ❌ | ✅ | ❌ |
| Экспорт PEM | ✅ | ✅ | ✅ |
| Выпуск для маркетов | ✅ | ✅ | ✅ |
| Требования | Java | Java + Gradle | Python 3.6+ |
| Размер | 400 KB | 600 KB | 2 MB |

## 🔐 Вопросы безопасности

### 🔒 Где хранятся мои ключи?
```
После подписи найдёте в папке вывода:
- app-release-key.jks (Keystore с приватным ключом)
- certificate.pem (Публичный сертификат)
```

### ⚠️ Чем опасны приватные ключи?
```
❌ НИКОГДА не выкладывайте keystore в репозиторий
❌ НИКОГДА не делитесь keystore файлом
❌ НИКОГДА не забирайте пароль от keystore
✅ ВСЕГДА храните keystore в безопасном месте
✅ ВСЕГДА используйте ОДИ keystore для версии
```

### 🔑 Стандартные пароли
```
Keystore password: android_key_password
Key password: android_key_password
Alias: android_key
Key algorithm: RSA 2048-bit
Validity: 10000 days (~27 лет)
```

## 🆘 Быстрая помощь

### Что выбрать?
```
❓ У меня готовый APK
✓ Используйте Basic версию (run_java.bat)

❓ У меня исходный код Android проекта  
✓ Используйте Advanced версию (run_advanced.bat)

❓ Я разработчик и хочу кастомизировать код
✓ Используйте Python версию + читайте README.md
```

### Где найти ошибку?
```
1️⃣ Посмотрите окно логирования в программе (прямо в интерфейсе)
2️⃣ Откройте TROUBLESHOOTING.md и найдите свою ошибку
3️⃣ Запустите check_requirements.bat (проверить установки)
4️⃣ Проверьте Java версию: java -version
```

### Где найти результаты?
```
После подписи файлы сохраняются в папке вывода:
✅ app-signed.apk              - готовый для маркета
✅ certificate.pem             - сертификат
✅ playmarket_info.txt         - для Google Play
✅ rustore_info.txt            - для RuStore
✅ app-release-key.jks         - для будущих подписей
```

## 🎯 Частые вопросы

**Q: Какую версию выбрать?**  
A: Начните с Basic (run_java.bat), если понадобится пересборка - перейдите на Advanced

**Q: Что такое Release и Debug?**  
A: Debug = с отладкой, большой размер. Release = оптимизирован, меньше размер, надо подписать

**Q: Нужен ли интернет?**  
A: Для Advanced версии да (при пересборке gradle скачает зависимости)

**Q: Это бесплатно?**  
A: Да, MIT License (смотрите LICENSE файл)

**Q: Могу ли я использовать для своего приложения?**  
A: Да! Модифицируйте под свои нужды

## 📞 Версия и статус

```
Версия: 1.1 Advanced
Статус: ✅ Готово к использованию
Java версия: 11.0.17+  
Последнее обновление: Март 2026

В этой версии:
✅ 2 Java GUI приложения (Basic + Advanced с пересборкой)
✅ 2 Python программы (GUI + CLI)
✅ 8 файлов документации (~50 KB текста)
✅ 14+ файлов ошибок с решениями
✅ Примеры использования для обоих маркетов
✅ Скрипты запуска для быстрого старта
✅ Проверка требований (check_requirements.bat)
```

---

## 🚀 Начните прямо сейчас!

### Вариант 1: Быстрый старт (2 минуты)
```
1. Двойной клик: run_java.bat
2. Выбрать: APK файл
3. Нажать: кнопку подписи
4. Результат: в папке вывода ✓
```

### Вариант 2: С инструкциями (10 минут)
```
1. Прочитать: QUICKSTART.md
2. Запустить: run_java.bat  
3. Следовать: инструкциям в программе
4. Результат: готовый signed APK ✓
```

### Вариант 3: Профессиональное использование
```
1. Прочитать: REBUILD_GUIDE.md
2. Запустить: run_advanced.bat
3. Выбрать: Android проект
4. Результат: пересобранный и подписанный APK ✓
```

---

**Выбирайте свой путь выше и начинайте! 🎉**  
_Вопросы? Смотрите TROUBLESHOOTING.md_

## 🎯 Выбрать нужный документ

### Помогите, я новичок!
→ Прочитайте [QUICKSTART.md](QUICKSTART.md)

### Как установить программу?
→ Прочитайте [INSTALL.md](INSTALL.md)

### Как использовать программу?
→ Прочитайте [README.md](README.md) или [README_JAVA.md](README_JAVA.md)

### Хочу примеры использования
→ Прочитайте [EXAMPLES.md](EXAMPLES.md)

### Ошибка при подписи APK!
→ Прочитайте [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

### Какие ошибки возможны?
→ Смотрите раздел "Ошибки при создании подписи" в [README.md](README.md) или [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

### Нужны примеры команд для CLI
→ Прочитайте [EXAMPLES.md](EXAMPLES.md)

### Нужна информация о лицензии
→ Прочитайте [LICENSE](LICENSE)

## 🔍 Быстрый поиск по ключевым словам

### Java
- [README_JAVA.md](README_JAVA.md) - Документация
- [run_java.bat](run_java.bat) - Запуск
- Раздел "Java версия" в [INSTALL.md](INSTALL.md)

### Python
- [README.md](README.md) - Документация
- [run.bat](run.bat) - Запуск
- [INSTALL.md](INSTALL.md) - Установка

### Ошибки keystore
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md#проблема-keystore-повреждён-или-утерян)
- [README.md](README.md) → Раздел "Ошибки"

### Ошибки APK
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md#проблема-apk-файл-повреждён)
- [README.md](README.md) → Раздел "Ошибки"

### Подпись на Play Market
- [README.md](README.md) → Раздел "Загрузка на Google Play Market"
- [EXAMPLES.md](EXAMPLES.md) → Сценарий 1

### Подпись на RuStore
- [README.md](README.md) → Раздел "Загрузка на RuStore"
- [EXAMPLES.md](EXAMPLES.md) → Сценарий 2

### Безопасность
- [README.md](README.md) → Раздел "Безопасность"
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md#-ошибки-безопасности)

## 📋 Чек-лист перед началом

- ✅ Прочитано [QUICKSTART.md](QUICKSTART.md)  
- ✅ Запущен [check_requirements.bat](check_requirements.bat)
- ✅ Java установлена и работает
- ✅ APK файл подготовлен (целостный)
- ✅ Папка для результатов выбрана
- ✅ Прочитано руководство [README.md](README.md) или [README_JAVA.md](README_JAVA.md)

## 🤝 Если что-то непонятно

1. **Прочитайте документ** соответствующего раздела (выше)
2. **Поищите в [TROUBLESHOOTING.md](TROUBLESHOOTING.md)**
3. **Проверьте [EXAMPLES.md](EXAMPLES.md)** для примеров
4. **Проверьте FAQ** в [README.md](README.md)

## 🔗 Связанные ресурсы

- **Google Play Console:** https://play.google.com/console
- **RuStore Developer:** https://developer.rustore.ru/
- **Java官 Documentation:** https://www.oracle.com/java/
- **Android App Signing:** https://developer.android.com/studio/publish/app-signing

## 📝 История документации

| Версия | Дата | Изменения |
|--------|------|----------|
| 1.0 | Март 2026 | Первая версия с полной документацией |

## 🎓 Рекомендуемый порядок чтения

### Для новичков:
1. [QUICKSTART.md](QUICKSTART.md) - 5 минут
2. [README.md](README.md) - детали
3. [EXAMPLES.md](EXAMPLES.md) - примеры
4. [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - если проблемы

### Для опытных:
1. [EXAMPLES.md](EXAMPLES.md) - примеры
2. [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - ошибки
3. Прямо к использованию!

---

**Версия документации:** 1.0  
**Последнее обновление:** Март 2026  
**Язык:** Русский + Примеры на English/Русском
