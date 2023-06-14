package extrydev.app.yuknaklet.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import extrydev.app.yuknaklet.model.CreateWorkFromWhere
import extrydev.app.yuknaklet.model.CreateWorkToWhere
import extrydev.app.yuknaklet.model.PlaceDetails
import extrydev.app.yuknaklet.model.PlaceAutocompletePrediction
import extrydev.app.yuknaklet.repository.YukNakletRepository
import extrydev.app.yuknaklet.service.GooglePlacesApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateWorkViewModel @Inject constructor(private val yukNakletRepository: YukNakletRepository, private val service: GooglePlacesApiService) : ViewModel() {
    val errorMessage = MutableStateFlow("")
    val currentLocation = LatLng(41.015137, 28.979530)
    var map: GoogleMap? = null
    val fromLocation = MutableLiveData<LatLng?>()
    val toLocation = MutableLiveData<LatLng?>()
    private var locationMode = LocationMode.FROM
    private val markers = MutableLiveData<List<Marker>>(emptyList())
    val searchResults = MutableLiveData<List<PlaceAutocompletePrediction>>()
    val searchResults2 = MutableLiveData<List<PlaceAutocompletePrediction>>()
    val selectedPlaceDetails = MutableLiveData<PlaceDetails>()
    val selectedPlaceDetails2 = MutableLiveData<PlaceDetails>()
//    var fromWhere = MutableLiveData<CreateWorkFromWhere>()
//    val toWhere = MutableLiveData<CreateWorkToWhere>()

    fun resetLocations() {
        markers.value?.forEach { it.remove() }
        markers.value = emptyList()
        fromLocation.value = null
        toLocation.value = null
        locationMode = LocationMode.FROM
    }
    fun clearSearchResults() {
        searchResults.value = listOf()
    }
    fun clearSearchResults2() {
        searchResults2.value = listOf()
    }
//    fun addFromWhere(lat: Double, long: Double, city: String, district: String, detail: String)
//    {
//        fromWhere.value = CreateWorkFromWhere(lat, long, city, district, detail)
//    }
//    fun addToWhere(lat: Double, long: Double, city: String, district: String, detail: String)
//    {
//        toWhere.value = CreateWorkToWhere(lat, long, city, district, detail)
//    }
    fun onLocationSelected(latLng: LatLng, googleMap: GoogleMap) {
        when (locationMode) {
            LocationMode.FROM -> {
                if (fromLocation.value == null) {
                    markers.value?.firstOrNull()?.remove()
                    val marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Başlangıç yeri"))
                    fromLocation.value = latLng
                    markers.value = listOfNotNull(marker, markers.value?.getOrNull(1))
                    locationMode = LocationMode.TO
                }
            }
            LocationMode.TO -> {
                if (toLocation.value == null) {
                    markers.value?.getOrNull(1)?.remove()
                    val marker = googleMap.addMarker(MarkerOptions().position(latLng).title("Varış yeri"))
                    toLocation.value = latLng
                    markers.value = listOfNotNull(markers.value?.firstOrNull(), marker)
                }
            }
        }
    }

    enum class LocationMode {
        FROM, TO
    }
    fun createWork(fromWhere: CreateWorkFromWhere, toWhere: CreateWorkToWhere, fuel: Int, kdv: Int, price: Int, commision: Int, carType: String, category: String, description: String, image: ArrayList<String>)
    {
        viewModelScope.launch {
            val result = yukNakletRepository.postCreateWork(fromWhere, toWhere, fuel, kdv, price, commision, carType, category, description, image)
            if(result.message != null && result.message != "")
            {
                result.message.let {
                    errorMessage.value = it
                }
            }
            else
                errorMessage.value = result.message.toString()
        }
    }
    fun searchPlaces(query: String){
        viewModelScope.launch {
            val result = service.getAutocompleteResults(query)
            searchResults.value = result.predictions
        }
    }
    fun searchPlaces2(query: String){
        viewModelScope.launch {
            val result = service.getAutocompleteResults(query)
            searchResults2.value = result.predictions
        }
    }
    fun getPlace(placeId: String) {
        viewModelScope.launch {
            val result = service.getPlaceDetails(placeId)
            selectedPlaceDetails.value = result.result
        }
    }
    fun getPlace2(placeId: String) {
        viewModelScope.launch {
            val result = service.getPlaceDetails(placeId)
            selectedPlaceDetails2.value = result.result
        }
    }
}