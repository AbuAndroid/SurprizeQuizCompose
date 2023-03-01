package com.example.surprizequizcompose.model

data class QuizModel(
    val quizTitle:String?,
    val questions:ArrayList<Questions>
)

data class Questions(
    var questionId:String?,
    var questionTitle:String?,
    var questionImage:String?,
    val options: MutableList<Options>
)

data class Options(
    var optionId:String?,
    var option : String?,
    var optionImage:String?,
    var isAnswer : Boolean = false,
)
