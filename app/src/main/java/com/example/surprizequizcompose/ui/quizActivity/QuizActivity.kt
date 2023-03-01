package com.example.surprizequizcompose.ui.quizActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.surprizequizcompose.router.QuizRouter


class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizRouter(quizViewModel = QuizViewModel())
        }
    }
}



