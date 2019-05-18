package deliege.adrien.cms

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment

interface Model {
    fun getArticleList(): List<Article>

    fun getArticle(id: Int): Article?

    fun getCommentListByArticle(id: Int): List<Comment>

    fun postComment(article_id: Int, text: String)
}