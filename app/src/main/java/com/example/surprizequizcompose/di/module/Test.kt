package com.example.surprizequizcompose.di.module

import com.example.surprizequizcompose.repository.QuizRepository
import com.example.surprizequizcompose.ui.quizActivity.QuizViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object Test {
    fun module() = repositoryModule + viewModelModule + commonModule
}

val repositoryModule = module {
    single {
        QuizRepository()
    }
}

val viewModelModule = module {
    viewModel {
        QuizViewModel()
    }
}

val commonModule = module {

}