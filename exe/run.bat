@echo off
REM APK Signer для Play Market и RuStore
REM Батник для запуска программы с GUI

echo.
echo ====================================
echo APK Signer для Play Market и RuStore
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

REM Запуск программы
echo Запуск приложения...
python main.py

if %errorlevel% neq 0 (
    echo.
    echo Ошибка при запуске приложения!
    pause
)
