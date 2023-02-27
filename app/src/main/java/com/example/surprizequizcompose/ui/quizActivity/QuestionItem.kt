package com.example.surprizequizcompose.ui.quizActivity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surprizequizcompose.R
import com.example.surprizequizcompose.ui.theme.addQuestionButtonBackground
import com.example.surprizequizcompose.ui.theme.button_corner

@Preview(showBackground = true)
@Composable
fun QuestionItemPreview() {
    QuestionItem()
}

@Composable
fun QuestionItem() {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp),
      //  border = BorderStroke()
    ) {
        Column {
            TextField(
                value = "01. Question 01",
                onValueChange = {},
                trailingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_insert_photo),
                            contentDescription = "Clear"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            )

            OptionItem()
            OptionItem()
            
            Button(
                modifier = Modifier.padding(horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = addQuestionButtonBackground
                ),
                elevation = ButtonDefaults.elevation(0.dp),
                shape = RoundedCornerShape(15.dp),
                onClick = { /*TODO*/ }) {
                Text(
                    text = "+ Add Option",
                    color = Color.DarkGray
                )
            }

            Divider()

            Row(
                modifier = Modifier.padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.offset(10.dp),
                    text = "Set Answer Key",
                    color = button_corner
                )

                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon( Icons.Filled.Add , contentDescription = "Add")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon( Icons.Filled.CopyAll, contentDescription = "Copy")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon( Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionitemPreview() {
    OptionItem()
}

@Composable
fun OptionItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = false, onClick = { /*TODO*/ })
        Text(text = "Option 1")
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_insert_photo),
                contentDescription = "image"
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close"
            )
        }
    }
}