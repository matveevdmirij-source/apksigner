@echo off
REM APK Signer Advanced - Версия с функцией пересборки
REM Графический интерфейс с использованием Java Swing
REM Включает сборку Debug версии в Release

setlocal enabledelayedexpansion

echo.
echo ===============================================
echo APK Signer Advanced для Play Market и RuStore
echo Java версия (Swing GUI)
echo Включает пересборку Android проекта
echo ===============================================
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
javac -encoding UTF-8 APKSignerAdvanced.java >nul 2>&1
if %errorlevel% neq 0 (
    echo Ошибка при компиляции!
    javac -encoding UTF-8 APKSignerAdvanced.java
    pause
    exit /b 1
)

echo Запуск приложения...
java APKSignerAdvanced

if %errorlevel% neq 0 (
    echo.
    echo Ошибка при запуске приложения!
    pause
)
