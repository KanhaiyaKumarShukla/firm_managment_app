package com.rach.firmmanagement.testing

import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rach.firmmanagement.employee.drawCell
import com.rach.firmmanagement.ui.theme.FirmManagementTheme

@Composable
fun CanvasConcept() {

    Canvas(modifier = Modifier.fillMaxSize()) {

        val height= size.height

        drawTable()


    }

}

fun DrawScope.drawTable(){

    val height = 100.dp.toPx()
    val width = 100.dp.toPx()

    val x = 50f
    val y = 50f

    for (i in 0..2){
        for (j in 0..2){

            val startx = x + j * (width)
            val startY = y + i * (height)
            drawCell(x = startx , y = startY , height = height, width = width, text = "Cell ${i + 1}, ${j + 1}")

        }
    }

}

@Preview(showBackground = true)
@Composable
fun CanvasConceptPreview() {
    FirmManagementTheme {
        CanvasConcept()
    }
}