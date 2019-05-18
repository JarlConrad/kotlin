package deliege.adrien.cms

import deliege.adrien.cms.control.ArticlePresenterImpl
import deliege.adrien.cms.control.ArticleListPresenterImpl
import deliege.adrien.cms.control.CommentPresenterImpl

class AppComponents(mySqlUrl: String, mySqlUser: String, mySqlPassword: String) {

    private val pool = ConnectionPool(mySqlUrl, mySqlUser, mySqlPassword)


    fun getPool(): ConnectionPool {
        return pool
    }

    private val model = MysqlModel(getPool())

    fun getModel(): Model {
        return MysqlModel(getPool())
    }

    fun getArticleListPresenter(view: ArticleListPresenter.View): ArticleListPresenter {
        return ArticleListPresenterImpl(getModel(), view)
    }

    fun getArticlePresenter(view: ArticlePresenter.View): ArticlePresenter {
        return ArticlePresenterImpl(getModel(), view)
    }

//    fun getCommentPresenter(post: CommentPresenter.Post): CommentPresenter {
//        return CommentPresenterImpl(getModel())
//    }

    fun getCommentPresenter(): CommentPresenter {
        return CommentPresenterImpl(getModel())
    }
}