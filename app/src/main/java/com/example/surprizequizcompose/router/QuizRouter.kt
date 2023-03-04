package com.example.surprizequizcompose.router

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.surprizequizcompose.ui.quizActivity.QuizScreen
import com.example.surprizequizcompose.ui.quizActivity.QuizViewModel

@Composable
fun QuizRouter(
    quizViewModel: QuizViewModel
) {
    val uiState by quizViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val activity = (LocalContext.current as Activity)
    NavHost(navController = navController, startDestination = "Quiz_screen") {
        composable("Quiz_screen") {

            QuizScreen(
                quizTitle = uiState.quizTitle,
                question = uiState.questions,
                onQuizTitleEnter = { quizTitle ->
                    quizViewModel.onQuizTitleEnter(quizTitle)
                },
                onQuestionNameEnter = { questionId,questionTitle ->
                    quizViewModel.onQuestionTitleEnter(questionId,questionTitle)
                },
                onOptionSelected = { questionId,optionId->
                    quizViewModel.onOptionSelected(questionId,optionId)
                },
                addQuestion = {
                    quizViewModel.addQuestion()
                },
                addOption = {questionId->
                    quizViewModel.addOption(questionId)
                },
                onOptionEnter = {questionId,optionId,optionText->
                    quizViewModel.onOptionTextEnter(questionId,optionId,optionText)
                },
                addQuestionBelow = {questionId->
                    quizViewModel.addQuestionBelow(questionId)
                },
                copyQuestionBelow = {questionId,question->
                    quizViewModel.copyQuestionBelow(questionId,question)
                },
                deleteQuestion = {questionId->
                    quizViewModel.deleteQuestion(questionId)
                },
                getQuestionImageFromGallery = { questionId, questionImage->
                    quizViewModel.setQuestionImageFromGallery(questionId,questionImage)
                },
                getOptionImageFromGallery = { questionId, optionId, optionImage->
                    quizViewModel.setOptionImageFromGallery(questionId,optionId,optionImage)
                },
                deleteOption = { questionId, optionId ->
                    quizViewModel.deleteOption(questionId, optionId)
                },
                closeApplication = {
                    activity.finish()
                }
            )
        }
    }
}