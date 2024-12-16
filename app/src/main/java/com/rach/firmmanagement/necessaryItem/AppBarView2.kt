package com.rach.firmmanagement.necessaryItem

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppBarView2(
    title: String,
    onClickNav: () -> Unit
) {

    val navigationIcon: (@Composable () -> Unit) =
        if (title.contains("Home")) {
            {
                IconButton(onClick = { onClickNav() }) {

                    Icon(
                        imageVector = Icons.Default.Menu, contentDescription = "Home Menu",
                        tint = Color.White
                    )


                }
            }
        } else {
            {
                IconButton(onClick = { onClickNav() }) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription = "Arrow Back",
                        tint = Color.White
                    )

                }
            }
        }

    TopAppBar(
        title = {
            Text(
                text = title, color = Color.White,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        },
        elevation = 4.dp,
        backgroundColor = Color.Red,
        navigationIcon = navigationIcon
    )

}