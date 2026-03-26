"""
APK Signer для Play Market и RuStore

Приложение для генерации PEM ключей и цифровых подписей APK файлов

Версия: 1.0
Дата: Март 2026
Язык: Python 3.7+
ОС: Windows, macOS, Linux

Модули:
------
main.py          - Графический интерфейс (GUI)
cli.py          - Интерфейс командной строки (CLI)
run.bat         - Запуск GUI версии
cli.bat         - Запуск CLI версии
check_requirements.bat - Проверка требований

Требования:
----------
- Python 3.7 или выше
- Java Development Kit (JDK) 8+
- OpenSSL (опционально)

Функции:
-------
✓ Создание нового Keystore
✓ Подпись APK файлов
✓ Экспорт сертификатов в PEM
✓ Генерация подписей для Google Play Market
✓ Генерация подписей для RuStore
✓ Вычисление SHA256 и SHA1 отпечатков
✓ Графический и командно-строчный интерфейсы

Использование (GUI):
--------------------
python main.py
или двойной клик по run.bat

Использование (CLI):
--------------------
python cli.py app.apk
python cli.py app.apk -o output_folder
python cli.py app.apk -k keystore.jks -o output

Дополнительно:
--------------
QUICKSTART.md   - Быстрый старт за 5 минут
README.md       - Полная документация
INSTALL.md      - Инструкции по установке
EXAMPLES.md     - Примеры использования
LICENSE         - Лицензия

Контакт:
--------
GitHub: [ваш репозиторий]
issues: [ссылка на issues]

Лицензия:
---------
Это программное обеспечение предоставляется как есть.
Смотрите файл LICENSE для подробностей.

Дополнительно:
--------------
- Все пароли по умолчанию: android_key_password
- Размер ключа: 2048 бит (RSA)
- Срок действия: 10000 дней (~27 лет)
- Цель: Облегчить процесс подписи APK для разработчиков
"""

__version__ = "1.0"
__author__ = "APK Signer Team"
__license__ = "MIT-like"
__python_requires__ = ">=3.7"

# Требуемые переменные окружения:
# JAVA_HOME - путь к папке Java (например: C:\Program Files\Java\jdk-21)
# PATH - должен содержать bin папку Java

# Проверка совместимости систем:
# - Windows XP SP3+ (Python 3.7+)
# - Windows Vista+
# - macOS 10.9+
# - Linux (Ubuntu 16.04+, Fedora 22+, CentOS 7+)

# Размер упаковки:
# - Исходные файлы: ~50 KB
# - С зависимостями Python: ~50 MB (если не установлен Python)

# Рекомендуемые параметры компилятора (для создания EXE):
# pyinstaller --onefile --windowed --add-data "templates:templates" main.py
# pyinstaller --onefile cli.py

# Тестирование:
# - Протестировано на Python 3.7, 3.8, 3.9, 3.10, 3.11
# - Протестировано на Windows 10/11, macOS 12+, Ubuntu 20.04+
# - Совместимо с Java 8, 11, 17, 21

# Выход файлов:
# app-release-key.jks      - Java Keystore с приватным ключом
# app-signed.apk           - Подписанный APK файл
# certificate.pem          - Публичный сертификат (PEM)
# certificate.der          - Публичный сертификат (DER)
# playmarket_certificate.pem - Сертификат для Play Market
# playmarket_fingerprints.txt - Отпечатки для Play Market
# rustore_signature_info.txt - Информация для RuStore
# app-release-key.jks.info - Метаинформация о keystore (JSON)

# Future features:
# - Поддержка подписей Android App Bundle (.aab)
# - Web интерфейс
# - Поддержка множественных приложений одновременно
# - Интеграция с облачными хранилищами
# - Автоматическая загрузка на маркеты
# - Поддержка Gradle плагинов
# - CI/CD интеграция (GitHub Actions, GitLab CI, etc)
