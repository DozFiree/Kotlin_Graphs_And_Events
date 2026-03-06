package Kool_KontrolWork

// 1. А я не скажу
// 2. taleLast(20) - это специальная функция, которая позволяет выводить список из 20 сообщений(ну или чего то другого, что есть в списке), и удаляет последние сообщения, чтобы их было 20, чтобы не перегружать например чат.
// 3. suspend функция - это функция, которая будет приостанавливаться во время выполнения другой какой либо функции
// 4. Лямбда - это переменная, которая быстро объявляется, без val, var и т.п.

import de.fabmax.kool.KoolApplication           // KoolApplication - запускает Kool-приложение (окно + цикл рендера)
import de.fabmax.kool.addScene                  // addScene - функция "добавь сцену" в приложение (у тебя она просила отдельный импорт)
import de.fabmax.kool.math.FLT_EPSILON
import de.fabmax.kool.math.Vec3f                // Vec3f - 3D-вектор (x, y, z), как координаты / направление
import de.fabmax.kool.math.deg                  // deg - превращает число в "градусы" (угол)
import de.fabmax.kool.scene.*                   // scene.* - Scene, defaultOrbitCamera, addColorMesh, lighting и т.д.
import de.fabmax.kool.modules.ksl.KslPbrShader  // KslPbrShader - готовый PBR-шейдер (материал)
import de.fabmax.kool.util.Color                // Color - цвет (RGBA)
import de.fabmax.kool.util.Time                 // Time.deltaT - сколько секунд прошло между кадрами
import de.fabmax.kool.pipeline.ClearColorLoad   // ClearColorLoad - режим: "не очищай экран, оставь то что уже нарисовано"
import de.fabmax.kool.modules.ui2.*             // UI2: addPanelSurface, Column, Row, Button, Text, dp, remember, mutableStateOf
import de.fabmax.kool.util.DynamicStruct
import de.fabmax.kool.modules.ui2.UiModifier.*

import kotlinx.coroutines.launch  // запускает корутину
import kotlinx.coroutines.Job     // контроллер запущенной корутины
import kotlinx.coroutines.isActive // проверка жива ли ещё корутина - полезно для циклов
import kotlinx.coroutines.delay

// Flow корутины
import kotlinx.coroutines.launch // запуск корутины
import kotlinx.coroutines.flow.MutableSharedFlow // табло состояний
import kotlinx.coroutines.flow.SharedFlow // Только чтение для подписчиков
import kotlinx.coroutines.flow.MutableStateFlow // Радиостанция событий
import kotlinx.coroutines.flow.StateFlow // Только для чтения стостояний
import kotlinx.coroutines.flow.asSharedFlow // Отдать наружу только SharedFlow
import kotlinx.coroutines.flow.asStateFlow // Отдать только StateFlow
import kotlinx.coroutines.flow.collect // слушать поток

// импорты Serialization
import kotlinx.serialization.Serializable // Анотация, что можно сохранять
import kotlinx.serialization.builtins.ShortArraySerializer
import kotlinx.serialization.encodeToString //запись в файл
import kotlinx.serialization.decodeFromString // Чтение файла
import kotlinx.serialization.json.Json // Формат файла Json
import lesson7.DialogueOption
import lesson7.DialogueView
import lesson7.Listener
import lesson9.GameState


import java.io.File

class GameState{
    val playerId = mutableStateOf("Oleg")

    val hp = mutableStateOf(100)
    val maxHp = 100

    val questState = mutableStateOf(QuestState.START)
    val attackSpeedTicksLeft = mutableStateOf(0)
    val attackCoolDownMsLeft = mutableStateOf(0L)

    val logLines = mutableStateOf<List<String>>(emptyList())
}
fun pushLog(game: GameState, text: String){
    game.logLines.value = (game.logLines.value + text).takeLast(20)
}
//Добавляем в Effect manager эффект на ускорение атаки
class EffectManager (
    private val game: GameState,
    private val scope: kotlinx.coroutines.CoroutineScope
){
    private var attackSpeedJob: Job? = null

}
sealed interface GameEvent{
    val playerId: String
}
data class AttackPressed(
    override val playerId: String,
    val targetId: String
): GameEvent

data class DamageDealt(
    override val playerId: String,
    val targetId: String,
    val amount: Int
) : GameEvent
data class QuestStateChanged(
    override val playerId: String,
    val questId: String,
    val newState: String
): GameEvent
data class ChoiceSelected(
    override val playerId: String,
    val npcId: String,
    val choiceId: String
): GameEvent
data class AttackSpeedBuffApplied(
    override val playerId: String,
    val ticks: Int,
): GameEvent
data class  TalkedToNpc(
    override val playerId: String,
    val npcId: String,
): GameEvent
enum class QuestState{
    START,
    OFFERED,
    HELP_ACCEPTED,
    GOOD_END,
}

data class DialogueOption(
    val id: String,
    val text: String,
)
data class DialogueView(
    val npcName: String,
    val text: String,
    val options: List<DialogueOption>
)

class Npc(
    val id: String,
    val name: String
){
    fun dialogueFor(state: QuestState): DialogueView {
        return when(state){
            QuestState.START -> DialogueView(
                name,
                "Привет! Нажми Talk, чтобы начать диалог",
                listOf(
                    DialogueOption("talk", "Говорить")
                )
            )
            QuestState.OFFERED -> DialogueView(
                name,
                "Поможешь?",
                listOf(
                    DialogueOption("help", "Помочь"),
                )
            )
           QuestState.HELP_ACCEPTED -> DialogueView(
                name,
                "Мне необходимо убить манекена. Убей его.",
                emptyList()
            )
            QuestState.GOOD_END -> DialogueView(
                name,
                "Спасибо за помощь!",
                emptyList()
            )
        }
    }
}
typealias Listener = (GameEvent) -> Unit
class EventBus{
    private val listeners = mutableStateListOf<lesson7.Listener>()

    fun subscribe(listener: Listener){
        listeners.add(listener)
    }

    fun publish(event: lesson7.GameEvent){
        for (l in listeners){
            l(event)
        }
    }
}

// Команда - "запрос клиента на сервер"
sealed interface GameCommand {
    val playerId: String
}

data class CmdTalkToNpc(
    override val playerId: String,
    val npcId: String
): GameCommand

data class CommandRejected(
    override val playerId: String,
    val reason: String
): GameCommand

data class CmdSelectedChoice(
    override val playerId: String,
    val npcId: String,
    val choiceId: String
): GameCommand

class CooldownManager(
    private val game: GameState,
    private val scope: kotlinx.coroutines.CoroutineScope
){
    private  var cooldownJob: Job? = null

    fun startAttackCooldown(totalMs: Long) {
        cooldownJob?.cancel()

        game.attackCoolDownMsLeft.value = totalMs
        pushLog(game, "Кулдаун атаки ${totalMs}мс")

        cooldownJob = scope.launch {
            val step = 100L

            while(isActive && game.attackCoolDownMsLeft.value > 0L){
                delay(step)
                game.attackCoolDownMsLeft.value = (game.attackCoolDownMsLeft.value - step).coerceAtLeast(0)
            }
        }
    }
    fun canAttack(): Boolean{
        return game.attackCoolDownMsLeft.value <= 0L
    }
}

fun main() = KoolApplication {
    val game = GameState()

}