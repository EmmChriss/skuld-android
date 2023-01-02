package com.lab.skuld.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query

@Composable
fun <T, F> rememberLiveArray(
    modelClass: Class<T>,
    query: Query,
    filterMap: (T) -> F?,
    onDataChanged: () -> Unit = {},
    onChildChanged : (
        type: ChangeEventType,
        snapshot: DocumentSnapshot,
        newIndex: Int,
        oldIndex: Int) -> Unit
    = { _, _, _, _ -> },
    onError : (FirebaseFirestoreException) -> Unit
    = { _ -> }
) : List<F> {
    var documents : List<F> by remember { mutableStateOf(listOf()) }
    fun updateDocuments(iter: Iterable<T>) {
        documents = iter
            .mapNotNull(filterMap)
            .toList()
    }

    DisposableEffect(Unit) {
        val arr = FirestoreArray(query, ClassSnapshotParser(modelClass))
        val listener = object : ChangeEventListener {
            override fun onChildChanged(
                type: ChangeEventType,
                snapshot: DocumentSnapshot,
                newIndex: Int,
                oldIndex: Int
            ) {
                onChildChanged(type, snapshot, newIndex, oldIndex)
                updateDocuments(arr.asIterable())
            }

            override fun onDataChanged() {
                onDataChanged()
                updateDocuments(arr.asIterable())
            }

            override fun onError(e: FirebaseFirestoreException) {
                onError(e)
            }
        }

        arr.addChangeEventListener(listener)

        onDispose {
            arr.removeChangeEventListener(listener)
        }
    }

    return documents
}
