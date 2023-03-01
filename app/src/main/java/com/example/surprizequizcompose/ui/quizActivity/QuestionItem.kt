package com.example.surprizequizcompose.ui.quizActivity

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.surprizequizcompose.R
import com.example.surprizequizcompose.ui.theme.addQuestionButtonBackground
import com.example.surprizequizcompose.ui.theme.button_corner

@Preview(showBackground = true)
@Composable
fun QuestionItemPreview() {
    QuestionItem(
        questionPosition = 0,
        questionId = 1,
        questionTitle = "What is Your Name ?",
        onQuestionNameEnter = { },
        options = mutableListOf(
            OptionsUiModel(
                optionId = 1,
                text = "Option 1",
                isSelected = false,
                optionImage = ""
            )
        ),
        onOptionSelected = { },
        addOption = {},
        onOptionEnter = { _, _ -> },
        addQuestionBelow = {},
        copyQuestionBelow = {},
        deleteQuestion = {},
        setAnswerKey = {},
        question = QuestionsUiModel(
            questionId = 1,
            questionImage = "image",
            lastUpdate = System.currentTimeMillis(),
            questionName = "",
            options = mutableListOf()
        ),
        getQuestionImageFromGallery = { },
        getOptionImageFromGallery = {},
        deleteOption = {},
        openQuestionImageBottomSheet = {}
    )
}

@Composable
fun QuestionItem(
    questionPosition:Int,
    questionId: Long,
    questionTitle: String,
    onQuestionNameEnter: (String) -> Unit,
    options: MutableList<OptionsUiModel>,
    onOptionSelected: (Int) -> Unit,
    addOption: () -> Unit,
    onOptionEnter: (Int, String) -> Unit,
    addQuestionBelow: () -> Unit,
    copyQuestionBelow: () -> Unit,
    deleteQuestion: () -> Unit,
    setAnswerKey: () -> Unit,
    question: QuestionsUiModel,
    getQuestionImageFromGallery: () -> Unit,
    getOptionImageFromGallery: (Int) -> Unit,
    deleteOption: (Int) -> Unit,
    openQuestionImageBottomSheet:(Int)->Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            TextField(
                value = questionTitle,
                onValueChange = { questionTitle ->
                    onQuestionNameEnter(questionTitle)
                },
                placeholder = {
                    Text(text = "${questionPosition + 1}, Enter Question Name :")
                },
                trailingIcon = {
                    IconButton(onClick = {
                        openQuestionImageBottomSheet(questionPosition)
                    }) {
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

            QuestionImage(question)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                options.forEachIndexed { optionPosition, option ->
                    OptionItem(
                        optionPosition = optionPosition,
                        option = option,
                        onOptionSelected = {
                            onOptionSelected(optionPosition)
                        },
                        onOptionEnter = { optionText ->
                            onOptionEnter(optionPosition, optionText)
                        },
                        getOptionImage = {
                            getOptionImageFromGallery(optionPosition)
                        },
                        deleteOption = {
                            deleteOption(optionPosition)
                        }
                    )
                }

            }
            Button(
                modifier = Modifier.padding(horizontal = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = addQuestionButtonBackground
                ),
                elevation = ButtonDefaults.elevation(0.dp),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    addOption()
                }) {
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
                    modifier = Modifier
                        .offset(10.dp)
                        .clickable {
                            setAnswerKey()
                        },
                    text = "Set Answer Key",
                    color = button_corner
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    addQuestionBelow()
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
                IconButton(onClick = {
                    copyQuestionBelow()
                }) {
                    Icon(Icons.Filled.CopyAll, contentDescription = "Copy")
                }
                IconButton(onClick = {
                    deleteQuestion()
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun QuestionImage(question: QuestionsUiModel) {
    if (question.questionImage.isNotEmpty()) {
        Image(
            painter = rememberImagePainter(data = Uri.parse(question.questionImage)),
            contentDescription = "questionImage",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}


