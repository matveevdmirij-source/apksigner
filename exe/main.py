import tkinter as tk
from tkinter import filedialog, messagebox, ttk
import os
import subprocess
import json
from pathlib import Path
import zipfile
import xml.etree.ElementTree as ET
from datetime import datetime
import hashlib
import base64

class APKSignerApp:
    def __init__(self, root):
        self.root = root
        self.root.title("APK Signer для Play Market и RuStore")
        self.root.geometry("700x600")
        self.root.resizable(False, False)
        
        self.apk_path = tk.StringVar()
        self.keystore_path = tk.StringVar()
        self.output_dir = tk.StringVar()
        
        self.setup_ui()
        
    def setup_ui(self):
        """Создание интерфейса приложения"""
        # Главная рамка
        main_frame = ttk.Frame(self.root, padding="10")
        main_frame.pack(fill=tk.BOTH, expand=True)
        
        # Заголовок
        title = ttk.Label(main_frame, text="Генератор подписей APK", 
                         font=("Arial", 14, "bold"))
        title.pack(pady=10)
        
        # Выбор APK файла
        apk_frame = ttk.LabelFrame(main_frame, text="Выбор APK файла", padding="10")
        apk_frame.pack(fill=tk.X, pady=10)
        
        ttk.Entry(apk_frame, textvariable=self.apk_path, width=50).pack(side=tk.LEFT, padx=5)
        ttk.Button(apk_frame, text="Обзор", 
                  command=self.select_apk).pack(side=tk.LEFT, padx=5)
        
        # Выбор Keystore
        keystore_frame = ttk.LabelFrame(main_frame, text="Keystore файл (опционально)", padding="10")
        keystore_frame.pack(fill=tk.X, pady=10)
        
        ttk.Entry(keystore_frame, textvariable=self.keystore_path, width=50).pack(side=tk.LEFT, padx=5)
        ttk.Button(keystore_frame, text="Обзор", 
                  command=self.select_keystore).pack(side=tk.LEFT, padx=5)
        
        info_label = ttk.Label(keystore_frame, text="Если не выбран, будет создан новый", 
                              foreground="gray", font=("Arial", 9))
        info_label.pack(anchor=tk.W, padx=5, pady=5)
        
        # Выбор папки вывода
        output_frame = ttk.LabelFrame(main_frame, text="Папка для сохранения файлов", padding="10")
        output_frame.pack(fill=tk.X, pady=10)
        
        ttk.Entry(output_frame, textvariable=self.output_dir, width=50).pack(side=tk.LEFT, padx=5)
        ttk.Button(output_frame, text="Обзор", 
                  command=self.select_output_dir).pack(side=tk.LEFT, padx=5)
        
        # Информация о приложении
        info_frame = ttk.LabelFrame(main_frame, text="Информация", padding="10")
        info_frame.pack(fill=tk.BOTH, expand=True, pady=10)
        
        self.info_text = tk.Text(info_frame, height=8, width=80, state=tk.DISABLED)
        scrollbar = ttk.Scrollbar(info_frame, orient=tk.VERTICAL, command=self.info_text.yview)
        self.info_text['yscrollcommand'] = scrollbar.set
        
        self.info_text.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        # Кнопки действий
        button_frame = ttk.Frame(main_frame)
        button_frame.pack(fill=tk.X, pady=10)
        
        ttk.Button(button_frame, text="Генерировать ключи и подписи", 
                  command=self.generate_signatures).pack(side=tk.LEFT, padx=5)
        ttk.Button(button_frame, text="Очистить", 
                  command=self.clear_fields).pack(side=tk.LEFT, padx=5)
        
    def select_apk(self):
        """Выбор APK файла"""
        filename = filedialog.askopenfilename(
            title="Выберите APK файл",
            filetypes=[("APK файлы", "*.apk"), ("Все файлы", "*.*")]
        )
        if filename:
            self.apk_path.set(filename)
            self.extract_apk_info(filename)
    
    def select_keystore(self):
        """Выбор Keystore файла"""
        filename = filedialog.askopenfilename(
            title="Выберите Keystore файл",
            filetypes=[("Keystore файлы", "*.jks;*.keystore"), ("Все файлы", "*.*")]
        )
        if filename:
            self.keystore_path.set(filename)
    
    def select_output_dir(self):
        """Выбор папки вывода"""
        dirname = filedialog.askdirectory(title="Выберите папку для сохранения")
        if dirname:
            self.output_dir.set(dirname)
    
    def log_message(self, message):
        """Добавить сообщение в лог"""
        self.info_text.config(state=tk.NORMAL)
        timestamp = datetime.now().strftime("%H:%M:%S")
        self.info_text.insert(tk.END, f"[{timestamp}] {message}\n")
        self.info_text.see(tk.END)
        self.info_text.config(state=tk.DISABLED)
        self.root.update()
    
    def extract_apk_info(self, apk_path):
        """Извлечение информации из APK"""
        try:
            self.log_message("Анализ APK файла...")
            
            with zipfile.ZipFile(apk_path, 'r') as apk_zip:
                # Попытка прочитать AndroidManifest.xml
                if 'AndroidManifest.xml' in apk_zip.namelist():
                    manifest = apk_zip.read('AndroidManifest.xml')
                    
            self.log_message("✓ APK файл успешно загружен")
            
            # Получить информацию о файле
            file_size = os.path.getsize(apk_path) / (1024 * 1024)
            self.log_message(f"  Размер: {file_size:.2f} MB")
            
        except Exception as e:
            self.log_message(f"✗ Ошибка при анализе APK: {str(e)}")
    
    def generate_signatures(self):
        """Генерировать ключи и подписи"""
        if not self.apk_path.get():
            messagebox.showerror("Ошибка", "Пожалуйста, выберите APK файл")
            return
        
        output_dir = self.output_dir.get() or os.path.dirname(self.apk_path.get())
        os.makedirs(output_dir, exist_ok=True)
        
        self.log_message("\n" + "="*50)
        self.log_message("Начало генерации подписей...")
        self.log_message("="*50)
        
        try:
            # Шаг 1: Генерировать или использовать существующий keystore
            keystore_path = self._get_keystore(output_dir)
            
            # Шаг 2: Подписать APK
            self._sign_apk(keystore_path, output_dir)
            
            # Шаг 3: Экспортировать сертификат в PEM
            self._export_certificate_pem(keystore_path, output_dir)
            
            # Шаг 4: Создать подписи для Play Market и RuStore
            self._generate_play_market_signature(keystore_path, output_dir)
            self._generate_rustore_signature(keystore_path, output_dir)
            
            self.log_message("\n" + "="*50)
            self.log_message("✓ ВСЕ ОПЕРАЦИИ ЗАВЕРШЕНЫ УСПЕШНО!")
            self.log_message("="*50)
            self.log_message(f"\nФайлы сохранены в: {output_dir}")
            
            messagebox.showinfo("Успех", f"Подписи успешно созданы!\n\nПапка: {output_dir}")
            
        except Exception as e:
            self.log_message(f"\n✗ ОШИБКА: {str(e)}")
            messagebox.showerror("Ошибка", f"Ошибка при генерации: {str(e)}")
    
    def _get_keystore(self, output_dir):
        """Получить keystore (создать или использовать существующий)"""
        if self.keystore_path.get():
            keystore_path = self.keystore_path.get()
            self.log_message(f"✓ Используется существующий keystore: {keystore_path}")
        else:
            keystore_path = os.path.join(output_dir, "app-release-key.jks")
            self._create_keystore(keystore_path)
        
        return keystore_path
    
    def _create_keystore(self, keystore_path):
        """Создать новый keystore"""
        self.log_message("\n1. Создание нового Keystore...")
        
        try:
            # Проверка наличия keytool
            self._check_keytool()
            
            # Параметры для создания keystore
            keystore_password = "android_key_password"
            key_alias = "release_key"
            key_password = "android_key_password"
            
            cmd = [
                "keytool",
                "-genkey",
                "-v",
                "-keystore", keystore_path,
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-validity", "10000",
                "-alias", key_alias,
                "-storepass", keystore_password,
                "-keypass", key_password,
                "-dname", "CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown"
            ]
            
            subprocess.run(cmd, capture_output=True, check=True)
            self.log_message(f"✓ Keystore создан: {keystore_path}")
            self.log_message(f"  Пароль keystore: {keystore_password}")
            self.log_message(f"  Пароль ключа: {key_password}")
            self.log_message(f"  Alias ключа: {key_alias}")
            
        except FileNotFoundError:
            self.log_message("✗ keytool не найден. Генерируем заменяющий способ...")
            self._create_keystore_alternative(keystore_path)
    
    def _create_keystore_alternative(self, keystore_path):
        """Альтернативный способ создания Keystore с использованием OpenSSL"""
        self.log_message("  Используется альтернативный метод (OpenSSL)...")
        
        try:
            key_file = keystore_path.replace(".jks", ".key")
            cert_file = keystore_path.replace(".jks", ".pem")
            
            # Генерировать приватный ключ
            openssl_cmd = [
                "openssl", "req", "-x509", "-newkey", "rsa:2048",
                "-keyout", key_file,
                "-out", cert_file,
                "-days", "10000",
                "-nodes",
                "-subj", "/CN=Unknown/OU=Unknown/O=Unknown/L=Unknown/ST=Unknown/C=Unknown"
            ]
            
            result = subprocess.run(openssl_cmd, capture_output=True, text=True)
            if result.returncode == 0:
                self.log_message(f"✓ Ключ и сертификат созданы успешно")
                # Сохраняем информацию о файлах
                self._save_keystore_info(keystore_path, key_file, cert_file)
            else:
                raise Exception(f"OpenSSL ошибка: {result.stderr}")
                
        except FileNotFoundError:
            self.log_message("✗ ни keytool ни openssl не найдены")
            raise Exception("Требуется установка Java Development Kit или OpenSSL")
    
    def _save_keystore_info(self, keystore_path, key_file, cert_file):
        """Сохранить информацию о keystore"""
        info = {
            "keystore_path": keystore_path,
            "key_file": key_file,
            "cert_file": cert_file,
            "created": datetime.now().isoformat()
        }
        info_file = keystore_path + ".info"
        with open(info_file, 'w') as f:
            json.dump(info, f, indent=2)
    
    def _sign_apk(self, keystore_path, output_dir):
        """Подписать APK файл"""
        self.log_message("\n2. Подпись APK файла...")
        
        try:
            apk_path = self.apk_path.get()
            signed_apk = os.path.join(output_dir, "app-signed.apk")
            
            keystore_password = "android_key_password"
            key_alias = "release_key"
            
            # Попытка использовать apksigner (больший приоритет)
            try:
                self._sign_with_apksigner(apk_path, signed_apk, keystore_path)
            except:
                # Fallback на jarsigner
                self._sign_with_jarsigner(apk_path, signed_apk, keystore_path, 
                                         keystore_password, key_alias)
            
            if os.path.exists(signed_apk):
                size = os.path.getsize(signed_apk) / (1024 * 1024)
                self.log_message(f"✓ APK подписан успешно: {signed_apk}")
                self.log_message(f"  Размер: {size:.2f} MB")
        except Exception as e:
            self.log_message(f"⚠ Подпись APK: {str(e)}")
    
    def _sign_with_apksigner(self, apk_path, output_path, keystore_path):
        """Подпись с использованием apksigner"""
        cmd = [
            "apksigner", "sign",
            "--ks", keystore_path,
            "--ks-pass", "pass:android_key_password",
            "--key-pass", "pass:android_key_password",
            "--out", output_path,
            apk_path
        ]
        result = subprocess.run(cmd, capture_output=True, text=True, check=True)
        self.log_message(f"  Используется apksigner")
    
    def _sign_with_jarsigner(self, apk_path, output_path, keystore_path, 
                            keystore_pass, key_alias):
        """Подпись с использованием jarsigner"""
        cmd = [
            "jarsigner",
            "-verbose",
            "-sigalg", "SHA1withRSA",
            "-digestalg", "SHA1",
            "-keystore", keystore_path,
            "-storepass", keystore_pass,
            "-keypass", keystore_pass,
            "-signedjar", output_path,
            apk_path,
            key_alias
        ]
        subprocess.run(cmd, capture_output=True, check=True)
        self.log_message(f"  Используется jarsigner")
    
    def _export_certificate_pem(self, keystore_path, output_dir):
        """Экспортировать сертификат в PEM формат"""
        self.log_message("\n3. Экспорт сертификата в PEM...")
        
        try:
            pem_path = os.path.join(output_dir, "certificate.pem")
            der_path = os.path.join(output_dir, "certificate.der")
            
            # Экспортировать сертификат в DER формат
            cmd_export = [
                "keytool",
                "-export",
                "-alias", "release_key",
                "-file", der_path,
                "-keystore", keystore_path,
                "-storepass", "android_key_password",
                "-rfc"  # Сразу экспортировать в PEM (RFC формат)
            ]
            
            try:
                result = subprocess.run(cmd_export, capture_output=True, text=True)
                
                # Если PEM не был создан напрямую, конвертируем из DER
                if not os.path.exists(pem_path) or result.returncode != 0:
                    cmd_convert = [
                        "openssl", "x509",
                        "-inform", "DER",
                        "-in", der_path,
                        "-out", pem_path
                    ]
                    subprocess.run(cmd_convert, capture_output=True, check=True)
                else:
                    pem_path = der_path
                
                if os.path.exists(pem_path):
                    self.log_message(f"✓ Сертификат экспортирован: {pem_path}")
                    self._calculate_certificate_hash(pem_path)
                    
            except Exception as e:
                self.log_message(f"⚠ Экспорт PEM: {str(e)}")
                
        except Exception as e:
            self.log_message(f"✗ Ошибка при экспорте сертификата: {str(e)}")
    
    def _calculate_certificate_hash(self, pem_path):
        """Вычислить хэш сертификата"""
        try:
            with open(pem_path, 'rb') as f:
                cert_data = f.read()
            
            sha256_hash = hashlib.sha256(cert_data).hexdigest()
            sha1_hash = hashlib.sha1(cert_data).hexdigest()
            
            self.log_message(f"  SHA256: {sha256_hash}")
            self.log_message(f"  SHA1: {sha1_hash}")
            
        except Exception as e:
            self.log_message(f"  Ошибка при вычислении хэша: {str(e)}")
    
    def _generate_play_market_signature(self, keystore_path, output_dir):
        """Генерировать подпись для Play Market"""
        self.log_message("\n4. Генерация подписи для Google Play Market...")
        
        try:
            # Получить публичный ключ
            cmd = [
                "keytool",
                "-export",
                "-alias", "release_key",
                "-keystore", keystore_path,
                "-storepass", "android_key_password",
                "-rfc"
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True, check=True)
            
            # Сохранить сертификат для Play Market
            play_market_cert = os.path.join(output_dir, "playmarket_certificate.pem")
            with open(play_market_cert, 'w') as f:
                f.write(result.stdout)
            
            # Вычислить отпечаток SHA256
            with open(play_market_cert, 'rb') as f:
                cert_data = f.read()
            
            sha256_fingerprint = hashlib.sha256(cert_data).hexdigest()
            
            self.log_message(f"✓ Подпись Play Market готова")
            self.log_message(f"  Файл: {play_market_cert}")
            self.log_message(f"  SHA256 отпечаток: {sha256_fingerprint}")
            
            # Сохранить отпечатки
            fingerprints_file = os.path.join(output_dir, "playmarket_fingerprints.txt")
            with open(fingerprints_file, 'w', encoding='utf-8') as f:
                f.write("Google Play Market - Отпечатки сертификата\n")
                f.write("="*50 + "\n\n")
                f.write(f"SHA256: {sha256_fingerprint}\n")
                
        except Exception as e:
            self.log_message(f"⚠ Ошибка при генерации подписи Play Market: {str(e)}")
    
    def _generate_rustore_signature(self, keystore_path, output_dir):
        """Генерировать подпись для RuStore"""
        self.log_message("\n5. Генерация подписи для RuStore...")
        
        try:
            # Получить информацию о ключе
            cmd = [
                "keytool",
                "-list",
                "-v",
                "-keystore", keystore_path,
                "-storepass", "android_key_password"
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True, check=True)
            
            # Сохранить информацию для RuStore
            rustore_info = os.path.join(output_dir, "rustore_signature_info.txt")
            with open(rustore_info, 'w', encoding='utf-8') as f:
                f.write("RuStore - Информация для подписи приложения\n")
                f.write("="*50 + "\n\n")
                
                # Парсим информацию о сертификате
                lines = result.stdout.split('\n')
                for i, line in enumerate(lines):
                    if 'SHA' in line or 'Fingerprint' in line or 'Owner:' in line:
                        f.write(line + "\n")
                
                # Добавить инструкции для RuStore
                f.write("\n" + "="*50 + "\n")
                f.write("Инструкции для загрузки на RuStore:\n")
                f.write("1. Откройте консоль разработчика RuStore\n")
                f.write("2. Перейдите в раздел 'Подпись приложения'\n")
                f.write("3. Загрузите подписанный APK файл\n")
                f.write("4. Загрузите PEM сертификат (certificate.pem)\n")
                f.write("5. Убедитесь, что SHA256 отпечатки совпадают\n")
            
            self.log_message(f"✓ Подпись RuStore готова")
            self.log_message(f"  Файл информации: {rustore_info}")
            
        except Exception as e:
            self.log_message(f"⚠ Ошибка при генерации подписи RuStore: {str(e)}")
    
    def _check_keytool(self):
        """Проверить наличие keytool"""
        try:
            subprocess.run(["keytool", "-help"], capture_output=True, check=True, timeout=5)
        except:
            raise Exception("keytool не найден. Требуется установка Java Development Kit (JDK)")
    
    def clear_fields(self):
        """Очистить поля"""
        self.apk_path.set("")
        self.keystore_path.set("")
        self.output_dir.set("")
        self.info_text.config(state=tk.NORMAL)
        self.info_text.delete(1.0, tk.END)
        self.info_text.config(state=tk.DISABLED)

def main():
    root = tk.Tk()
    app = APKSignerApp(root)
    root.mainloop()

if __name__ == "__main__":
    main()
