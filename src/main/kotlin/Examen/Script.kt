package Examen

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

@Serializable
data class FileInfo(val filename: String, val lines: Int)

fun countLines(filePath: String): Int? = try {
    File(filePath).useLines { it.count() }
} catch (e: Exception) {
    println("Ошибка при чтении файла: ${e.message}")
    null
}

fun main() {
    println("Введите путь к файлу:")
    val filePath = readlnOrNull()?.trim()
    if (filePath.isNullOrEmpty()) {
        println("Путь не может быть пустым")
        return
    }

    val lineCount = countLines(filePath)
    if (lineCount == null) return

    val result = Json.encodeToString(FileInfo(filePath, lineCount))
    println(result)
}