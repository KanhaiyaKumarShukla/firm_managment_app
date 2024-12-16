package com.rach.firmmanagement.necessaryItem

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.rach.firmmanagement.R
import com.rach.firmmanagement.ui.theme.blueAcha

@Composable
fun AppBarView(
    title: String,
    onNavClick: () -> Unit = {}
) {

    val navigationIcon: (@Composable () -> Unit) = {
        IconButton(onClick = { onNavClick() }) {

            Icon(
                imageVector = Icons.Default.Menu, contentDescription = "Menu Drawer",
                tint = Color.White
            )

        }
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                color = colorResource(id = R.color.white),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        },
        elevation = 4.dp,
        backgroundColor = blueAcha,
        navigationIcon = navigationIcon
    )


}