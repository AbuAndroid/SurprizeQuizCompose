package com.example.surprizequizcompose.ui.quizActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surprizequizcompose.R
import com.example.surprizequizcompose.ui.theme.buttonGradient
import com.example.surprizequizcompose.ui.theme.button_corner
import com.example.surprizequizcompose.ui.theme.primary_white
import com.example.surprizequizcompose.ui.theme.quizContentBackground

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    QuizScreen()
}

@Composable
fun QuizScreen() {

    Scaffold(
        topBar = { TopBar() },
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        QuizContent(innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar()
}

@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1A7BDC), Color(0xFF56B8FF)),
                    start = Offset(0f, 0f),
                    end = Offset(1f, 500f)
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.appbarbackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back home",
                    tint = Color.White
                )
            }
            Text(
                text = "Engager",
                color = Color.White, fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizContentPreview() {
    QuizContent(innerPadding = PaddingValues(10.dp))
}

@Composable
fun QuizContent(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .background(quizContentBackground)
            .fillMaxSize(1f)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Quiz title",
                    modifier = Modifier
                        .padding(bottom = 5.dp),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.LightGray,
                        textColor = Color.Gray
                    ),
                    value = "",
                    onValueChange = {}
                )

            }
        }

        LazyColumn {
            item{
                QuestionItem()
               // QuestionItem()
            }
        }

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF7FCFD),
                            Color(0xFFE1F5FA)
                        )
                    )
                ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            elevation = ButtonDefaults.elevation(0.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "+ Add New Question",
                color = button_corner
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    BottomBar()
}

@Composable
fun BottomBar() {
    Card(
        // backgroundColor = primary_white,
        //modifier = Modifier.padding(10.dp),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, button_corner),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = button_corner
                ),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Cancel")
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = button_corner,
                    contentColor = primary_white
                )
            ) {
                Text(text = "Save")
            }
        }
    }
}
