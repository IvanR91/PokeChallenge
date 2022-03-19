package com.example.pokechallenge.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    buttonLabel: String,
    isEnable: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box(modifier = modifier.width(80.dp)) {
        if (!isLoading) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
                enabled = isEnable,
                onClick = onClick
            ) {
                Text(
                    text = buttonLabel.uppercase(),
                    maxLines = 1,
                    color = if (isEnable) Color.Black else Color.Gray
                )
            }
        } else {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = Color.Blue,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
fun LoadingButtonEnablePreview() {
    MaterialTheme {
        LoadingButton(
            buttonLabel = "Search",
            isEnable = true,
            isLoading = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun LoadingButtonDisablePreview() {
    MaterialTheme {
        LoadingButton(
            buttonLabel = "Search",
            isEnable = false,
            isLoading = false,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun LoadingButtonLoadingPreview() {
    MaterialTheme {
        LoadingButton(
            buttonLabel = "Search",
            isEnable = false,
            isLoading = true,
            onClick = {}
        )
    }
}
