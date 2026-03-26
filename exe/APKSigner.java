import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class APKSigner extends JFrame {
    private JTextField appPathField;  // Для APK или AAB
    private JTextField keystorePathField;
    private JTextField outputDirField;
    private JTextArea logArea;
    private JButton generateButton;
    private JButton clearButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JComboBox formatCombo;  // Выбор формата APK/AAB
    private JLabel formatLabel;
    private JLabel fileStatusLabel;

    private static final String KEYSTORE_PASSWORD = "android_key_password";
    private static final String KEY_ALIAS = "release_key";
    private static final String KEY_PASSWORD = "android_key_password";

    public APKSigner() {
        setTitle("APK Signer для Play Market и RuStore");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок
        JLabel titleLabel = new JLabel("Генератор подписей APK/AAB", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Центральная часть
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 5, 5));

        // Выбор формата
        JPanel formatPanel = new JPanel(new BorderLayout(5, 5));
        formatPanel.setBorder(BorderFactory.createTitledBorder("Выбор формата"));
        formatCombo = new JComboBox(new Object[] {"APK", "AAB"});
        formatCombo.addActionListener(e -> updateFileFilter());
        formatPanel.add(formatCombo, BorderLayout.WEST);
        fileStatusLabel = new JLabel("APK - стандартный формат");
        fileStatusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formatPanel.add(fileStatusLabel, BorderLayout.CENTER);
        centerPanel.add(formatPanel);

        // Выбор файла (APK или AAB)
        centerPanel.add(createFilePanel("Выбор файла приложения", appPathField = new JTextField(40), () -> selectAppFile()));

        // Выбор Keystore
        centerPanel.add(createFilePanel("Keystore файл (опционально)", keystorePathField = new JTextField(40), () -> selectKeystore()));

        // Выбор папки вывода
        centerPanel.add(createFilePanel("Папка для сохранения файлов", outputDirField = new JTextField(40), () -> selectOutputDir()));

        // Логирование и прогресс
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Информация"));

        logArea = new JTextArea(10, 70);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        statusLabel = new JLabel("Готово");
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(statusLabel, BorderLayout.EAST);
        logPanel.add(statusPanel, BorderLayout.SOUTH);

        centerPanel.add(logPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Кнопки действий
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        generateButton = new JButton("Генерировать ключи и подписи");
        generateButton.addActionListener(e -> generateSignatures());
        buttonPanel.add(generateButton);

        clearButton = new JButton("Очистить");
        clearButton.addActionListener(e -> clearFields());
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createFilePanel(String label, JTextField textField, Runnable browseAction) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBorder(BorderFactory.createTitledBorder(label));

        textField.setEditable(false);
        panel.add(textField, BorderLayout.CENTER);

        JButton browseButton = new JButton("Обзор");
        browseButton.addActionListener(e -> browseAction.run());
        panel.add(browseButton, BorderLayout.EAST);

        return panel;
    }

    private void updateFileFilter() {
        String format = (String) formatCombo.getSelectedItem();
        if ("APK".equals(format)) {
            fileStatusLabel.setText("APK - стандартный формат для установки");
        } else if ("AAB".equals(format)) {
            fileStatusLabel.setText("AAB - современный формат для Google Play Store");
        }
    }

    private void selectAppFile() {
        JFileChooser chooser = new JFileChooser();
        String format = (String) formatCombo.getSelectedItem();
        
        if ("APK".equals(format)) {
            chooser.setFileFilter(new FileNameExtensionFilter("APK файлы", "apk"));
        } else if ("AAB".equals(format)) {
            chooser.setFileFilter(new FileNameExtensionFilter("AAB файлы", "aab"));
        }
        
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            appPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            analyzeAppFile(chooser.getSelectedFile().getAbsolutePath(), format);
        }
    }

    private void selectAPK() {
        selectAppFile();
    }

    private void selectKeystore() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Keystore файлы", "jks", "keystore"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            keystorePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void selectOutputDir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputDirField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void analyzeAppFile(String filePath, String format) {
        logMessage("Анализ " + format + " файла...");
        try {
            File appFile = new File(filePath);
            if (appFile.exists()) {
                double sizeInMB = appFile.length() / (1024.0 * 1024.0);
                logMessage("✓ Файл загружен успешно");
                logMessage("  Тип: " + format);
                logMessage("  Размер: " + String.format("%.2f", sizeInMB) + " MB");
            }
        } catch (Exception e) {
            logMessage("✗ Ошибка при анализе: " + e.getMessage());
        }
    }

    private void analyzeAPK(String apkPath) {
        analyzeAppFile(apkPath, "APK");
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void clearSigningFields() {
        appPathField.setText("");
        keystorePathField.setText("");
        outputDirField.setText("");
        logArea.setText("");
        statusLabel.setText("Готово");
        progressBar.setValue(0);
    }

    private void clearFields() {
        clearSigningFields();
    }

    private void generateSignatures() {
        if (appPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите файл приложения (APK или AAB)", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String outputDirStr = outputDirField.getText();
        if (outputDirStr.isEmpty()) {
            outputDirStr = new File(appPathField.getText()).getParent();
        }
        
        final String outputDir = outputDirStr;
        final String appPath = appPathField.getText();
        final String format = (String) formatCombo.getSelectedItem();

        new Thread(() -> {
            try {
                progressBar.setValue(0);
                statusLabel.setText("Обработка...");
                generateButton.setEnabled(false);

                logMessage("\n" + "=".repeat(50));
                logMessage("Начало генерации подписей для " + format + "...");
                logMessage("=".repeat(50));

                File outputDirFile = new File(outputDir);
                outputDirFile.mkdirs();

                String keystorePath = keystorePathField.getText().isEmpty()
                        ? new File(outputDir, "app-release-key.jks").getAbsolutePath()
                        : keystorePathField.getText();

                progressBar.setValue(10);

                // Создать или использовать keystore
                if (!new File(keystorePath).exists()) {
                    logMessage("\n1. Создание нового Keystore...");
                    createKeystore(keystorePath);
                } else {
                    logMessage("\n1. Используется существующий keystore");
                }

                progressBar.setValue(30);

                // Подписать файл (APK или AAB)
                if ("APK".equals(format)) {
                    logMessage("\n2. Подпись APK файла...");
                    signAPK(appPath, keystorePath, new File(outputDir, "app-signed.apk").getAbsolutePath());
                } else {
                    logMessage("\n2. Подпись AAB файла...");
                    signAAB(appPath, keystorePath, new File(outputDir, "app-signed.aab").getAbsolutePath());
                }

                progressBar.setValue(50);

                // Экспортировать сертификат
                logMessage("\n3. Экспорт сертификата в PEM...");
                exportCertificate(keystorePath, outputDir);

                progressBar.setValue(70);

                // Генерировать подписи для Play Market
                logMessage("\n4. Генерация подписи для Google Play Market...");
                generatePlayMarketSignature(keystorePath, outputDir, format);

                progressBar.setValue(85);

                // Генерировать подписи для RuStore (только для APK)
                if ("APK".equals(format)) {
                    logMessage("\n5. Генерация подписи для RuStore...");
                    generateRuStoreSignature(keystorePath, outputDir);
                } else {
                    logMessage("\n5. AAB используется только для Google Play Store (RuStore использует APK)");
                }

                progressBar.setValue(95);

                logMessage("\n" + "=".repeat(50));
                logMessage("✓ ВСЕ ОПЕРАЦИИ ЗАВЕРШЕНЫ УСПЕШНО!");
                logMessage("=".repeat(50));
                logMessage("\nФайлы сохранены в: " + outputDir);

                progressBar.setValue(100);
                statusLabel.setText("Завершено!");
                JOptionPane.showMessageDialog(this, "Подписи успешно созданы!\n\nПапка: " + outputDir, "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                logMessage("\n✗ ОШИБКА: " + e.getMessage());
                statusLabel.setText("Ошибка!");
                JOptionPane.showMessageDialog(this, "Ошибка при генерации: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                generateButton.setEnabled(true);
            }
        }).start();
    }

    private void createKeystore(String keystorePath) throws Exception {
        logMessage("  Создание keystore с помощью keytool...");

        ProcessBuilder pb = new ProcessBuilder(
                "keytool",
                "-genkey",
                "-v",
                "-keystore", keystorePath,
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-validity", "10000",
                "-alias", KEY_ALIAS,
                "-storepass", KEYSTORE_PASSWORD,
                "-keypass", KEY_PASSWORD,
                "-dname", "CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown"
        );

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            logMessage("✓ Keystore создан: " + keystorePath);
            logMessage("  Пароль keystore: " + KEYSTORE_PASSWORD);
            logMessage("  Пароль ключа: " + KEY_PASSWORD);
            logMessage("  Alias ключа: " + KEY_ALIAS);
        } else {
            throw new Exception("Ошибка при создании keystore!");
        }
    }

    private void signAPK(String apkPath, String keystorePath, String outputPath) throws Exception {
        logMessage("  Подпись APK файла (jarsigner)...");
        logMessage("  Файлы:");
        logMessage("    APK: " + apkPath);
        logMessage("    Keystore: " + keystorePath);
        logMessage("    Выход: " + outputPath);

        ProcessBuilder pb = new ProcessBuilder(
                "jarsigner",
                "-verbose",
                "-sigalg", "SHA1withRSA",
                "-digestalg", "SHA1",
                "-keystore", keystorePath,
                "-storepass", KEYSTORE_PASSWORD,
                "-keypass", KEY_PASSWORD,
                "-signedjar", outputPath,
                apkPath,
                KEY_ALIAS
        );

        Process process = pb.start();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder errorOutput = new StringBuilder();
        StringBuilder output = new StringBuilder();
        String line;
        
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }
        while ((line = inputReader.readLine()) != null) {
            output.append(line).append("\n");
        }
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            File signedAPK = new File(outputPath);
            double sizeInMB = signedAPK.length() / (1024.0 * 1024.0);
            logMessage("✓ APK подписан успешно: " + outputPath);
            logMessage("  Размер: " + String.format("%.2f", sizeInMB) + " MB");
        } else {
            String fullError = output.toString() + errorOutput.toString();
            logMessage("DEBUG: Выход jarsigner:\n" + fullError);
            throw new Exception("Ошибка при подписи APK (exit code: " + exitCode + ")\n" + fullError);
        }
    }

    private void signAAB(String aabPath, String keystorePath, String outputPath) throws Exception {
        logMessage("  Подпись AAB файла (jarsigner)...");
        logMessage("  Файлы:");
        logMessage("    AAB: " + aabPath);
        logMessage("    Keystore: " + keystorePath);
        logMessage("    Выход: " + outputPath);

        ProcessBuilder pb = new ProcessBuilder(
                "jarsigner",
                "-verbose",
                "-sigalg", "SHA1withRSA",
                "-digestalg", "SHA1",
                "-keystore", keystorePath,
                "-storepass", KEYSTORE_PASSWORD,
                "-keypass", KEY_PASSWORD,
                "-signedjar", outputPath,
                aabPath,
                KEY_ALIAS
        );

        Process process = pb.start();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder errorOutput = new StringBuilder();
        StringBuilder output = new StringBuilder();
        String line;
        
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }
        while ((line = inputReader.readLine()) != null) {
            output.append(line).append("\n");
        }
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            File signedAAB = new File(outputPath);
            double sizeInMB = signedAAB.length() / (1024.0 * 1024.0);
            logMessage("✓ AAB подписан успешно: " + outputPath);
            logMessage("  Размер: " + String.format("%.2f", sizeInMB) + " MB");
            logMessage("  Примечание: AAB подходит для Google Play Store");
        } else {
            String fullError = output.toString() + errorOutput.toString();
            logMessage("DEBUG: Выход jarsigner:\n" + fullError);
            throw new Exception("Ошибка при подписи AAB (exit code: " + exitCode + ")\n" + fullError);
        }
    }

    private void exportCertificate(String keystorePath, String outputDir) throws Exception {
        logMessage("  Экспорт сертификата...");

        String certPath = new File(outputDir, "certificate.pem").getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(
                "keytool",
                "-export",
                "-alias", KEY_ALIAS,
                "-keystore", keystorePath,
                "-storepass", KEYSTORE_PASSWORD,
                "-rfc"
        );

        ProcessBuilder pbOutput = new ProcessBuilder(
                "cmd.exe", "/C",
                "keytool -export -alias " + KEY_ALIAS + " -keystore " + keystorePath + 
                " -storepass " + KEYSTORE_PASSWORD + " -rfc > " + certPath
        );

        Process process = pbOutput.start();
        int exitCode = process.waitFor();

        if (exitCode == 0 && new File(certPath).exists()) {
            byte[] certData = Files.readAllBytes(Paths.get(certPath));
            String sha256 = calculateSHA256(certData);
            String sha1 = calculateSHA1(certData);

            logMessage("✓ Сертификат экспортирован: " + certPath);
            logMessage("  SHA256: " + sha256);
            logMessage("  SHA1: " + sha1);
        } else {
            logMessage("⚠ Не удалось экспортировать сертификат");
        }
    }

    private void generatePlayMarketSignature(String keystorePath, String outputDir, String format) throws Exception {
        logMessage("✓ Подпись Play Market готова");

        File infoFile = new File(outputDir, "playmarket_info.txt");
        StringBuilder content = new StringBuilder();
        content.append("Google Play Market - Информация о подписи\n");
        content.append("=".repeat(50)).append("\n\n");
        content.append("Формат: ").append(format).append("\n");
        content.append("Сертификат: certificate.pem\n");
        
        if ("APK".equals(format)) {
            content.append("Приложение: app-signed.apk\n\n");
            content.append("Инструкции для загрузки:\n");
            content.append("1. Откройте Google Play Console\n");
            content.append("2. Создайте новое приложение\n");
            content.append("3. Загрузите app-signed.apk\n");
            content.append("4. Система автоматически извлечет SHA256\n");
        } else {
            content.append("Приложение: app-signed.aab\n\n");
            content.append("Инструкции для загрузки:\n");
            content.append("1. Откройте Google Play Console\n");
            content.append("2. Создайте новое приложение\n");
            content.append("3. Перейдите в Release → Production\n");
            content.append("4. Загрузите app-signed.aab\n");
            content.append("5. Google Play автоматически создаст APK для каждого устройства\n");
            content.append("6. Система автоматически извлечет SHA256\n\n");
            content.append("Преимущества AAB:\n");
            content.append("- Меньший размер загрузки для пользователя\n");
            content.append("- Оптимизация для каждого устройства\n");
            content.append("- Автоматическое создание APK\n");
        }

        Files.write(infoFile.toPath(), content.toString().getBytes());
        logMessage("  Файл информации: " + infoFile.getAbsolutePath());
    }

    private void generatePlayMarketSignature(String keystorePath, String outputDir) throws Exception {
        generatePlayMarketSignature(keystorePath, outputDir, "APK");
    }

    private void generateRuStoreSignature(String keystorePath, String outputDir) throws Exception {
        logMessage("✓ Подпись RuStore готова");

        File infoFile = new File(outputDir, "rustore_info.txt");
        StringBuilder content = new StringBuilder();
        content.append("RuStore - Информация о подписи\n");
        content.append("=".repeat(50)).append("\n\n");
        content.append("Сертификат: certificate.pem\n");
        content.append("Приложение: app-signed.apk\n\n");
        content.append("Инструкции для загрузки:\n");
        content.append("1. Откройте RuStore Developer Console\n");
        content.append("2. Создайте новое приложение\n");
        content.append("3. Загрузите app-signed.apk\n");
        content.append("4. Загрузите certificate.pem\n");
        content.append("5. Проверьте совпадение SHA256\n");

        Files.write(infoFile.toPath(), content.toString().getBytes());
        logMessage("  Файл информации: " + infoFile.getAbsolutePath());
    }

    private String calculateSHA256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String calculateSHA1(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new APKSigner());
    }
}
