package com.example.pokechallenge.activities.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokechallenge.compose.LoadingButton

@Composable
fun SearchBar(
    text: String,
    buttonEnable: Boolean,
    isSearching: Boolean,
    searchChangeText: (String) -> Unit,
    clickSearch: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val focusManager = LocalFocusManager.current

        TextField(
            modifier = Modifier
                .weight(1f)
                .background(color = Color.White),
            value = text,
            onValueChange = { searchChangeText(it) }
        )

        Spacer(modifier = Modifier.width(16.dp))

        LoadingButton(
            buttonLabel = "search",
            isEnable = buttonEnable,
            isLoading = isSearching,
            onClick = {
                focusManager.clearFocus()
                clickSearch()
            }
        )
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(
            text = "Text",
            buttonEnable = true,
            isSearching = false,
            searchChangeText = {},
            clickSearch = {})
    }
}
