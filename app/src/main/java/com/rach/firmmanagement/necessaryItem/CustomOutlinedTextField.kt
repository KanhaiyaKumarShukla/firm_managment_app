package com.rach.firmmanagement.necessaryItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import java.lang.Error
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.res.painterResource
import com.rach.firmmanagement.R

@Composable
fun CustomOutlinedTextFiled(
    value : String,
    onValueChange : (String) -> Unit,
    label : String,
    singleLine :Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
    readOnly:Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    colors: androidx.compose.material3.TextFieldColors = OutlinedTextFieldDefaults.colors()
){



    Column(modifier = modifier) {

        OutlinedTextField(value = value,
            onValueChange = {
                onValueChange(it)
            },
            label = { Text(text = label)},
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            colors = colors
        )

        if (isError){
            Text(text = "Please fill all field",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }


    }

}

@Preview(showBackground = true)
@Composable
fun CustomOutlinedTextPreview(){
    FirmManagementTheme {
        CustomOutlinedTextFiled(
            "",
            {},
            "Name",
            true,
            false,
            modifier = Modifier.fillMaxWidth(),
            readOnly = false
        )
    }
}