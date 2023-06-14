@file:Suppress("DEPRECATION")

package extrydev.app.yuknaklet.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import extrydev.app.yuknaklet.model.CreateWorkFromWhere
import extrydev.app.yuknaklet.model.CreateWorkToWhere
import extrydev.app.yuknaklet.model.PlaceAutocompletePrediction
import extrydev.app.yuknaklet.viewmodel.CreateWorkViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkPage(navController: NavController, viewModel: CreateWorkViewModel = hiltViewModel() ) {
    var fromLat: Double
    var fromLong: Double
    var fromCity: String
    var fromDistrict: String
    val fromDetail = remember {mutableStateOf(TextFieldValue(""))}
    var toLat: Double
    var toLong: Double
    var toCity: String
    var toDistrict: String
    val toDetail = remember {mutableStateOf(TextFieldValue(""))}
    val context = LocalContext.current
    val geocoder = Geocoder(context, Locale.getDefault())
    var fromAddress: MutableList<Address>?
    var toAddress: MutableList<Address>?
//    val fromWhereFromViewModel = viewModel.fromWhere.observeAsState().value
//    val toWhereFromViewModel = viewModel.toWhere.observeAsState().value
    Scaffold(topBar = {

    }, content =
    {
        Column(modifier = Modifier
            .fillMaxSize()) {
            SearchBar(viewModel, navController)
            MapViewContainer(viewModel = viewModel)
            Button(onClick = { viewModel.resetLocations() }) {
                Text("Pinleri Temizle")
            }
            Button(onClick = {
                if(viewModel.fromLocation.value!=null && viewModel.toLocation.value != null && fromDetail.value.toString() != "" && toDetail.value.toString() != "")
                {
                    fromAddress = geocoder.getFromLocation(
                        viewModel.fromLocation.value?.latitude ?: 0.0,
                        viewModel.fromLocation.value?.longitude?:0.0,
                        1)
                    fromLat = fromAddress!![0].latitude
                    fromLong = fromAddress!![0].longitude
                    fromCity = fromAddress!![0].locality
                    fromDistrict = fromAddress!![0].subLocality
                    toAddress = geocoder.getFromLocation(
                        viewModel.fromLocation.value?.latitude ?: 0.0,
                        viewModel.fromLocation.value?.longitude?:0.0,
                        1)
                    toLat = toAddress!![0].latitude
                    toLong = toAddress!![0].longitude
                    toCity = toAddress!![0].locality
                    toDistrict = toAddress!![0].subLocality
                    val fromWhere = CreateWorkFromWhere(fromLat, fromLong, fromCity, fromDistrict, fromDetail.toString())
                    val toWhere = CreateWorkToWhere(toLat, toLong, toCity, toDistrict, toDetail.toString())
                    navController.navigate("createWorkSecondPage/{$fromWhere}/{$toWhere}")
                }
                else
                {
                    Toast.makeText(context,"Boş alan bırakmayınız!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Devam")
            }
        }
    }, bottomBar = {})
}

@Composable
fun MapViewContainer(viewModel: CreateWorkViewModel) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle(context)
    val callback = rememberMapViewOnReady(mapView, viewModel)
    AndroidView({ mapView }) {
        mapView.getMapAsync(callback)
    }
}
fun rememberMapViewOnReady(mapView: MapView, viewModel: CreateWorkViewModel): OnMapReadyCallback {
    return OnMapReadyCallback { googleMap ->
        viewModel.map = googleMap
        googleMap.setOnMapClickListener { latLng ->
            viewModel.onLocationSelected(latLng, googleMap)
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(viewModel.currentLocation))
    }
}
@Composable
fun rememberMapViewWithLifecycle(context: Context): MapView {
    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            onStart()
            onResume()
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(mapViewLifecycleObserver(mapView))
        onDispose {
            lifecycle.removeObserver(mapViewLifecycleObserver(mapView))
            mapView.onDestroy()
        }
    }

    return mapView
}

fun mapViewLifecycleObserver(mapView: MapView) = object : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        mapView.onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        mapView.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        mapView.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        mapView.onStop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mapView.onDestroy()
    }
}
@Composable
fun SearchBar(viewModel: CreateWorkViewModel, navController: NavController) {
    var fromPlace by remember { mutableStateOf<Place?>(null) }
    var toPlace by remember { mutableStateOf<Place?>(null) }
    Column {
        SearchAndSelectPlace("Nereden", fromPlace, navController, viewModel) { selectedPlace ->
            fromPlace = selectedPlace
        }
        Spacer(Modifier.height(16.dp))
        SearchAndSelectPlace2("Nereye", toPlace, viewModel) { selectedPlace ->
            toPlace = selectedPlace
        }
    }
}

@Composable
fun SearchAndSelectPlace(label: String, selectedPlace: Place?,navController: NavController, viewModel: CreateWorkViewModel, onPlaceSelected: (Place) -> Unit) {
    val searchResults = viewModel.searchResults.observeAsState(listOf())
    var text by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var lat by remember { mutableDoubleStateOf(0.0) }
    var long by remember { mutableDoubleStateOf(0.0) }
    var district by remember { mutableStateOf("") }
    SearchBox(
        modifier = Modifier.padding(16.dp),
        searchResults = searchResults.value,
        text = text,
        onTextChange = { text = it },
    ) {
        city = it.structured_formatting.main_text //problem
        Log.d("onur","city: $city")
        val id =it.place_id
        viewModel.viewModelScope.launch {
            viewModel.getPlace(id)
        }
    }
    val selectedPlaceDetails = viewModel.selectedPlaceDetails.observeAsState()
    LaunchedEffect(selectedPlaceDetails.value) {
        selectedPlaceDetails.value?.let { placeDetails ->
            lat = placeDetails.geometry.location.lat
            long = placeDetails.geometry.location.lng
//            district = placeDetails.address_components.first().long_name
//            val x = placeDetails.address_components[1].long_name
//            val y = placeDetails.address_components[2].long_name
//            val z = placeDetails.address_components[3].long_name
//            Log.d("onur","adres: lat: $lat, long: $long, city: $city, district: $district, x: $x, y: $y, z: $z")
//            viewModel.addFromWhere(lat,long,city,district,"a")
            viewModel.onLocationSelected(LatLng(lat, long), viewModel.map!!)
            viewModel.clearSearchResults()
        }
    }
}
@Composable
fun SearchAndSelectPlace2(label: String, selectedPlace: Place?, viewModel: CreateWorkViewModel, onPlaceSelected: (Place) -> Unit) {
    val searchResults = viewModel.searchResults2.observeAsState(listOf())
    var text by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var lat by remember { mutableDoubleStateOf(0.0) }
    var long by remember { mutableDoubleStateOf(0.0) }
    var district by remember { mutableStateOf("") }
    SearchBox2(
        modifier = Modifier.padding(16.dp),
        searchResults = searchResults.value,
        text = text,
        onTextChange = { text = it },
    ) {
        city = it.structured_formatting.main_text   //problem
        val id =it.place_id
        viewModel.viewModelScope.launch {
            viewModel.getPlace2(id)
        }
    }
    val selectedPlaceDetails = viewModel.selectedPlaceDetails2.observeAsState()
    LaunchedEffect(selectedPlaceDetails.value) {
        selectedPlaceDetails.value?.let { placeDetails ->
            lat = placeDetails.geometry.location.lat
            long = placeDetails.geometry.location.lng
//            district = placeDetails.address_components.first().long_name
//            val x = placeDetails.address_components[1].long_name
//            val y = placeDetails.address_components[2].long_name
//            val z = placeDetails.address_components[3].long_name
//            Log.d("onur","adres: lat: $lat, long: $long, city: $city, district: $district, x: $x, y: $y, z: $z")
//            viewModel.addToWhere(lat,long,city,district,"")
            viewModel.onLocationSelected(LatLng(lat, long), viewModel.map!!)
            viewModel.clearSearchResults2()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    searchResults: List<PlaceAutocompletePrediction>,
    text: String,
    onTextChange: (String) -> Unit,
    onItemSelected: (PlaceAutocompletePrediction) -> Unit
) {
    val viewModel: CreateWorkViewModel = hiltViewModel()
    Column(modifier = modifier) {
        TextField(
            value = text,
            onValueChange = {
                onTextChange(it)
                viewModel.searchPlaces(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search") },
            trailingIcon = {
                IconButton(onClick = {
                    onTextChange("")
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear text")
                }
            },
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 200.dp)
        ) {
            items(searchResults) { prediction ->
                TextButton(onClick = {
                    onTextChange(prediction.description)
                    onItemSelected(prediction)
                }) {
                    Text(prediction.description)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox2(
    modifier: Modifier = Modifier,
    searchResults: List<PlaceAutocompletePrediction>,
    text: String,
    onTextChange: (String) -> Unit,
    onItemSelected: (PlaceAutocompletePrediction) -> Unit
) {
    val viewModel: CreateWorkViewModel = hiltViewModel()
    Column(modifier = modifier) {
        TextField(
            value = text,
            onValueChange = {
                onTextChange(it)
                viewModel.searchPlaces2(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search") },
            trailingIcon = {
                IconButton(onClick = {
                    onTextChange("")
                }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear text")
                }
            },
            singleLine = true
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 200.dp)
        ) {
            items(searchResults) { prediction ->
                TextButton(onClick = {
                    onTextChange(prediction.description)
                    onItemSelected(prediction)
                }) {
                    Text(prediction.description)
                }
            }
        }
    }
}