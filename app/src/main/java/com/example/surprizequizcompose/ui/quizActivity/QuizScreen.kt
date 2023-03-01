package com.example.surprizequizcompose.ui.quizActivity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.surprizequizcompose.R
import com.example.surprizequizcompose.ui.theme.button_corner
import com.example.surprizequizcompose.ui.theme.primary_white
import com.example.surprizequizcompose.ui.theme.quizContentBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

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
        getOptionImageFromGallery = { _, _, _ -> }
    ) { _, _ -> }
}

@OptIn(ExperimentalMaterialApi::class)
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
    deleteOption: (Int, Int) -> Unit
) {
    requestPermission()
    val questionForSetAnswerKey: MutableState<Int?> = remember { mutableStateOf(null) }
    val context = LocalContext.current
    val imageUri: Uri? = initTempUri(context)
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    val imageUriState = remember { mutableStateOf<Uri?>(null) }

    val requestQuestionImageCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (imageUri != null) {
                capturedImageUri = imageUri

            }
        }

    val requestOptionImageCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (imageUri != null) {
                capturedImageUri = imageUri
            }
        }

    val requestQuestionImageFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUriState.value = uri
            questionForSetAnswerKey.value?.let { it1 ->
                if (uri != null) {
                    getQuestionImageFromGallery(it1,uri)
                }
            }
        }

    val requestOptionImageFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUriState.value = uri
        }


    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            if (questionForSetAnswerKey.value != null) {
                BottomSheetContent(
                    questionPosition = questionForSetAnswerKey.value!!,
                    question = question[questionForSetAnswerKey.value!!],
                    onOptionSelected = { questionPosition, optionPosition ->
                        onOptionSelected(questionPosition, optionPosition)
                    }
                )
            }

            GetImageBottomSheet(
                getImageFromCamera = {
                    requestQuestionImageCamera.launch(imageUri)
                    questionForSetAnswerKey.value?.let {
                        if (imageUri != null) {
                            getQuestionImageFromGallery(it,imageUri)
                        }
                    }
                },
                getImageFromGallery = {
                    requestQuestionImageFromGallery.launch("image/*")
                }
            )
        },
        scaffoldState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TopBar()
                Column(
                    modifier = Modifier
                        .background(quizContentBackground)
                        .weight(1f)
                ) {
                    QuizContent(
                        quizTitle = quizTitle,
                        onQuizTitleEnter = onQuizTitleEnter,
                        onQuestionNameEnter = onQuestionNameEnter,
                        question = question,
                        onOptionSelected = onOptionSelected,
                        addQuestion = addQuestion,
                        addOption = addOption,
                        onOptionEnter = onOptionEnter,
                        addQuestionBelow = addQuestionBelow,
                        copyQuestionBelow = copyQuestionBelow,
                        deleteQuestion = deleteQuestion,
                        setAnswerKey = { questionId ->
                            questionForSetAnswerKey.value = questionId
                            coroutineScope.launch {
                                if (sheetState.bottomSheetState.isCollapsed)
                                    sheetState.bottomSheetState.expand()
                            }
                        },
                        getQuestionImageFromGallery = { questionId ->
                            requestQuestionImageFromGallery.launch("image/*")
                            imageUriState.value?.let { getQuestionImageFromGallery(questionId, it) }
                        },
                        getOptionImageFromGallery = { optionId, questionId ->
                            requestOptionImageFromGallery.launch("image/*")
                            imageUriState.value.let {
                                it?.let { it1 ->
                                    getOptionImageFromGallery(
                                        optionId, questionId,
                                        it1
                                    )
                                }
                            }
                        },
                        deleteOption = {questionId, optionId ->
                            deleteOption(questionId, optionId)
                        },
                        openQuestionImageBottomSheet = {questionId->
                            questionForSetAnswerKey.value = questionId
                            coroutineScope.launch {
                                if (sheetState.bottomSheetState.isCollapsed)
                                    sheetState.bottomSheetState.expand()
                            }

                        }
                    )
                }
                BottomBar()
            }
        },
        sheetPeekHeight = 0.dp,
        sheetBackgroundColor = Color.LightGray
    )
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestPermission() {
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val galleryPermission = rememberMultiplePermissionsState(
        permissions = permissions.toList()
    )

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    galleryPermission.launchMultiplePermissionRequest()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })
}

fun initTempUri(context: Context): Uri? {
    val tempImagesDir = File(context.filesDir, "temp_images")
    tempImagesDir.mkdir()
    val tempImage = File(tempImagesDir, "${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "com.example.surprizequizcompose.provider", tempImage)
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
    QuizContent(
        quizTitle = "",
        onQuizTitleEnter = {},
        onQuestionNameEnter = { _, _ -> },
        question = mutableListOf(),
        onOptionSelected = { _, _ -> },
        addQuestion = {},
        addOption = { },
        onOptionEnter = { _, _, _ -> },
        addQuestionBelow = {},
        copyQuestionBelow = { _, _ -> },
        deleteQuestion = {},
        setAnswerKey = {},
        getQuestionImageFromGallery = {},
        getOptionImageFromGallery = { _, _ -> },
        deleteOption = {_,_->},
        openQuestionImageBottomSheet = {}
    )
}

@Composable
fun QuizContent(
    quizTitle: String?,
    onQuizTitleEnter: (String) -> Unit,
    onQuestionNameEnter: (Int, String) -> Unit,
    question: List<QuestionsUiModel>,
    onOptionSelected: (Int, Int) -> Unit,
    addQuestion: () -> Unit,
    addOption: (Int) -> Unit,
    onOptionEnter: (Int, Int, String) -> Unit,
    addQuestionBelow: (Int) -> Unit,
    copyQuestionBelow: (Int, QuestionsUiModel) -> Unit,
    deleteQuestion: (Int) -> Unit,
    setAnswerKey: (Int) -> Unit,
    getQuestionImageFromGallery: (Int) -> Unit,
    getOptionImageFromGallery: (Int, Int) -> Unit,
    deleteOption: (Int, Int) -> Unit,
    openQuestionImageBottomSheet:(Int)->Unit
) {
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn {
                itemsIndexed(question) { questionPosition, question ->
                    question.options?.let { option ->
                        QuestionItem(
                            questionPosition = questionPosition,
                            question = question,
                            questionId = question.questionId,
                            questionTitle = question.questionName.toString(),
                            options = option,
                            onQuestionNameEnter = { questionName ->
                                onQuestionNameEnter(questionPosition, questionName)
                            },
                            onOptionSelected = { optionId ->
                                onOptionSelected(questionPosition, optionId)
                            },
                            addOption = {
                                addOption(questionPosition)
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
                                deleteQuestion(questionPosition)
                            },
                            setAnswerKey = {
                                setAnswerKey(questionPosition)
                            },
                            getQuestionImageFromGallery = {
                                getQuestionImageFromGallery(questionPosition)
                            },
                            getOptionImageFromGallery = { optionId ->
                                getOptionImageFromGallery(questionPosition, optionId)
                            },
                            deleteOption = { optionId ->
                                deleteOption(questionPosition, optionId)
                            },
                            openQuestionImageBottomSheet = {questionId->
                                openQuestionImageBottomSheet(questionId)
                            }
                        )
                    }
                }
            }
        }
        Button(
            onClick = {
                addQuestion()
            },
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
    Card() {
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
    Box(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "${questionPosition + 1},${question.questionName}",
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
}

@Preview(showBackground = true)
@Composable
fun BottomSheetSetAnswerKeyItemPreview() {
    BottomSheetSetAnswerKeyItem(
        OptionsUiModel(
            optionId = 1,
            text = "Option 1",
            optionImage = "",
            isSelected = false
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

@Preview(showBackground = true)
@Composable
fun GetImageBottomSheetPreview() {
    GetImageBottomSheet(
        getImageFromGallery = {},
        getImageFromCamera = {}
    )
}

@Composable
fun GetImageBottomSheet(
    getImageFromGallery: () -> Unit,
    getImageFromCamera: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        IconButton(onClick = {
            getImageFromGallery()
        }) {
            Icon(
                Icons.Filled.FileOpen,
                contentDescription = "Gallery",
                modifier = Modifier.size(100.dp)
            )
        }
        IconButton(onClick = {
            getImageFromCamera()
        }) {
            Icon(
                Icons.Filled.Camera,
                contentDescription = "Camera",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}
