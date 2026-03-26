@echo off
REM APK Signer для Play Market и RuStore (Java версия)
REM Графический интерфейс с использованием Java Swing

setlocal enabledelayedexpansion

echo.
echo ====================================
echo APK Signer для Play Market и RuStore
echo Java версия (Swing GUI)
echo ====================================
echo.

REM Проверка Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Ошибка: Java не найдена!
    echo Пожалуйста, установите JDK с сайта https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Компиляция приложения...
javac -encoding UTF-8 APKSigner.java >nul 2>&1
if %errorlevel% neq 0 (
    echo Ошибка при компиляции!
    javac -encoding UTF-8 APKSigner.java
    pause
    exit /b 1
)

echo Запуск приложения...
java APKSigner

if %errorlevel% neq 0 (
    echo.
    echo Ошибка при запуске приложения!
    pause
)
