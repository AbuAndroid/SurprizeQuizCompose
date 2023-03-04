package com.example.surprizequizcompose.ui.quizActivity

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.surprizequizcompose.R

@Preview(showBackground = true)
@Composable
fun OptionItemPreview() {
    OptionItem(
        optionPosition = 0,
        option = OptionsUiModel(
            optionId = 1,
            text = "Option 1",
            isSelected = false,
            optionImage = "",
            lastUpdate = 1
        ),
        onOptionSelected = { },
        onOptionEnter = {},
        getOptionImage = {},
        deleteOption = {}
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OptionItem(
    optionPosition: Int,
    option: OptionsUiModel,
    onOptionSelected: () -> Unit,
    onOptionEnter: (String) -> Unit,
    getOptionImage: () -> Unit,
    deleteOption: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            option.isSelected?.let {
                RadioButton(
                    selected = it,
                    enabled = false,
                    onClick = {
                        onOptionSelected()
                    },
                    modifier = Modifier.offset(x=20.dp,y=0.dp),
                )
            }
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal =  10.dp),
                textStyle = TextStyle(fontSize = 15.sp),
                value = option.text.toString(),
                onValueChange = { optionText ->
                    onOptionEnter(optionText)
                },
                placeholder = {
                    Text(text = "Option ")
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    placeholderColor = Color.Gray
                )
            )
            IconButton(
                modifier = Modifier.width(20.dp),
                onClick = {
                    keyboardController?.hide()
                getOptionImage()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_insert_photo),
                    contentDescription = "image"
                )
            }
            IconButton(onClick = {
                deleteOption()
            }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
        }
        OptionImage(option)
    }
}

@Composable
fun OptionImage(option: OptionsUiModel) {
    if (option.optionImage.isNotEmpty()) {
        Image(
            painter = rememberImagePainter(data = Uri.parse(option.optionImage)),
            contentDescription = "optionImage",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}