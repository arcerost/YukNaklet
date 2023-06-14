package extrydev.app.yuknaklet.model

data class AutocompleteResponse(
    val predictions: List<PlaceAutocompletePrediction >,
    val status: String
)
data class PlaceAutocompletePrediction (
    val description: String,
    val matched_substrings: List<PlaceAutocompleteMatchedSubstring>,
    val place_id: String,
    val structured_formatting: PlaceAutocompleteStructuredFormat,
    val terms: List<PlaceAutocompleteTerm>
)
data class PlaceAutocompleteTerm(
    val offset: Double,
    val value: String
)
data class PlaceAutocompleteStructuredFormat(
    val main_text: String,
    val main_text_matched_substrings: List<PlaceAutocompleteMatchedSubstring>,
    val second_text: String
)
data class PlaceAutocompleteMatchedSubstring(
    val length: Double,
    val offset: Double
)
data class PlaceDetailsResponse(
    val result: PlaceDetails,
    val status: String
)

data class PlaceDetails(
    val address_components : List<AddressComponent>,
    val geometry: Geometry
)
data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)
data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)