package com.caelis.app

class CaelisEngine {

fun process(input: String): String {
    val command = input.trim().lowercase()

    if (command.isEmpty()) {
        return "Команда не получена. Ожидаю ввода."
    }

    return when {
        command in listOf("привет", "здравствуй", "здравствуй, целис", "hello", "hi") ->
            "Приветствую. CAELIS 0.1 готов к работе."

        command.contains("статус") ->
            "Система CAELIS: активна.\n" +
            "Локальное ядро: работает.\n" +
            "Облачный ИИ: пока не подключён.\n" +
            "Версия: 0.1.0."

        command.contains("помощь") ||
        command == "help" ||
        command.contains("что ты умеешь") ->
            "Доступные команды:\n\n" +
            "• Привет — приветствие.\n" +
            "• Статус — состояние системы.\n" +
            "• Время — текущее время устройства.\n" +
            "• Помощь — список команд.\n\n" +
            "Это начальная версия локального ядра."

        command.contains("время") ->
            "Текущее время устройства: " +
            java.text.SimpleDateFormat(
                "HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date())

        command.contains("кто ты") ||
        command.contains("твоё имя") ||
        command.contains("твое имя") ->
            "Я CAELIS — персональный интеллектуальный помощник. " +
            "Сейчас работаю в базовом локальном режиме."

        else ->
            "Команда принята: \"$input\"\n\n" +
            "Пока я работаю без облачной языковой модели. " +
            "Для свободного диалога подключим ИИ на следующем этапе."
    }
}

}
