#!/usr/bin/env python3
"""
APK Signer CLI - Версия с интерфейсом командной строки
для генерации подписей APK для Play Market и RuStore
"""

import os
import sys
import argparse
import subprocess
import json
from pathlib import Path
from datetime import datetime
import hashlib
import zipfile

class APKSignerCLI:
    def __init__(self):
        self.keystore_password = "android_key_password"
        self.key_alias = "release_key"
        self.key_password = "android_key_password"
    
    def log(self, message, level="INFO"):
        """Вывести сообщение с временной меткой"""
        timestamp = datetime.now().strftime("%H:%M:%S")
        prefix = f"[{timestamp}] {level:8s}"
        print(f"{prefix} {message}")
    
    def log_success(self, message):
        """Вывести успешное сообщение"""
        self.log(message, "✓ OK")
    
    def log_error(self, message):
        """Вывести сообщение об ошибке"""
        self.log(message, "✗ ERROR")
    
    def log_warning(self, message):
        """Вывести предупреждение"""
        self.log(message, "⚠ WARN")
    
    def log_info(self, message):
        """Вывести информационное сообщение"""
        self.log(message, "ℹ INFO")
    
    def check_requirements(self):
        """Проверить наличие требуемых инструментов"""
        self.log("\n" + "="*60)
        self.log("Проверка требований", "CHECK")
        self.log("="*60)
        
        # Проверить Java
        try:
            subprocess.run(["keytool", "-help"], capture_output=True, check=True, timeout=5)
            self.log_success("Java Development Kit найден")
        except FileNotFoundError:
            self.log_error("keytool не найден (требуется JDK)")
            return False
        except Exception as e:
            self.log_error(f"Ошибка при проверке JDK: {e}")
            return False
        
        return True
    
    def verify_apk(self, apk_path):
        """Проверить корректность APK файла"""
        self.log(f"\nПроверка APK: {apk_path}")
        
        if not os.path.exists(apk_path):
            self.log_error(f"Файл не найден: {apk_path}")
            return False
        
        try:
            with zipfile.ZipFile(apk_path, 'r') as apk_zip:
                if 'AndroidManifest.xml' not in apk_zip.namelist():
                    self.log_error("Это не APK файл (нет AndroidManifest.xml)")
                    return False
            
            file_size = os.path.getsize(apk_path) / (1024 * 1024)
            self.log_success(f"APK файл корректен ({file_size:.2f} MB)")
            return True
            
        except Exception as e:
            self.log_error(f"Ошибка при проверке APK: {e}")
            return False
    
    def create_keystore(self, keystore_path):
        """Создать новый Keystore"""
        self.log(f"\nСоздание Keystore: {keystore_path}")
        
        try:
            cmd = [
                "keytool",
                "-genkey",
                "-v",
                "-keystore", keystore_path,
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-validity", "10000",
                "-alias", self.key_alias,
                "-storepass", self.keystore_password,
                "-keypass", self.key_password,
                "-dname", "CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown"
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True, check=True)
            self.log_success(f"Keystore создан успешно")
            self.log_info(f"  Пароль keystore: {self.keystore_password}")
            self.log_info(f"  Пароль ключа: {self.key_password}")
            self.log_info(f"  Alias: {self.key_alias}")
            return True
            
        except Exception as e:
            self.log_error(f"Ошибка при создании Keystore: {e}")
            return False
    
    def sign_apk(self, apk_path, keystore_path, output_path):
        """Подписать APK файл"""
        self.log(f"\nПодпись APK файла...")
        
        try:
            # Попытка использовать apksigner
            try:
                cmd = [
                    "apksigner", "sign",
                    "--ks", keystore_path,
                    "--ks-pass", f"pass:{self.keystore_password}",
                    "--key-pass", f"pass:{self.key_password}",
                    "--out", output_path,
                    apk_path
                ]
                subprocess.run(cmd, capture_output=True, check=True)
                self.log_success(f"APK подписан (apksigner)")
                return True
                
            except FileNotFoundError:
                # Fallback на jarsigner
                cmd = [
                    "jarsigner",
                    "-verbose",
                    "-sigalg", "SHA1withRSA",
                    "-digestalg", "SHA1",
                    "-keystore", keystore_path,
                    "-storepass", self.keystore_password,
                    "-keypass", self.key_password,
                    "-signedjar", output_path,
                    apk_path,
                    self.key_alias
                ]
                subprocess.run(cmd, capture_output=True, check=True)
                self.log_success(f"APK подписан (jarsigner)")
                return True
                
        except Exception as e:
            self.log_error(f"Ошибка при подписи APK: {e}")
            return False
    
    def export_certificate_pem(self, keystore_path, output_dir):
        """Экспортировать сертификат в PEM"""
        self.log(f"\nЭкспорт сертификата в PEM...")
        
        try:
            pem_path = os.path.join(output_dir, "certificate.pem")
            
            cmd = [
                "keytool",
                "-export",
                "-alias", self.key_alias,
                "-keystore", keystore_path,
                "-storepass", self.keystore_password,
                "-rfc"
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True, check=True)
            
            with open(pem_path, 'w') as f:
                f.write(result.stdout)
            
            # Вычислить хэши
            with open(pem_path, 'rb') as f:
                cert_data = f.read()
            
            sha256_hash = hashlib.sha256(cert_data).hexdigest()
            sha1_hash = hashlib.sha1(cert_data).hexdigest()
            
            self.log_success(f"Сертификат экспортирован")
            self.log_info(f"  Файл: {pem_path}")
            self.log_info(f"  SHA256: {sha256_hash}")
            self.log_info(f"  SHA1: {sha1_hash}")
            
            return pem_path, sha256_hash, sha1_hash
            
        except Exception as e:
            self.log_error(f"Ошибка при экспорте сертификата: {e}")
            return None, None, None
    
    def generate_signatures(self, apk_path, keystore_path, output_dir):
        """Генерировать все подписи"""
        self.log("\n" + "="*60)
        self.log("Генерация подписей APK", "GENERATE")
        self.log("="*60)
        
        # Проверить APK
        if not self.verify_apk(apk_path):
            return False
        
        # Создать keystore, если его нет
        if not os.path.exists(keystore_path):
            if not self.create_keystore(keystore_path):
                return False
        else:
            self.log_info(f"Используется существующий Keystore")
        
        # Подписать APK
        signed_apk = os.path.join(output_dir, "app-signed.apk")
        if not self.sign_apk(apk_path, keystore_path, signed_apk):
            return False
        
        # Экспортировать сертификат
        pem_path, sha256, sha1 = self.export_certificate_pem(keystore_path, output_dir)
        if not pem_path:
            return False
        
        # Сохранить информацию для Play Market
        self.log(f"\nПодготовка файлов для Play Market...")
        playmarket_info = os.path.join(output_dir, "playmarket_info.txt")
        with open(playmarket_info, 'w', encoding='utf-8') as f:
            f.write("Google Play Market - Информация о подписи\n")
            f.write("="*50 + "\n\n")
            f.write(f"Сертификат: certificate.pem\n")
            f.write(f"SHA256: {sha256}\n")
            f.write(f"SHA1: {sha1}\n")
            f.write(f"\nПриложение: app-signed.apk\n")
        self.log_success(f"Информация для Play Market сохранена")
        
        # Сохранить информацию для RuStore
        self.log(f"\nПодготовка файлов для RuStore...")
        rustore_info = os.path.join(output_dir, "rustore_info.txt")
        with open(rustore_info, 'w', encoding='utf-8') as f:
            f.write("RuStore - Информация о подписи\n")
            f.write("="*50 + "\n\n")
            f.write(f"Сертификат: certificate.pem\n")
            f.write(f"SHA256: {sha256}\n")
            f.write(f"SHA1: {sha1}\n")
            f.write(f"\nПриложение: app-signed.apk\n")
        self.log_success(f"Информация для RuStore сохранена")
        
        return True
    
    def print_summary(self, output_dir, apk_path):
        """Вывести краткое резюме"""
        self.log("\n" + "="*60)
        self.log("РЕЗЮМЕ", "SUMMARY")
        self.log("="*60)
        
        files_to_check = [
            "app-signed.apk",
            "certificate.pem",
            "app-release-key.jks",
            "playmarket_info.txt",
            "rustore_info.txt"
        ]
        
        self.log("\nСгенерированные файлы:\n")
        for filename in files_to_check:
            filepath = os.path.join(output_dir, filename)
            if os.path.exists(filepath):
                size = os.path.getsize(filepath) / 1024
                self.log_success(f"  {filename} ({size:.1f} KB)")
            else:
                self.log_warning(f"  {filename} (не найден)")
        
        self.log(f"\nПапка с файлами: {output_dir}")
        self.log("\n" + "="*60)
        self.log("Генерация завершена успешно!", "✓ OK")
        self.log("="*60 + "\n")
    
    def run(self, args):
        """Главный метод запуска"""
        # Проверить требования
        if not self.check_requirements():
            return 1
        
        # Проверить входные параметры
        if not os.path.exists(args.apk):
            self.log_error(f"APK файл не найден: {args.apk}")
            return 1
        
        # Создать папку вывода
        output_dir = args.output or os.path.dirname(args.apk)
        os.makedirs(output_dir, exist_ok=True)
        
        # Определить пути к файлам
        keystore_path = args.keystore or os.path.join(output_dir, "app-release-key.jks")
        
        # Генерировать подписи
        if self.generate_signatures(args.apk, keystore_path, output_dir):
            self.print_summary(output_dir, args.apk)
            return 0
        else:
            self.log_error("\nОшибка при генерации подписей!")
            return 1

def main():
    parser = argparse.ArgumentParser(
        description='APK Signer для Play Market и RuStore',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
ПРИМЕРЫ:
  python cli.py app-release.apk
  python cli.py app-release.apk -o ./signed
  python cli.py app-release.apk -k my-keystore.jks -o ./signed
        """
    )
    
    parser.add_argument('apk', help='Путь к APK файлу')
    parser.add_argument('-k', '--keystore', help='Путь к файлу Keystore (создастся новый, если не указан)')
    parser.add_argument('-o', '--output', help='Папка для сохранения файлов (по умолчанию папка APK)')
    
    args = parser.parse_args()
    
    signer = APKSignerCLI()
    return signer.run(args)

if __name__ == "__main__":
    sys.exit(main())
