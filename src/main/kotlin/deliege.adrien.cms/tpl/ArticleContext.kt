package deliege.adrien.cms.tpl

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment
import deliege.adrien.cms.model.MySession

data class ArticleContext(
    val article: Article,
    val comments: List<Comment>,
    val session: MySession?
)