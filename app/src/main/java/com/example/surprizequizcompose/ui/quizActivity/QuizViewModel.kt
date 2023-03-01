package com.example.surprizequizcompose.ui.quizActivity

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

class QuizViewModel : ViewModel() {

    private val viewModelState = MutableStateFlow(QuizViewModelState())

    val uiState = viewModelState.map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    fun onQuizTitleEnter(quizTitle: String) {
        viewModelState.update { it.copy(quizTitle = quizTitle) }
    }

    fun onQuestionTitleEnter(questionId: Int, questionTitle: String) {
        val questionList = uiState.value.questions
        val question = questionList[questionId]
        question.questionName = questionTitle
        viewModelState.update {
            it.copy(
                questions = questionList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun onOptionSelected(questionId: Int, optionId: Int) {
        val questionList = viewModelState.value.questions
        val question = questionList[questionId]
        question.options?.forEach { option ->
            option.isSelected = false
            if (option.optionId.toInt() == optionId) {
                option.isSelected = true
            }
        }
        viewModelState.update {
            it.copy(
                questions = questionList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun addQuestion() {
        val quizList = uiState.value.questions
        val question = QuestionsUiModel(
            questionId = System.currentTimeMillis() ,
            questionName = "",
            options = mutableListOf(),
            lastUpdate = System.currentTimeMillis(),
            questionImage = ""
        )
        quizList.add(question)
        viewModelState.update {
            it.copy(
                questions = quizList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun deleteQuestion(questionId: Int) {
        val questionList = viewModelState.value.questions
        questionList.removeAt(questionId)
        viewModelState.update {
            it.copy(
                questions = questionList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun addQuestionBelow(questionId: Int) {
        val questionList = viewModelState.value.questions
        val question = QuestionsUiModel(
            questionId = System.currentTimeMillis(),
            questionName = "",
            options = mutableListOf(),
            lastUpdate = System.currentTimeMillis(),
            questionImage = ""
        )

        questionList.add(questionId+1, question)
        viewModelState.update {
            it.copy(
                questions = questionList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun copyQuestionBelow(questionId: Int, question: QuestionsUiModel) {
        val questionList = viewModelState.value.questions
        val optionList = question.options?.mapIndexed { index, option ->
            OptionsUiModel(
                optionId = System.currentTimeMillis()+index ,
                text = option.text,
                isSelected = option.isSelected,
                optionImage = option.optionImage
            )
        }
        val questionCard = QuestionsUiModel(
            questionId = System.currentTimeMillis(),
            questionName = question.questionName,
            questionImage = question.questionImage,
            options = optionList?.toMutableList(),
            lastUpdate = System.currentTimeMillis()
        )

        questionList.add(questionId+1, questionCard)
        viewModelState.update {
            it.copy(
                questions = questionList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun addOption(questionId: Int) {
        val questionList = uiState.value.questions
        val question = questionList[questionId]
        val option = question.options?.size?.let {
            OptionsUiModel(
                optionId = it.toLong() ,
                text = "",
                isSelected = false,
                optionImage = ""
            )
        }
        if (option != null) {
            question.options.add(option)
        }

        viewModelState.update {
            it.copy(
                questions = questionList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun deleteOption(questionId: Int, optionId: Int) {
        val questionList = viewModelState.value.questions
        val question = questionList[questionId]
        question.options?.removeAt(optionId)
        viewModelState.update { it.copy(questions = questionList, lastUpdate = System.currentTimeMillis()) }
    }

    fun onOptionTextEnter(questionId: Int, optionId: Int, optionText: String) {
        val questionList = viewModelState.value.questions
        val question = questionList[questionId]
        val option = question.options?.get(optionId)
        option?.text = optionText

        viewModelState.update {
            it.copy(
                questions = questionList,
                lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun setQuestionImageFromGallery(questionId: Int, questionImage: Uri) {
        val questionList = viewModelState.value.questions
        val question = questionList[questionId]
        question.questionImage = questionImage.toString()
        viewModelState.update {
            it.copy(
                questions = questionList, lastUpdate = System.currentTimeMillis()
            )
        }
    }

    fun setOptionImageFromGallery(questionId: Int, optionId: Int, optionImage: Uri) {
        val questionList = viewModelState.value.questions
        val question = questionList[questionId-1]
        val option = question.options?.get(optionId)
        if (option != null) {
            option.optionImage = optionImage.toString()
        }
        viewModelState.update {
            it.copy(
                questions = questionList, lastUpdate = System.currentTimeMillis()
            )
        }
    }
}

data class QuizViewModelState(
    val isLoading: Boolean? = false,
    val quizTitle: String? = "",
    val questions: MutableList<QuestionsUiModel> = mutableListOf(),
    val lastUpdate: Long = System.currentTimeMillis(),
) {
    fun toUiState(): QuizViewUiModel {
        return QuizViewUiModel(
            isLoading = isLoading,
            quizTitle = quizTitle,
            questions = questions,
            lastUpdate = lastUpdate,
        )
    }
}

data class QuizViewUiModel(
    var isLoading: Boolean? = false,
    val quizTitle: String?,
    val lastUpdate: Long,
    val questions: MutableList<QuestionsUiModel>,
    val isQuizFinished: Boolean = false,
)

data class QuestionsUiModel(
    val questionId: Long,
    var questionName: String?,
    val lastUpdate: Long,
    var questionImage: String,
    val options: MutableList<OptionsUiModel>?
)

data class OptionsUiModel(
    val optionId: Long,
    var text: String?,
    var optionImage: String,
    var isSelected: Boolean?
)