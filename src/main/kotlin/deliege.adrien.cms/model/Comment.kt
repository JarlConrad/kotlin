package deliege.adrien.cms.model

data class Comment(
    val id: Int,
    val article_id: Int,
    val text: String
)