package com.rach.firmmanagement.necessaryItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.red

@Composable
fun RadioButton1(
    isSelected: Boolean,
    title: String,
    onValueChange: (String) -> Unit
) {




    Row(
    ) {
        RadioButton(
            selected = isSelected, onClick = {

                onValueChange(title)

            },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.Blue,
                unselectedColor = Color.Black
            )
        )
        Text(
           text = title,
            modifier = Modifier.align(CenterVertically)
        )

    }

}

@Preview(showBackground = true)
@Composable
fun RadioPreview() {
    FirmManagementTheme {
        RadioButton1(
            isSelected = true,
            title = "Vishal",
            onValueChange = {}
        )
    }
}