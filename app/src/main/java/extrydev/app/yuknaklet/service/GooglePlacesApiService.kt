package extrydev.app.yuknaklet.service

import extrydev.app.yuknaklet.model.AutocompleteResponse
import extrydev.app.yuknaklet.model.PlaceDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApiService {
    @GET("maps/api/place/autocomplete/json")
    suspend fun getAutocompleteResults(
        @Query("input") input: String,
        @Query("key") apiKey: String = "AIzaSyCstSUcIbbZxjQB9XMJFWZM4gj10akWrtQ"
    ): AutocompleteResponse
    @GET("maps/api/place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String = "AIzaSyCstSUcIbbZxjQB9XMJFWZM4gj10akWrtQ"
    ): PlaceDetailsResponse

}