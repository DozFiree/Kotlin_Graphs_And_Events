package lesson14

import lesson12.EventBus
import lesson12.GameEvent
import kotlin.math.E

fun main() {
    val system = VillageQuestSystem()
    system.register()

    val player = "Oleg"
    val traitor = "Oleg_traitor"
    val clown = "Далбоёб"

    //Герой
    EventBus.post(GameEvent.DialogueStarted(player, "Старый"))
    EventBus.post(GameEvent.QuestStateChanged(player, "NOT_STARTED", "TALKED_TO_ELDER"))
    EventBus.post(GameEvent.DialogueChoiceSelected("Старый", "accept", "Oleg"))
    EventBus.post(GameEvent.QuestStateChanged(player, "TALKED_TO_ELDER", "ACCEPTED_HELP"))
    EventBus.post(GameEvent.CharacterDied("Kirill", "Oleg", "Oleg"))
    EventBus.post(GameEvent.QuestStateChanged(player, "ACCEPTED_HELP", "KILLED_KIRILL_SHAMAN"))
    EventBus.post(GameEvent.DialogueChoiceSelected("Старый", "report", "Oleg"))
    EventBus.post(GameEvent.QuestStateChanged(player, "KILLED_KIRILL_SHAMAN", "HERO_ENDING"))

    //Предатель
    EventBus.post(GameEvent.DialogueStarted(traitor, "Старый"))
    EventBus.post(GameEvent.QuestStateChanged(player, "NOT_STARTED", "TALKED_TO_ELDER"))
    EventBus.post(GameEvent.DialogueChoiceSelected("Старый", "decline", traitor))
    EventBus.post(GameEvent.QuestStateChanged(player, "TALKED_TO_ELDER", "REFUSED_HELP"))
    EventBus.post(GameEvent.DialogueChoiceSelected("Kirill", "help_kirill", traitor))
    EventBus.post(GameEvent.QuestStateChanged(player, "REFUSED_HELP", "HELPED_KIRILL"))
    EventBus.post(GameEvent.QuestStateChanged(player, "HELPED_KIRILL", "BAD_ENDING"))

    //Далбоёб
    EventBus.post(GameEvent.DialogueStarted(clown, "Старый"))
    EventBus.post(GameEvent.QuestStateChanged(player, "NOT_STARTED", "TALKED_TO_ELDER"))
    EventBus.post(GameEvent.DialogueChoiceSelected("Старый", "accept", clown))
    EventBus.post(GameEvent.QuestStateChanged(player, "TALKED_TO_ELDER", "ACCEPTED_HELP"))
    EventBus.post(GameEvent.CharacterDied("Kirill", clown, clown))
    EventBus.post(GameEvent.QuestStateChanged(player, "ACCEPTED_HELP", "KILLED_KIRILL_SHAMAN"))
    EventBus.post(GameEvent.DialogueChoiceSelected("Kirill", "help_Kirill", clown))
    EventBus.post(GameEvent.QuestStateChanged(player, "KILLED_KIRILL_SHAMAN", "HELPED_KIRILL"))
    EventBus.post(GameEvent.QuestStateChanged(player, "HELPED_KIRILL", "BAD_ENDING"))


    EventBus.processQueue(100)
}