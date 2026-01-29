package Za4et


class DialogGraph{
    private val nodes = mutableMapOf<QuestState, lesson15.DialogNode>()

    init{
        val start = DialogNode(
            QuestState.START,
            "Привет, подходи сюда."
        )
        val talking = DialogNode(
            QuestState.TALKING,
            "Я смотрю, ты тут впервые. Могу научить тебя сражаться. Хочешь?"
        )
        val accepted = DialogNode(
            QuestState.ACCEPTED,
            "Отлично, убей вон того манекена."
        )
        val decline = DialogNode(
            QuestState.DECLINE,
            "Тогда удачной дороги, путник!"
        )
        val dummyKilled = DialogNode(
            QuestState.DUMMY_KILLED,
            "Ты убил манекена? Подходи сюда!"
        )
        val completed = DialogNode(
            QuestState.COMPLETED,
            "Отлично! Ты доказал, что умеешь сражаться."
        )


         talking.addChoice("accept", QuestState.ACCEPTED)
        talking.addChoice("refuse", QuestState.DECLINE)

        accepted.addChoice("bye", QuestState.COMPLETED)
        decline.addChoice("bye", QuestState.COMPLETED)
        completed.addChoice("bye", QuestState.COMPLETED)

        listOf( accepted, decline, completed).forEach {
            nodes[it.state] = it
        }
    }
    fun getNode(state: QuestState): DialogNode{
        return nodes[state]!!
    }
}