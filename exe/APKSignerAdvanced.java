import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class APKSignerAdvanced extends JFrame {
    private JTabbedPane tabbedPane;
    private JTextField apkPathField;
    private JTextField keystorePathField;
    private JTextField outputDirField;
    private JTextArea logArea;
    private JButton generateButton;
    private JButton clearButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JComboBox formatCombo;  // Для выбора APK/AAB

    // Вкладка перекомпиляции
    private JTextField projectPathField;
    private JButton rebuildButton;
    private JProgressBar rebuildProgressBar;
    private JLabel rebuildStatusLabel;
    private JTextArea rebuildLogArea;

    private static final String KEYSTORE_PASSWORD = "android_key_password";
    private static final String KEY_ALIAS = "release_key";
    private static final String KEY_PASSWORD = "android_key_password";

    public APKSignerAdvanced() {
        setTitle("APK Signer + Rebuilder для Play Market и RuStore");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        setResizable(true);

        setupUI();
        setVisible(true);
    }

    private void setupUI() {
        tabbedPane = new JTabbedPane();

        // Вкладка 1: Подпись APK
        tabbedPane.addTab("Подпись APK", createSigningTab());

        // Вкладка 2: Пересборка и подпись
        tabbedPane.addTab("Пересборка Release", createRebuildTab());

        add(tabbedPane);
    }

    // ========== ВКЛАДКА 1: Подпись APK/AAB ==========
    private JPanel createSigningTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок
        JLabel titleLabel = new JLabel("Генератор подписей APK/AAB", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Центральная часть
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 5, 5));

        // Выбор формата (новое!)
        JPanel formatPanel = new JPanel(new BorderLayout(5, 5));
        formatPanel.setBorder(BorderFactory.createTitledBorder("Выбор формата"));
        formatCombo = new JComboBox(new Object[] {"APK", "AAB"});
        JLabel fileStatusLabel = new JLabel("APK - стандартный формат");
        fileStatusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        formatCombo.addActionListener(e -> {
            String fmt = (String) formatCombo.getSelectedItem();
            if ("APK".equals(fmt)) {
                fileStatusLabel.setText("APK - стандартный формат для установки");
            } else {
                fileStatusLabel.setText("AAB - современный формат для Google Play Store");
            }
        });
        formatPanel.add(formatCombo, BorderLayout.WEST);
        formatPanel.add(fileStatusLabel, BorderLayout.CENTER);
        centerPanel.add(formatPanel);

        // Выбор файла
        centerPanel.add(createFilePanel("Выбор файла приложения", apkPathField = new JTextField(40), () -> {
            selectAppFileAdvanced((String)formatCombo.getSelectedItem());
        }));

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
        clearButton.addActionListener(e -> clearSigningFields());
        buttonPanel.add(clearButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    // ========== ВКЛАДКА 2: Пересборка Release ==========
    private JPanel createRebuildTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок
        JLabel titleLabel = new JLabel("Пересборка Android проекта в Release", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Центральная часть
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 5, 5));

        // Выбор папки проекта
        centerPanel.add(createFilePanel("Папка проекта Android", projectPathField = new JTextField(40), () -> selectProjectPath()));

        // Выбор Keystore
        JPanel keystorePanel = createFilePanel("Keystore для подписи", new JTextField(40), () -> selectKeystoreForRebuild());
        centerPanel.add(keystorePanel);

        // Логирование и прогресс
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Вывод пересборки"));

        rebuildLogArea = new JTextArea(10, 70);
        rebuildLogArea.setEditable(false);
        rebuildLogArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        JScrollPane scrollPane = new JScrollPane(rebuildLogArea);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        rebuildProgressBar = new JProgressBar();
        rebuildProgressBar.setStringPainted(true);
        rebuildStatusLabel = new JLabel("Готово");
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(rebuildProgressBar, BorderLayout.CENTER);
        statusPanel.add(rebuildStatusLabel, BorderLayout.EAST);
        logPanel.add(statusPanel, BorderLayout.SOUTH);

        centerPanel.add(logPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Кнопки действий
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        rebuildButton = new JButton("Пересобрать и подписать Release");
        rebuildButton.addActionListener(e -> rebuildProject());
        buttonPanel.add(rebuildButton);

        JButton clearRebuildButton = new JButton("Очистить");
        clearRebuildButton.addActionListener(e -> clearRebuildFields());
        buttonPanel.add(clearRebuildButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
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

    // ========== ВЫБОР ФАЙЛОВ (Вкладка 1) ==========

    private void selectAppFileAdvanced(String format) {
        JFileChooser chooser = new JFileChooser();
        if ("APK".equals(format)) {
            chooser.setFileFilter(new FileNameExtensionFilter("APK файлы", "apk"));
        } else {
            chooser.setFileFilter(new FileNameExtensionFilter("AAB файлы", "aab"));
        }
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            apkPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            analyzeAPK(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void selectAPK() {
        selectAppFileAdvanced((String)formatCombo.getSelectedItem());
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

    // ========== ВЫБОР ФАЙЛОВ (Вкладка 2) ==========

    private void selectProjectPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedPath = chooser.getSelectedFile();
            projectPathField.setText(selectedPath.getAbsolutePath());
            analyzeAndroidProject(selectedPath.getAbsolutePath());
        }
    }

    private void selectKeystoreForRebuild() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Keystore файлы", "jks", "keystore"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            keystorePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void analyzeAPK(String apkPath) {
        logMessage("Анализ APK файла...");
        try {
            File apkFile = new File(apkPath);
            if (apkFile.exists()) {
                double sizeInMB = apkFile.length() / (1024.0 * 1024.0);
                logMessage("✓ APK файл загружен успешно");
                logMessage("  Размер: " + String.format("%.2f", sizeInMB) + " MB");
            }
        } catch (Exception e) {
            logMessage("✗ Ошибка при анализе APK: " + e.getMessage());
        }
    }

    private void analyzeAndroidProject(String projectPath) {
        rebuildLogMessage("Анализ проекта Android...");
        try {
            File buildGradle = new File(projectPath, "build.gradle");
            File gradlew = new File(projectPath, "gradlew.bat");
            File settings = new File(projectPath, "settings.gradle");

            if (buildGradle.exists()) {
                rebuildLogMessage("✓ Найден build.gradle");
            }
            if (gradlew.exists()) {
                rebuildLogMessage("✓ Найден gradlew.bat (пересборка будет с gradlew)");
            } else {
                rebuildLogMessage("⚠ gradlew не найден (может потребоваться gradle в PATH)");
            }
            if (settings.exists()) {
                rebuildLogMessage("✓ Найден settings.gradle");
            }

            // Проверить папку app
            File appDir = new File(projectPath, "app");
            if (appDir.exists() && appDir.isDirectory()) {
                rebuildLogMessage("✓ Найдена папка app/");
            }

        } catch (Exception e) {
            rebuildLogMessage("✗ Ошибка при анализе проекта: " + e.getMessage());
        }
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void rebuildLogMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            rebuildLogArea.append("[" + timestamp + "] " + message + "\n");
            rebuildLogArea.setCaretPosition(rebuildLogArea.getDocument().getLength());
        });
    }

    private void clearSigningFields() {
        apkPathField.setText("");
        keystorePathField.setText("");
        outputDirField.setText("");
        logArea.setText("");
        statusLabel.setText("Готово");
        progressBar.setValue(0);
    }

    private void clearRebuildFields() {
        projectPathField.setText("");
        rebuildLogArea.setText("");
        rebuildStatusLabel.setText("Готово");
        rebuildProgressBar.setValue(0);
    }

    // ========== ГЕНЕРАЦИЯ ПОДПИСЕЙ (Вкладка 1) ==========

    private void generateSignatures() {
        if (apkPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите файл приложения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String outputDir = outputDirField.getText().isEmpty()
                ? new File(apkPathField.getText()).getParent()
                : outputDirField.getText();
        
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

                final String keystorePath = keystorePathField.getText().isEmpty()
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

                // Подписать файл
                if ("APK".equals(format)) {
                    logMessage("\n2. Подпись APK файла...");
                    signAPK(apkPathField.getText(), keystorePath, new File(outputDir, "app-signed.apk").getAbsolutePath());
                } else {
                    logMessage("\n2. Подпись AAB файла...");
                    signAAB(apkPathField.getText(), keystorePath, new File(outputDir, "app-signed.aab").getAbsolutePath());
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
                    logMessage("\n5. AAB используется только для Google Play Store");
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

    // ========== ПЕРЕСБОРКА ПРОЕКТА (Вкладка 2) ==========

    private void rebuildProject() {
        if (projectPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, выберите папку проекта", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final String projectPath = projectPathField.getText();
        final String keystorePath = keystorePathField.getText().isEmpty()
                ? new File(projectPath, "app-release-key.jks").getAbsolutePath()
                : keystorePathField.getText();

        new Thread(() -> {
            try {
                rebuildProgressBar.setValue(0);
                rebuildStatusLabel.setText("Пересборка...");
                rebuildButton.setEnabled(false);

                rebuildLogMessage("\n" + "=".repeat(60));
                rebuildLogMessage("ПЕРЕСБОРКА ANDROID ПРОЕКТА В РЕЖИМЕ RELEASE");
                rebuildLogMessage("=".repeat(60));

                // Проверить требуемые файлы
                File buildGradle = new File(projectPath, "build.gradle");
                if (!buildGradle.exists()) {
                    throw new Exception("Не найден build.gradle. Убедитесь, что выбрана корневая папка проекта!");
                }

                rebuildProgressBar.setValue(5);

                // Шаг 1: Очистить предыдущие сборки
                rebuildLogMessage("\n1. Очистка предыдущих сборок (clean)...");
                runGradleCommand(projectPath, "clean");
                rebuildProgressBar.setValue(15);

                // Шаг 2: Собрать Release версию
                rebuildLogMessage("\n2. Сборка Release версии...");
                runGradleCommand(projectPath, "assembleRelease");
                rebuildProgressBar.setValue(50);

                // Шаг 3: Найти созданный APK
                rebuildLogMessage("\n3. Поиск сгенерированного APK файла...");
                File releaseApk = findReleaseApk(projectPath);
                if (releaseApk == null || !releaseApk.exists()) {
                    throw new Exception("Не удалось найти Release APK. Проверьте логи сборки.");
                }
                rebuildLogMessage("✓ Найден Release APK: " + releaseApk.getAbsolutePath());
                rebuildProgressBar.setValue(60);

                // Шаг 4: Создать keystore если нужен
                if (!new File(keystorePath).exists()) {
                    rebuildLogMessage("\n4. Создание нового Keystore...");
                    createKeystore(keystorePath);
                } else {
                    rebuildLogMessage("\n4. Используется существующий keystore");
                }
                rebuildProgressBar.setValue(70);

                // Шаг 5: Подписать APK
                rebuildLogMessage("\n5. Подпись Release APK нашим ключом...");
                final String outputDir = new File(projectPath, "release_signed").getAbsolutePath();
                new File(outputDir).mkdirs();
                final String signedApkPath = new File(outputDir, "app-release-signed.apk").getAbsolutePath();
                signAPK(releaseApk.getAbsolutePath(), keystorePath, signedApkPath);
                rebuildProgressBar.setValue(80);

                // Шаг 6: Экспортировать сертификат
                rebuildLogMessage("\n6. Экспорт сертификата в PEM...");
                exportCertificate(keystorePath, outputDir);
                rebuildProgressBar.setValue(90);

                // Шаг 7: Генерировать информацию для маркетов
                rebuildLogMessage("\n7. Создание информации для маркетов...");
                generatePlayMarketSignature(keystorePath, outputDir);
                generateRuStoreSignature(keystorePath, outputDir);
                rebuildProgressBar.setValue(95);

                rebuildLogMessage("\n" + "=".repeat(60));
                rebuildLogMessage("✓ ПЕРЕСБОРКА И ПОДПИСЬ ЗАВЕРШЕНЫ УСПЕШНО!");
                rebuildLogMessage("=".repeat(60));
                rebuildLogMessage("\nПодписанный APK: " + signedApkPath);
                rebuildLogMessage("Сертификат: " + outputDir + "\\certificate.pem");

                rebuildProgressBar.setValue(100);
                rebuildStatusLabel.setText("Завершено!");
                JOptionPane.showMessageDialog(this,
                        "Пересборка и подпись завершены успешно!\n\n" +
                                "Подписанный APK: " + signedApkPath + "\n\n" +
                                "Папка с результатами: " + outputDir,
                        "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                rebuildLogMessage("\n✗ ОШИБКА: " + e.getMessage());
                rebuildStatusLabel.setText("Ошибка!");
                JOptionPane.showMessageDialog(this, "Ошибка при пересборке: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                rebuildButton.setEnabled(true);
            }
        }).start();
    }

    private void runGradleCommand(String projectPath, String command) throws Exception {
        rebuildLogMessage("  Выполнение: gradle " + command);

        File projectDir = new File(projectPath);
        File gradlewBat = new File(projectPath, "gradlew.bat");
        File gradlewUnix = new File(projectPath, "gradlew");

        ProcessBuilder pb;
        if (gradlewBat.exists()) {
            pb = new ProcessBuilder(gradlewBat.getAbsolutePath(), command);
        } else if (gradlewUnix.exists()) {
            pb = new ProcessBuilder(gradlewUnix.getAbsolutePath(), command);
        } else {
            pb = new ProcessBuilder("gradle", command);
        }

        pb.directory(projectDir);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            rebuildLogMessage("  " + line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new Exception("Gradle команда завершилась с ошибкой (код " + exitCode + ")");
        }
        rebuildLogMessage("  ✓ " + command + " успешно завершён");
    }

    private File findReleaseApk(String projectPath) {
        // Стандартные пути для Release APK:
        String[] possiblePaths = {
                projectPath + "/app/build/outputs/apk/release/app-release.apk",
                projectPath + "/app/build/outputs/apk/release/app-release-unsigned.apk",
                projectPath + "/build/outputs/apk/release/app-release.apk",
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                return file;
            }
        }

        // Если не найден по стандартному пути - поискать рекурсивно
        return findApkRecursive(new File(projectPath, "app/build/outputs/apk"));
    }

    private File findApkRecursive(File dir) {
        if (!dir.exists()) return null;

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".apk")) {
                    return file;
                }
                if (file.isDirectory()) {
                    File found = findApkRecursive(file);
                    if (found != null) return found;
                }
            }
        }
        return null;
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    private void createKeystore(String keystorePath) throws Exception {
        rebuildLogMessage("  Создание keystore...");

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
            rebuildLogMessage("✓ Keystore создан");
        } else {
            throw new Exception("Ошибка при создании keystore!");
        }
    }

    private void signAPK(String apkPath, String keystorePath, String outputPath) throws Exception {
        // Вызови версию для логирования (используется из обеих вкладок)
        signAPKInternal(apkPath, keystorePath, outputPath, true);
    }

    private void signAAB(String aabPath, String keystorePath, String outputPath) throws Exception {
        signAABInternal(aabPath, keystorePath, outputPath, true);
    }

    private void signAPKInternal(String apkPath, String keystorePath, String outputPath, boolean isTabOne) throws Exception {
        if (isTabOne) {
            logMessage("  Подпись APK (jarsigner)...");
            logMessage("  Файлы:");
            logMessage("    APK: " + apkPath);
            logMessage("    Keystore: " + keystorePath);
            logMessage("    Выход: " + outputPath);
        } else {
            rebuildLogMessage("  Подпись APK (jarsigner)...");
            rebuildLogMessage("  Файлы:");
            rebuildLogMessage("    APK: " + apkPath);
            rebuildLogMessage("    Keystore: " + keystorePath);
            rebuildLogMessage("    Выход: " + outputPath);
        }

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
            if (isTabOne) {
                logMessage("✓ APK подписан: " + String.format("%.2f", sizeInMB) + " MB");
            } else {
                rebuildLogMessage("✓ APK подписан: " + String.format("%.2f", sizeInMB) + " MB");
            }
        } else {
            String fullError = output.toString() + errorOutput.toString();
            if (isTabOne) {
                logMessage("DEBUG: Выход jarsigner:\n" + fullError);
            } else {
                rebuildLogMessage("DEBUG: Выход jarsigner:\n" + fullError);
            }
            throw new Exception("Ошибка при подписи APK (exit code: " + exitCode + ")\n" + fullError);
        }
    }

    private void signAABInternal(String aabPath, String keystorePath, String outputPath, boolean isTabOne) throws Exception {
        if (isTabOne) {
            logMessage("  Подпись AAB (jarsigner)...");
            logMessage("  Файлы:");
            logMessage("    AAB: " + aabPath);
            logMessage("    Keystore: " + keystorePath);
            logMessage("    Выход: " + outputPath);
        } else {
            rebuildLogMessage("  Подпись AAB (jarsigner)...");
            rebuildLogMessage("  Файлы:");
            rebuildLogMessage("    AAB: " + aabPath);
            rebuildLogMessage("    Keystore: " + keystorePath);
            rebuildLogMessage("    Выход: " + outputPath);
        }

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
            if (isTabOne) {
                logMessage("✓ AAB подписан: " + String.format("%.2f", sizeInMB) + " MB");
                logMessage("  Примечание: AAB подходит для Google Play Store");
            } else {
                rebuildLogMessage("✓ AAB подписан: " + String.format("%.2f", sizeInMB) + " MB");
                rebuildLogMessage("  Примечание: AAB подходит для Google Play Store");
            }
        } else {
            String fullError = output.toString() + errorOutput.toString();
            if (isTabOne) {
                logMessage("DEBUG: Выход jarsigner:\n" + fullError);
            } else {
                rebuildLogMessage("DEBUG: Выход jarsigner:\n" + fullError);
            }
            throw new Exception("Ошибка при подписи AAB (exit code: " + exitCode + ")\n" + fullError);
        }
    }

    private void exportCertificate(String keystorePath, String outputDir) throws Exception {
        rebuildLogMessage("  Экспорт сертификата...");

        String certPath = new File(outputDir, "certificate.pem").getAbsolutePath();

        ProcessBuilder pbOutput = new ProcessBuilder(
                "cmd.exe", "/C",
                "keytool -export -alias " + KEY_ALIAS + " -keystore " + keystorePath +
                        " -storepass " + KEYSTORE_PASSWORD + " -rfc > " + certPath
        );

        Process process = pbOutput.start();
        int exitCode = process.waitFor();

        if (exitCode == 0 && new File(certPath).exists()) {
            rebuildLogMessage("✓ Сертификат экспортирован");
        } else {
            rebuildLogMessage("⚠ Не удалось экспортировать сертификат");
        }
    }

    private void generatePlayMarketSignature(String keystorePath, String outputDir) throws Exception {
        generatePlayMarketSignature(keystorePath, outputDir, "APK");
    }

    private void generatePlayMarketSignature(String keystorePath, String outputDir, String format) throws Exception {
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
            content.append("5. Google Play автоматически создаст APK для каждого устройства\n\n");
            content.append("Преимущества AAB:\n");
            content.append("- Меньший размер загрузки для пользователя\n");
            content.append("- Оптимизация для каждого устройства\n");
        }

        Files.write(infoFile.toPath(), content.toString().getBytes());
    }

    private void generateRuStoreSignature(String keystorePath, String outputDir) throws Exception {
        File infoFile = new File(outputDir, "rustore_info.txt");
        StringBuilder content = new StringBuilder();
        content.append("RuStore - Информация о подписи\n");
        content.append("=".repeat(50)).append("\n\n");
        content.append("Сертификат: certificate.pem\n");
        content.append("Приложение: app-release-signed.apk\n\n");
        content.append("Инструкции для загрузки:\n");
        content.append("1. Откройте RuStore Developer Console\n");
        content.append("2. Создайте новое приложение\n");
        content.append("3. Загрузите app-release-signed.apk\n");

        Files.write(infoFile.toPath(), content.toString().getBytes());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new APKSignerAdvanced());
    }
}
