package deliege.adrien.cms

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment

interface ArticlePresenter {
    fun start(id: Int)

    fun delete(id: Int)

    fun store(title: String, text: String)

    interface View {
        fun displayArticle(article: Article, comments: List<Comment>)

        fun displayNotFound()
    }
}