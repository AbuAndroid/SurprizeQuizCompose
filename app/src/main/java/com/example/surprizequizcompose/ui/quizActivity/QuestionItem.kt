package com.example.surprizequizcompose.ui.quizActivity

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.surprizequizcompose.R
import com.example.surprizequizcompose.ui.theme.addQuestionButtonBackground
import com.example.surprizequizcompose.ui.theme.button_corner

@Preview(showBackground = true)
@Composable
fun QuestionItemPreview() {
    QuestionItem(
        questionPosition = 0,
        questionTitle = "What is Your Name ?",
        onQuestionNameEnter = { },
        options = mutableListOf(
            OptionsUiModel(
                optionId = 1,
                text = "Option 1",
                isSelected = false,
                optionImage = "",
                lastUpdate = System.currentTimeMillis()
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
        deleteOption = {},
        openQuestionImageBottomSheet = {},
        openOptionImageBottomSheet = { _, _ -> },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun QuestionItem(
    questionPosition: Int,
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
    openOptionImageBottomSheet: (Int, Int) -> Unit,
    deleteOption: (Int) -> Unit,
    openQuestionImageBottomSheet: (Int) -> Unit,

    ) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).fillMaxWidth().clip(RoundedCornerShape(10.dp))
    ) {
        Box(modifier = Modifier
            .background(Color(0xFF3799ED))

            // .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(start = 5.dp),
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
                            Text(text = "Enter Question Name :")
                        },
                        leadingIcon = {
                            Text(
                                text = "${questionPosition + 1}.",
                                fontSize = 18.sp,
                                modifier = Modifier.offset(x = 10.dp, y = -3.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                keyboardController?.hide()
                                openQuestionImageBottomSheet(questionPosition)
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_insert_photo),
                                    contentDescription = "Clear",
                                    modifier = Modifier.width(20.dp)
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Justify)
                    )

                    QuestionImage(question)

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
                                openOptionImageBottomSheet(questionPosition, optionPosition)
                            },
                            deleteOption = {
                                deleteOption(optionPosition)
                            }
                        )
                    }

                    Button(
                        modifier = Modifier.padding(horizontal = 12.dp),
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
                                    keyboardController?.hide()
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
    }


}

@Composable
fun QuestionImage(question: QuestionsUiModel) {
    if (question.questionImage.isNotEmpty()) {
        Image(
            painter = rememberAsyncImagePainter(model = Uri.parse(question.questionImage)),
            contentDescription = "questionImage",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}


