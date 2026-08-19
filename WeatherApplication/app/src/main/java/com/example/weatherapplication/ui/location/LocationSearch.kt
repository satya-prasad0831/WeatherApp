package com.example.weatherapplication.ui.location

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapplication.data.remote.LocationResult
import com.example.weatherapplication.viewmodel.LocationViewModel
import androidx.compose.foundation.layout.heightIn

@Composable
fun LocationSearch(
    onLocationSelected: (LocationResult) -> Unit,
    viewModel: LocationViewModel = hiltViewModel()
) {

    var query by remember {
        mutableStateOf("")
    }

    val locations by viewModel.locations.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it

                viewModel.searchLocation(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search city")
            },
            singleLine = true
        )

        if (isLoading) {

            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )

        } else {

            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp)
            ) {

                items(locations) { location ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLocationSelected(location)

                                query = ""
                                viewModel.clearLocations()
                            }
                            .padding(16.dp)
                    ) {

                        Column {

                            Text(
                                text = location.name
                            )

                            Text(
                                text = "${location.admin1 ?: ""}, ${location.country ?: ""}"
                            )
                        }
                    }
                }
            }
        }
    }
}