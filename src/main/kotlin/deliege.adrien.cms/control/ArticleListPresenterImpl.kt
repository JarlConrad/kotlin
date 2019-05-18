package deliege.adrien.cms.control

import deliege.adrien.cms.ArticleListPresenter
import deliege.adrien.cms.Model

class ArticleListPresenterImpl(val model: Model, val view : ArticleListPresenter.View) : ArticleListPresenter {
    override fun start() {
        val list = model.getArticleList()
        view.displayArticleList(list)
    }
}