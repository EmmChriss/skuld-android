package com.lab.skuld.ui.screens

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lab.skuld.model.CreateEvent
import com.lab.skuld.model.Event
import com.lab.skuld.model.dateTimeToTimestamp
import com.lab.skuld.ui.Screen
import com.lab.skuld.ui.UIContextViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Date

fun eventToDocument(event: Event): Document {
    val lines = event.contents?.split("\n") ?: emptyList()
    val documentContents = lines.mapIndexed { index, line ->
        val parts = line.split(" ", limit = 2)
        val header = parts.getOrNull(0) ?: ""
        val value = parts.getOrNull(1) ?: ""
        TextData(index, header, value)
    }
    return Document(header = event.title, documentContents = documentContents)
}

fun documentToEvent(document: Document): Event {
    val contents = document.documentContents.joinToString("\n") { data -> "${data.header} ${data.value}" }
    return Event(id = "", startDate = null, endDate = null, title = document.header, checked = true, contents = contents)
}

fun documentToEventNoID(document: Document): CreateEvent {
    val contents = document.documentContents.joinToString("\n") { data -> "${data.header} ${data.value}" }
    return CreateEvent( startDate = null, endDate = null, title = document.header, checked = true, contents = contents)
}
data class MaybeEventNoID (
    var startDate: Timestamp? = null,
    var endDate: Timestamp? = null,
    val title: String? = null,
    val checked: Boolean? = null,
    val contents: String? = null,
)
fun documentToEventNoID2(document: Document): MaybeEventNoID {
    val contents = document.documentContents.joinToString("\n") { data -> "${data.header} ${data.value}" }
    return MaybeEventNoID( startDate = null, endDate = null, title = document.header, checked = true, contents = contents)
}
fun now(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

val emptyEventOrTask = Event(id = "", startDate = now(), endDate = now(), title = "New Task/Event", checked = true, contents = "")

data class TextData(var index: Int, var header: String, var value: String)
data class Document(var header: String= "", var image: Painter? = null, var  documentContents: List<TextData> = listOf(),var checked: Boolean? = null)








@Composable
fun ShowNewNoteScreen(event: Event = emptyEventOrTask){
    var isNewTask = false
    if(event.id == ""){
        isNewTask = true
    }
    val document = eventToDocument(event)
    val viewModel: UIContextViewModel = viewModel()


    var newHeader by remember { mutableStateOf("") }
    var documentTitle by remember { mutableStateOf("") }
    val textElementsValues = remember { mutableStateListOf<TextData>() }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val preList = mutableListOf<TextData>()
    if(document.documentContents.isNotEmpty() and textElementsValues.isEmpty()) {

        for(element in document.documentContents){
            preList.add(element)
        }
        textElementsValues += preList
    }

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
        .fillMaxWidth()
        .defaultMinSize(minHeight = 500.dp)
    ) {

        @Composable
        fun NewElementDialog() {
            var elementHeader by remember { mutableStateOf("") }
            var isDialogVisible by remember { mutableStateOf(false) }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = { isDialogVisible = true },
                    content = { Text("Add element") },
                )
            }
            if (isDialogVisible) {
                MyAlertDialog(
                    title = {
                        Text(
                            text = "New element header",
                            style = MaterialTheme.typography.h6,
                        )
                    },
                    content = {
                        OutlinedTextField(
                            value = elementHeader,
                            onValueChange = { elementHeader = it },
                            label = { Text("header") },
                        )
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { isDialogVisible = false },
                            content = { Text("nevermind", color = MaterialTheme.colors.onSecondary) },
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                newHeader = elementHeader

                                textElementsValues.add(TextData(textElementsValues.size, elementHeader, ""))
                                elementHeader = ""
                                isDialogVisible = false
                            },
                            content = { Text("OK") },
                        )
                    },
                    onDismiss = {
                        isDialogVisible = false
                    },
                )
            }
        }
        @Composable
        fun TextElement(textData: TextData) {
            val index = textData.index
            val header = textData.header
            val value = textData.value
            if(index>-1) {
                Text(header, modifier = Modifier.padding(5.dp), fontSize = 20.sp, color = MaterialTheme.colors.onPrimary)
                TextField(
                    value = value,
                    onValueChange = {
                        textElementsValues[index] = textElementsValues[index].copy(value = it)
                    },
                    label = { Text("Content", color = MaterialTheme.colors.onBackground) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp)),

                )
                TextButton(
                    onClick = {
                        textElementsValues[index] = textElementsValues[index].copy(index = -1)
                    },
                    content = {
                        Text(
                            "Delete element",
                            modifier = Modifier.padding(2.dp),
                            fontSize = 10.sp,
                            color = MaterialTheme.colors.onBackground

                        )
                    },
                )
            }
        }




        TextField(
            value = documentTitle,
            onValueChange = {
                documentTitle = it
            },
            label = { Text("Title", color = MaterialTheme.colors.onBackground) },
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.padding(10.dp))

        for (data in textElementsValues)
         {
             TextElement(textData = data)
             Spacer(modifier = Modifier.padding(5.dp))
         }



        NewElementDialog()
        Spacer(modifier = Modifier.padding(10.dp))
        Text("select dates(optional)", modifier = Modifier.padding(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Column (verticalArrangement = Arrangement.Center){
                startDate = datePickerButton("Start date")
                Spacer(modifier = Modifier.padding(1.dp))
                Text(startDate)
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Column (verticalArrangement = Arrangement.Center){
                endDate = datePickerButton("End date")
                Spacer(modifier = Modifier.padding(1.dp))
                Text(endDate)
            }
            //startDate = datePickerButton("Start date")
            //endDate = datePickerButton("End date")
        }
        Spacer(modifier = Modifier.padding(10.dp))
        var saveText = "Save"
        Button(onClick = {


            document.header = documentTitle
            document.documentContents = textElementsValues



            saveText = "Save"
            if(isNewTask){
                document.checked = false
                var eventNoID = documentToEventNoID2(document)
                //eventNoID.startDate = //startDate
                //eventNoID.endDate = endDate
                var dateSpliced = startDate.split("-")
                eventNoID.startDate = dateTimeToTimestamp(LocalDateTime(dateSpliced[0].toInt(), dateSpliced[1].toInt(), dateSpliced[2].toInt(), 1, 1))
                dateSpliced = endDate.split("-")
                eventNoID.endDate = dateTimeToTimestamp(LocalDateTime(dateSpliced[0].toInt(), dateSpliced[1].toInt(), dateSpliced[2].toInt(), 1, 1))


                Firebase.firestore
                    .collection("users/data/${Firebase.auth.currentUser!!.uid}")
                    .document()
                    .set(eventNoID)
                    .addOnSuccessListener {
                    viewModel.nav.push(Screen.TaskP(documentToEvent(document)))
                    }
                    .addOnFailureListener {
                        saveText = "Upload Failed"
                    }
            }else{
                val documentUp = documentToEventNoID(document)
                //documentUp.startDate = startDate
                //documentUp.endDate = endDate
                Firebase.firestore
                    .collection("users/data/${Firebase.auth.currentUser!!.uid}")
                    .document(event.id)
                    .set(documentUp)
                    .addOnSuccessListener {
                        viewModel.nav.push(Screen.TaskP(documentToEvent(document)))
                    }
                    .addOnFailureListener {
                        saveText = "Upload Failed"
                    }
            }
        }) {
            Text(saveText)
        }


    }
}


@Composable
fun datePickerButton(buttonText: String): String{

    val mContext = LocalContext.current

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // store date in string format
    val mDate = remember { mutableStateOf("") }

        val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mYear-${mMonth+1}-$mDayOfMonth"
        }, mYear, mMonth, mDay
    )


        Button(onClick = {
            mDatePickerDialog.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant) ) {
            Text(text = buttonText, color = MaterialTheme.colors.onSecondary)
        }
    //}
    return mDate.value

}
@Composable
fun MyAlertDialog(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    title.invoke()
                    Spacer(Modifier.size(16.dp))
                    content.invoke()
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    dismissButton.invoke()
                    confirmButton.invoke()
                }
            }
        }
    }
}




