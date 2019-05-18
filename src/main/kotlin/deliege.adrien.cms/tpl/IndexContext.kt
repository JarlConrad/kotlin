package deliege.adrien.cms.tpl

import deliege.adrien.cms.model.Article

data class IndexContext(
    val list: List<Article>
)
