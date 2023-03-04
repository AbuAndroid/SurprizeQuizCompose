package com.example.surprizequizcompose.ui.quizActivity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.surprizequizcompose.router.QuizRouter


class QuizActivity : ComponentActivity() {
    private var pressedTime :Long =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizRouter(
                quizViewModel = QuizViewModel()
            )
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        if(pressedTime + 200 > System.currentTimeMillis()){
//            super.onBackPressed()
//            finish()
//        }else{
//            Toast.makeText(this,"Press back again to Exit",Toast.LENGTH_SHORT).show()
//        }
//        pressedTime = System.currentTimeMillis()
//
//    }


}



