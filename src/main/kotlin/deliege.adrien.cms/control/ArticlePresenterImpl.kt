package deliege.adrien.cms.control

import deliege.adrien.cms.ArticlePresenter
import deliege.adrien.cms.Model

class ArticlePresenterImpl (val model: Model, val view : ArticlePresenter.View) : ArticlePresenter {
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