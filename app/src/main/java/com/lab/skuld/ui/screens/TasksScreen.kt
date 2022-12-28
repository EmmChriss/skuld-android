package com.lab.skuld.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import com.firebase.ui.firestore.FirestoreArray

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.CachingSnapshotParser
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lab.skuld.ui.rememberLiveArray
import kotlinx.coroutines.tasks.await

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
    val query = Firebase.firestore
        .collection("users/data/${Firebase.auth.currentUser!!.uid}")

    val documents: List<Task> = rememberLiveArray(
        MaybeTask::class.java,
        query,
        ::maybeToTask
    )

    LazyColumn {
        items(
            count = documents.size,
            key = { doc -> documents[doc].id }
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .animateItemPlacement()
            )
            {
                Text(documents[it].title)
                if (documents[it].contents != null) {
                    Text(documents[it].contents.toString())
                }
            }
        }
    }
}
