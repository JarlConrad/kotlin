package deliege.adrien.cms

import deliege.adrien.cms.model.Comment

interface CommentPresenter {
    fun store(article_id: Int, text: String)

    fun delete(id: Int)
}