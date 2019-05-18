package deliege.adrien.cms

import deliege.adrien.cms.model.Article

interface ArticleListPresenter {

    fun start()

    interface View {
        fun displayArticleList(list: List<Article>)
    }
}