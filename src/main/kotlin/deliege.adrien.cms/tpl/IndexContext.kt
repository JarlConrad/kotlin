package deliege.adrien.cms.tpl

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.MySession

data class IndexContext(
    val list: List<Article>,
    val session: MySession?
)
