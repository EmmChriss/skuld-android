package com.lab.skuld.ui.screens

//import com.firebase.ui.firestore.FirestoreArray

/*import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.CachingSnapshotParser
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser*/
//import com.lab.skuld.ui.maybeToEvent

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lab.skuld.ui.Screen
import com.lab.skuld.ui.UiContextViewModel
import com.lab.skuld.ui.rememberLiveArray


fun TaskToEvent(task: Task): Event{
    var event = Event(id = task.id, title = task.title, checked = task.checked, contents = task.contents, endDate = null, startDate = null)
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
    val uiContextViewModel: UiContextViewModel = viewModel()
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

    LazyColumn {
        items(
            count = documents.size,
            key = { doc -> documents[doc].id }
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .animateItemPlacement()
                    .clickable { uiContextViewModel.nav.push(Screen.TaskP(TaskToEvent(documents[it]))) }
            )
            {
                Text(documents[it].title)
                Spacer(modifier = Modifier.padding(20.dp))
                if (documents[it].contents != null) {
                    Text(documents[it].contents.toString())
                }
            }
        }
    }
}
