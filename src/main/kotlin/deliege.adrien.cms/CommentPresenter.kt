package deliege.adrien.cms

import deliege.adrien.cms.model.Comment

interface CommentPresenter {
    fun start(article_id: Int, text: String)
}