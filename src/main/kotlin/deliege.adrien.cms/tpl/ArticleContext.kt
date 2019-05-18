package deliege.adrien.cms.tpl

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment

data class ArticleContext(
    val article: Article,
    val comments: List<Comment>
)