package extrydev.app.yuknaklet.model

data class ConfigResponse(
    val error: String,
    val errorText: String,
    val response: ConfigResponseList
)