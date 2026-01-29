package Za4et


class DialogNode(
    val state: QuestState,
    val text: String
){
    // варианты выбора игрока(переходы)
    // choiceId -> следующее состояние после выбора
    private val choices = mutableMapOf<String, QuestState>()

    fun addChoice(choiceId: String, nextState: QuestState){
        choices[choiceId] = nextState
    }

    fun getNextState(choiceId: String):  QuestState? {
        return choices[choiceId]
    }
    fun print(){
        println("NPC говорит: \"$text\" ")
        println("Варианты: ")
        for (choice in choices.keys){
            println("→ $choice")
        }
    }
}