package com.lab.skuld.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lab.skuld.R
import com.lab.skuld.ui.Navigator
import com.lab.skuld.ui.Screen

//data class TextData(var index: Int, var header: String, var value: String)

data class Document(var header: String= "Title", var image: Painter, var  mapDocumentContents: Map<String, *>)




@Composable
fun ShowTasksScreen(navigator: Navigator) {

    ///////////
    val exampleDoc = listOf(
    TextData(0,"a", "aa"), TextData(1,"b", "bb"), TextData(3,"c", "cc")

    )


    @Composable
    fun DocumentPreview(document: Document) {
        fun findFirstString(mapDocumentContents: Map<String, *>): String{
            val filteredMap = mapDocumentContents.filter { (key, value) ->  value is String}
            if (filteredMap.isNotEmpty()) {
                return filteredMap.iterator().next().value as String
            }
            return ""
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp)
                .clickable { navigator.push(Screen.NewTask(exampleDoc)) }

        )
        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (document.image != null) {
                    document.image = painterResource(id = R.drawable.ic_launcher_background)
                }
                Image(
                    document.image,
                    contentDescription = "DocImage",
                    modifier = Modifier.clip(RoundedCornerShape(percent = 10))
                )
                Spacer(modifier = Modifier.width(15.dp))
                Column {
                    Text(document.header)
                    Text(findFirstString(document.mapDocumentContents))
                }
            }
        }

    }

    @Composable
    fun ExamplePreview() {
        DocumentPreview(
            document = Document(header = "Example header",
                image = painterResource(id = R.drawable.ic_launcher_background),
                mapDocumentContents = mutableMapOf("one" to 1, "two" to 2, "three" to 3, "four" to "Subheader - First string value in map", "five" to 5))
        )
    }





    LazyColumn {

        items(count = 20) {
            ExamplePreview()

        }
    }


}




