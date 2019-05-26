package deliege.adrien.cms.control

import deliege.adrien.cms.ArticlePresenter
import deliege.adrien.cms.Model

class ArticlePresenterImpl (val model: Model, val view : ArticlePresenter.View) : ArticlePresenter {
    override fun store(title: String, text: String) {
        model.postArticle(title, text)
    }

    override fun delete(id: Int) {
        val article = model.getArticle(id)
        if (article != null) {
            model.deleteAllComments(id)
            model.deleteArticle(id)
        } else {
            view.displayNotFound()
        }
    }

    override fun start(id: Int) {
        val article = model.getArticle(id)
        if (article != null) {
            val comments = model.getCommentListByArticle(id)
            view.displayArticle(article, comments)
        } else {
            view.displayNotFound()
        }
    }
}