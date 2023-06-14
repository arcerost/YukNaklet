package extrydev.app.yuknaklet.model

data class ConfigResponseList(
    val constant: String,
    val intro: List<ConfigResponseIntro>,
    val terms: List<String>
)