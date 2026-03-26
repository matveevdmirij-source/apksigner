@echo off
REM Скрипт проверки и установки требований для APK Signer

setlocal enabledelayedexpansion

echo.
echo ====================================
echo Проверка требований для APK Signer
echo ====================================
echo.

set requirements_ok=1

REM Проверка Python
echo [1/3] Проверка Python...
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo   [X] Python НЕ НАЙДЕН
    echo   Скачайте и установите с: https://www.python.org/downloads/
    echo   При установке отметьте "Add Python to PATH"
    set requirements_ok=0
) else (
    for /f "tokens=*" %%i in ('python --version 2^>^&1') do set python_version=%%i
    echo   [OK] !python_version!
)

REM Проверка Java
echo.
echo [2/3] Проверка Java Development Kit...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo   [X] Java НЕ НАЙДЕНА
    echo   Скачайте и установите с: https://www.oracle.com/java/technologies/downloads/
    set requirements_ok=0
) else (
    for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do set java_version=%%i
    echo   [OK] !java_version!
)

REM Проверка keytool
echo.
echo [3/3] Проверка keytool (часть JDK)...
keytool -help >nul 2>&1
if %errorlevel% neq 0 (
    echo   [X] keytool НЕ НАЙДЕН
    echo   Добавьте путь JDK в переменную PATH
    echo   Обычно: C:\Program Files\Java\jdk-[версия]\bin
    set requirements_ok=0
) else (
    echo   [OK] keytool найден
)

REM Опциональная проверка OpenSSL
echo.
echo [ОПЦИОНАЛЬНО] Проверка OpenSSL...
openssl version >nul 2>&1
if %errorlevel% neq 0 (
    echo   [~] OpenSSL НЕ найден (опционально)
    echo   Для альтернативных способов установите: https://slproweb.com/products/Win32OpenSSL.html
) else (
    for /f "tokens=*" %%i in ('openssl version 2^>^&1') do set openssl_version=%%i
    echo   [OK] !openssl_version!
)

REM Результат
echo.
echo ====================================
if %requirements_ok% equ 1 (
    echo Все требования установлены! [OK]
    echo Вы можете запустить программу:
    echo   - Графическая версия: run.bat
    echo   - Командная версия: cli.bat app.apk
) else (
    echo Требуется установить отсутствующие компоненты!
    echo Пожалуйста, установите все необходимые программы перед использованием APK Signer.
)
echo ====================================
echo.

pause
