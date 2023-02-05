package com.lab.skuld.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lab.skuld.model.Event
import com.lab.skuld.ui.Screen
import com.lab.skuld.ui.UIContextViewModel
import com.lab.skuld.ui.rememberLiveArray


fun TaskToEvent(task: Task): Event {
    val event = Event(id = task.id, title = task.title, checked = task.checked, contents = task.contents, endDate = null, startDate = null)
    return event
}

class MaybeTask {
    @DocumentId
    val id = ""
    val title: String? = null
    val checked: Boolean? = null
    val contents: String? = null
}

data class Task(
    val id: String,
    val title: String,
    val checked: Boolean,
    val contents: String?
)

fun maybeToTask(maybe: MaybeTask) =
    maybe.title?.let { title ->
        maybe.checked?.let { checked ->
            Task(maybe.id, title, checked, maybe.contents)
        }
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowTasksScreen() {
    val uiContextViewModel: UIContextViewModel = viewModel()
    LaunchedEffect(Unit) {
        uiContextViewModel.loadingBar.enabled = true
    }

    val query = Firebase.firestore
        .collection("users/data/${Firebase.auth.currentUser!!.uid}")

    val documents: List<Task> = rememberLiveArray(
        MaybeTask::class.java,
        query,
        ::maybeToTask,
        onDataChanged = {
            uiContextViewModel.loadingBar.enabled = false
        }
    )

    LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        items(

            count = documents.size,
            key = { doc -> documents[doc].id }
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .animateItemPlacement()
                    .background(color = MaterialTheme.colors.primary, shape = RoundedCornerShape(10.dp))
                    .padding(1.dp)
                    .clickable { uiContextViewModel.nav.push(Screen.TaskP(TaskToEvent(documents[it]))) }
            )
            {
                Column (modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .fillMaxWidth()
                    .padding(10.dp)){
                    Text(
                        fontSize = 22.sp,
                        text = documents[it].title,
                        color = MaterialTheme.colors.onPrimary
                    )
                    Spacer(modifier = Modifier.padding(1.dp))
                    if (documents[it].contents != null) {
                        Text(
                            color = MaterialTheme.colors.onPrimary,
                            modifier = Modifier.padding(5.dp),
                            text = documents[it].contents.toString()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier
                .padding(10.dp))
        }
    }

}
