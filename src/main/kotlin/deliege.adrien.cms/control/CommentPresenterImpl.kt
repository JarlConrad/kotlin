package deliege.adrien.cms.control

import deliege.adrien.cms.CommentPresenter
import deliege.adrien.cms.Model

class CommentPresenterImpl(val model: Model) : CommentPresenter {
    override  fun start(article_id: Int, text: String) {
        model.postComment(article_id, text)
    }
}