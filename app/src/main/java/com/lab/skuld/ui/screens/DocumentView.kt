package com.lab.skuld.ui.screens


import androidx.compose.foundation.Image
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

data class DocumentList(var mapDocuments: Map<Int, Document>)
//data class Document(var header: String= "Header", var text: String = "Text that should be shortened if it happens to be too long to fit on screen", var image: Painter)
data class Document(var header: String= "Header", var image: Painter, var  mapDocumentContents: Map<String, *>)



fun findFirstString(mapDocumentContents: Map<String, *>): String{
    val filteredMap = mapDocumentContents.filter { (key, value) ->  value is String}
    if (filteredMap.isNotEmpty()) {
        return filteredMap.iterator().next().value as String
    }
    return ""
}
/*
@Composable
fun firstDrawable(mapDocumentContents: Map<String, *>): ImageView {
    val filteredMap = mapDocumentContents.filter { (key, value) -> value is Painter }
    val value = filteredMap.iterator().next().value
    if (value != null) {
        return value as ImageView
    }

    return painterResource(id = R.drawable.image1) as ImageView
}*/

@Composable
fun DocumentPreview(document: Document) {

    Box(
        Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(10.dp)
    )
    {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (document.image != null) {
                document.image = painterResource(id = R.drawable.image1)
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
fun DocumentView() {

}

@Composable
fun DocumentListView() {

}

@Composable
fun ExamplePreview() {
    DocumentPreview(
        document = Document(header = "Example header",
        image = painterResource(id = R.drawable.image1),
        mapDocumentContents = mutableMapOf("one" to 1, "two" to 2, "three" to 3, "four" to "Subheader - First string value in map", "five" to 5))
    )
}


@Composable
fun ExampleDocumentList() {
    LazyColumn {
        items(count = 100) {
                ExamplePreview()

        }
    }
}
