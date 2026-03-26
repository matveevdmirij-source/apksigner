@echo off
REM APK Signer CLI - Версия с интерфейсом командной строки
REM Использование: cli.bat path/to/app.apk [-k keystore.jks] [-o output_folder]

echo.
echo ====================================
echo APK Signer CLI - Генератор подписей
echo ====================================
echo.

REM Проверка Python
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Ошибка: Python не найден!
    echo Пожалуйста, установите Python с сайта https://www.python.org/downloads/
    pause
    exit /b 1
)

REM Проверка Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Ошибка: Java не найдена!
    echo Пожалуйста, установите JDK с сайта https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

REM Запуск CLI
python cli.py %*

if %errorlevel% neq 0 (
    echo.
    echo Ошибка при выполнении!
    pause
)
