package com.example.surprizequizcompose.ui.quizActivity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.surprizequizcompose.R
import com.example.surprizequizcompose.ui.theme.button_corner
import com.example.surprizequizcompose.ui.theme.primary_white
import com.example.surprizequizcompose.ui.theme.quizContentBackground
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch
import java.io.File

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    QuizScreen(
        quizTitle = "",
        question = mutableListOf(),
        onQuizTitleEnter = {},
        onQuestionNameEnter = { _, _ -> },
        onOptionSelected = { _, _ -> },
        addQuestion = {},
        addOption = { },
        onOptionEnter = { _, _, _ -> },
        addQuestionBelow = {},
        copyQuestionBelow = { _, _ -> },
        deleteQuestion = {},
        getQuestionImageFromGallery = { _, _ -> },
        getOptionImageFromGallery = { _, _, _ -> },
        deleteOption = { _, _ -> },
        closeApplication = {}
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@SuppressLint("PermissionLaunchedDuringComposition", "CoroutineCreationDuringComposition")
@Composable
fun QuizScreen(
    quizTitle: String?,
    question: List<QuestionsUiModel>,
    onQuizTitleEnter: (String) -> Unit,
    onQuestionNameEnter: (Int, String) -> Unit,
    onOptionSelected: (Int, Int) -> Unit,
    addQuestion: () -> Unit,
    addOption: (Int) -> Unit,
    onOptionEnter: (Int, Int, String) -> Unit,
    addQuestionBelow: (Int) -> Unit,
    copyQuestionBelow: (Int, QuestionsUiModel) -> Unit,
    deleteQuestion: (Int) -> Unit,
    getQuestionImageFromGallery: (Int, Uri) -> Unit,
    getOptionImageFromGallery: (Int, Int, Uri) -> Unit,
    deleteOption: (Int, Int) -> Unit,
    closeApplication: () -> Unit
) {
    val requestCameraPermission =
        rememberPermissionState(permission = Manifest.permission.CAMERA)

    val requestGalleryPermission =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    val questionPositionForSetAnswerKey: MutableState<Int?> = remember { mutableStateOf(null) }
    val optionPositionForSetImage: MutableState<Int?> = remember { mutableStateOf(null) }

    var capturedImage by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    val galleryImage = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current as Activity
    val imageUri: Uri? = initTempUri(context)

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )
    val bottomSheetContentState = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val requestQuestionImageCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (imageUri != null) {
                capturedImage = imageUri
            }
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        }

    val requestOptionImageCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (imageUri != null) {
                capturedImage = imageUri
            }
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        }

    val requestQuestionImageFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            galleryImage.value = uri
            questionPositionForSetAnswerKey.value?.let { questionId ->
                uri?.let { questionImage ->
                    getQuestionImageFromGallery(questionId, questionImage)
                }
            }
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        }

    val requestOptionImageFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            galleryImage.value = uri
            questionPositionForSetAnswerKey.value?.let { questionId ->
                optionPositionForSetImage.value?.let { optionId ->
                    uri?.let { optionImage ->
                        getOptionImageFromGallery(questionId, optionId, optionImage)
                    }
                }
            }
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        }

    val alertState = remember {
        mutableStateOf(false)
    }

    val scaffoldState = rememberScaffoldState()
    ShowAlert(
        alertState.value,
        onDismiss = {
            alertState.value = false
        },
        onConfirm = {
            closeApplication()
        }
    )

    BackHandler {
        alertState.value = true
    }

    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = bottomSheetState,
        sheetContent = {
            Surface(modifier = Modifier.heightIn(min = 100.dp)) {
                if (bottomSheetContentState.value == "setAnswer") {
                    BottomSheetContent(
                        questionPosition = questionPositionForSetAnswerKey.value!!,
                        question = question[questionPositionForSetAnswerKey.value!!],
                        onOptionSelected = { questionPosition, optionPosition ->
                            onOptionSelected(questionPosition, optionPosition)
                        }
                    )
                } else if (bottomSheetContentState.value == "cameraOpen") {
                    if (questionPositionForSetAnswerKey.value != null &&
                        optionPositionForSetImage.value != null) {
                        GetImageBottomSheet(
                            requestCameraPermission,
                            requestGalleryPermission,
                            getImageFromGallery = {
                                requestOptionImageFromGallery.launch("image/*")
                            },
                            getImageFromCamera = {
                                requestOptionImageCamera.launch(imageUri)
                                if (imageUri != null) {
                                    getOptionImageFromGallery(
                                        questionPositionForSetAnswerKey.value!!,
                                        optionPositionForSetImage.value!!, imageUri
                                    )
                                }
                            }
                        )
                    } else {
                        GetImageBottomSheet(
                            requestCameraPermission,
                            requestGalleryPermission,
                            getImageFromCamera = {
                                requestQuestionImageCamera.launch(imageUri)
                                questionPositionForSetAnswerKey.value?.let {
                                    if (imageUri != null) {
                                        getQuestionImageFromGallery(it, imageUri)
                                    }
                                }
                            },
                            getImageFromGallery = {
                                requestQuestionImageFromGallery.launch("image/*")
                            }
                        )
                    }
                }
            }
        },
        scrimColor = Color.Black.copy(.5f),
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        content = {
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    TopBar(
                        closeApplication = {
                            alertState.value = true
                        })
                },
                bottomBar = {
                    BottomBar()
                },
            ) {
                Column(
                    modifier = Modifier
                        .background(quizContentBackground)
                        .fillMaxSize()
                        .padding(bottom = it.calculateBottomPadding())
                ) {
                    QuizContent(
                        quizTitle = quizTitle,
                        onQuizTitleEnter = onQuizTitleEnter,
                        onQuestionNameEnter = onQuestionNameEnter,
                        question = question,
                        onOptionSelected = onOptionSelected,
                        addOption = { optionPosition ->
                            addOption(optionPosition)
                        },
                        onOptionEnter = onOptionEnter,
                        addQuestionBelow = { questionPosition ->
                            if (question.size - 1 < 4) {
                                addQuestionBelow(questionPosition)
                            } else {
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Don't Create More than Four Question..",
                                        actionLabel = ""
                                    )
                                }
//                                Toast.makeText(
//                                    context,
//                                    "Don't Create More than Four Question..",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                        },
                        copyQuestionBelow = copyQuestionBelow,
                        deleteQuestion = deleteQuestion,
                        setAnswerKey = { questionId ->
                            bottomSheetContentState.value = "setAnswer"
                            questionPositionForSetAnswerKey.value = questionId
                            if (question[questionPositionForSetAnswerKey.value!!].questionName!!.isNotEmpty() &&
                                (question[questionPositionForSetAnswerKey.value!!].options?.size
                                    ?: 0) > 1
                            ) {
                                coroutineScope.launch {
                                    if (!bottomSheetState.isVisible) {
                                        bottomSheetState.show()
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Please Enter question and option",
                                        actionLabel = ""
                                    )
                                }
//                                Toast.makeText(
//                                    context,
//                                    "Please Enter question and option",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                        },
                        deleteOption = { questionId, optionId ->
                            deleteOption(questionId, optionId)
                        },
                        openQuestionImageBottomSheet = { questionId ->
                            bottomSheetContentState.value = "cameraOpen"
                            questionPositionForSetAnswerKey.value = questionId
                            coroutineScope.launch {
                                if (!bottomSheetState.isVisible) {
                                    bottomSheetState.show()
                                }
                            }
                        },
                        openOptionImageBottomSheet = { questionId, optionId ->
                            bottomSheetContentState.value = "cameraOpen"
                            questionPositionForSetAnswerKey.value = questionId
                            optionPositionForSetImage.value = optionId

                            coroutineScope.launch {
                                if (!bottomSheetState.isVisible)
                                    bottomSheetState.show()
                            }
                        },
                        addQuestion = addQuestion
                    )
                }
            }
        }
    )
}

fun initTempUri(context: Context): Uri? {
    val tempImagesDir = File(context.filesDir, "temp_images")
    tempImagesDir.mkdir()
    val tempImage = File(tempImagesDir, "JPEG${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "com.example.surprizequizcompose.provider",
        tempImage
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar(
        closeApplication = {}
    )
}

@Composable
fun TopBar(
    closeApplication: () -> Unit
) {
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
            IconButton(onClick = {
                closeApplication()
            }) {
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
    QuizContent(
        quizTitle = "",
        onQuizTitleEnter = {},
        onQuestionNameEnter = { _, _ -> },
        question = mutableListOf(),
        onOptionSelected = { _, _ -> },
        addOption = { },
        onOptionEnter = { _, _, _ -> },
        addQuestionBelow = {},
        copyQuestionBelow = { _, _ -> },
        deleteQuestion = {},
        setAnswerKey = {},
        deleteOption = { _, _ -> },
        openQuestionImageBottomSheet = {},
        openOptionImageBottomSheet = { _, _ -> },
        addQuestion = {}
    )
}

@Composable
fun QuizContent(
    quizTitle: String?,
    onQuizTitleEnter: (String) -> Unit,
    onQuestionNameEnter: (Int, String) -> Unit,
    question: List<QuestionsUiModel>,
    onOptionSelected: (Int, Int) -> Unit,
    addOption: (Int) -> Unit,
    onOptionEnter: (Int, Int, String) -> Unit,
    addQuestionBelow: (Int) -> Unit,
    copyQuestionBelow: (Int, QuestionsUiModel) -> Unit,
    deleteQuestion: (Int) -> Unit,
    setAnswerKey: (Int) -> Unit,
    deleteOption: (Int, Int) -> Unit,
    openQuestionImageBottomSheet: (Int) -> Unit,
    openOptionImageBottomSheet: (Int, Int) -> Unit,
    addQuestion: () -> Unit
) {
    val context = LocalContext.current
    Card {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            EnterQuizTitle(
                quizTitle = quizTitle,
                onQuizTitleEnter = { quizTitle ->
                    onQuizTitleEnter(quizTitle)
                }
            )
        }
    }
    LazyColumn(
        modifier = Modifier
            .background(quizContentBackground),
    ) {
        itemsIndexed(question) { questionPosition, question ->
            question.options?.let { option ->
                QuestionItem(
                    questionPosition = questionPosition,
                    question = question,
                    questionTitle = question.questionName.toString(),
                    options = option,
                    onQuestionNameEnter = { questionName ->
                        onQuestionNameEnter(questionPosition, questionName)
                    },
                    onOptionSelected = { optionId ->
                        onOptionSelected(questionPosition, optionId)
                    },
                    addOption = {
                        if (option.size < 4) {
                            addOption(questionPosition)
                        } else {
                            Toast.makeText(
                                context,
                                "Don't Create More Than 4 options",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onOptionEnter = { optionPosition, optionText ->
                        onOptionEnter(
                            questionPosition,
                            optionPosition,
                            optionText
                        )
                    },
                    addQuestionBelow = {
                        addQuestionBelow(questionPosition)
                    },
                    copyQuestionBelow = {
                        copyQuestionBelow(questionPosition, question)
                    },
                    deleteQuestion = {
                        Log.e("delete", questionPosition.toString())
                        deleteQuestion(questionPosition)
                    },
                    setAnswerKey = {
                        setAnswerKey(questionPosition)
                    },
                    deleteOption = { optionId ->
                        deleteOption(questionPosition, optionId)
                    },
                    openQuestionImageBottomSheet = { questionId ->
                        openQuestionImageBottomSheet(questionId)
                    },
                    openOptionImageBottomSheet = { questionId, optionId ->
                        openOptionImageBottomSheet(questionId, optionId)
                    }
                )
            }
        }
        item {
            Button(
                onClick = {
                    if (question.size < 4) {
                        addQuestion()
                    } else {
                        Toast.makeText(
                            context,
                            "Don't Create More than 4 Question",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE1F5FA)
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "+ Add New Question",
                    color = button_corner
                )
            }
        }
    }
}

@Composable
fun EnterQuizTitle(
    quizTitle: String?,
    onQuizTitleEnter: (String) -> Unit
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
        value = quizTitle.toString(),
        onValueChange = { question ->
            onQuizTitleEnter(question)
        },
        placeholder = {
            Text(text = "Enter Quiz Title")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    BottomBar()
}

@Composable
fun BottomBar() {
    Card {
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

@Composable
fun BottomSheetContent(
    questionPosition: Int,
    question: QuestionsUiModel,
    onOptionSelected: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = "Select Option", textAlign = TextAlign.Justify)
        Divider()
        Text(
            text = "${questionPosition + 1}.${question.questionName}",
            fontSize = 20.sp
        )
        question.options?.forEachIndexed { optionPosition, option ->
            BottomSheetSetAnswerKeyItem(
                options = option,
                onOptionSelected = {
                    onOptionSelected(questionPosition, optionPosition)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetSetAnswerKeyItemPreview() {
    BottomSheetSetAnswerKeyItem(
        OptionsUiModel(
            optionId = 1,
            text = "Option 1",
            optionImage = "",
            isSelected = false,
            lastUpdate = 1
        ),
        onOptionSelected = {}
    )
}

@Composable
fun BottomSheetSetAnswerKeyItem(
    options: OptionsUiModel,
    onOptionSelected: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.isSelected?.let {
            RadioButton(
                selected = it,
                onClick = {
                    onOptionSelected()
                })
        }
        options.text?.let { Text(text = it) }
    }
}

//@OptIn(ExperimentalPermissionsApi::class)
//@Preview(showBackground = true)
//@Composable
//fun GetImageBottomSheetPreview() {
//    GetImageBottomSheet(
//        requestCameraState = ,
//        requestGalleryState = ,
//        getImageFromGallery = {},
//        getImageFromCamera = {}
//    )
//}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetImageBottomSheet(
    requestCameraState: PermissionState,
    requestGalleryState: PermissionState,
    getImageFromGallery: () -> Unit,
    getImageFromCamera: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Image :", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
        Divider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = {
                if (requestGalleryState.hasPermission) {
                    getImageFromGallery()
                } else {
                    requestGalleryState.launchPermissionRequest()
                }

            }) {
                Icon(
                    Icons.Filled.FileOpen,
                    contentDescription = "Gallery",
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
            }
            IconButton(onClick = {
                if (requestCameraState.hasPermission) {
                    getImageFromCamera()
                } else {
                    requestCameraState.launchPermissionRequest()
                    if (requestCameraState.hasPermission) {
                        getImageFromCamera()
                    }
                }
            }) {
                Icon(
                    Icons.Filled.Camera,
                    contentDescription = "Camera",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowAlertPreview() {
    ShowAlert(
        show = true,
        onDismiss = {},
        onConfirm = {}
    )
}

@Composable
fun ShowAlert(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text(text = "OK")
                }
            },
            onDismissRequest = {
                onDismiss()
            },
            title = {
                Text(text = "Do you want to Exit ?")
            },
            text = {
                Text(text = "Should I Continue with the requested action?")
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun SnackBarExamplePreview() {
    SnackBarExample()
}

@Composable
fun SnackBarExample() {
    val snackbarState = remember {
        SnackbarHostState()
    }
    SnackbarHost(
        hostState = snackbarState,
        snackbar = { snackbarData ->

        }
    )
}
