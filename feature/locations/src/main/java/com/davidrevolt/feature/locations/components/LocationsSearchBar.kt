package com.davidrevolt.feature.locations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidrevolt.core.model.Location


private const val MEDIUM_BLUE = 0xFF1976D2
private const val MEDIUM_INDIGO = 0xFF3F51B5
private const val DEEP_INDIGO = 0xFF1A237E
private const val PALE_LAVENDER = 0xFFE6E6FA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onClearSearchClick: () -> Unit,
    onCancelSearchClick: () -> Unit,
    // Options for search results
    searchResults: List<Location>,
    onSearchResultClick: (Location) -> Unit,
    isSearching: Boolean,
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearchClick,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                placeholder = {
                    Text(
                        "Search locations...",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(MEDIUM_INDIGO).copy(alpha = 0.7f)
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = null,
                        tint = Color(MEDIUM_BLUE)
                    )
                },
                trailingIcon = {
                    when {
                        query.isNotEmpty() -> {
                            IconButton(onClick = onClearSearchClick) {
                                Icon(
                                    Icons.Rounded.Clear,
                                    contentDescription = "Clear search",
                                    tint = Color(MEDIUM_BLUE)
                                )
                            }
                        }

                        expanded /*&& query.isEmpty() */ -> {
                            IconButton(onClick = onCancelSearchClick) {
                                Icon(
                                    Icons.Rounded.KeyboardArrowUp,
                                    contentDescription = "Cancel search",
                                    tint = Color(MEDIUM_BLUE)
                                )
                            }
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(DEEP_INDIGO),
                    unfocusedTextColor = Color(DEEP_INDIGO),
                    cursorColor = Color(MEDIUM_BLUE)
                )
            )
        },
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = SearchBarDefaults.colors(containerColor = Color(PALE_LAVENDER))
    ) {
        when {
            isSearching -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        color = Color(0xFF1976D2), // Medium Blue
                        strokeWidth = 4.dp,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            searchResults.isEmpty() && query.isNotEmpty() -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No locations found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(MEDIUM_INDIGO).copy(alpha = 0.7f),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            else -> {
                LazyColumn {
                    items(items = searchResults, key = { it.id }) { locationResult ->
                        LocationSearchResultItem(
                            modifier = Modifier.animateItem(),
                            location = locationResult,
                            onClick = { onSearchResultClick(locationResult) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun LocationSearchResultItem(
    modifier: Modifier,
    location: Location,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(Color(PALE_LAVENDER))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            tint = Color(MEDIUM_BLUE).copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(DEEP_INDIGO), // Deep Indigo
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${location.administrativeArea}, ${location.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(MEDIUM_INDIGO).copy(alpha = 0.7f)
            )
        }
    }
}