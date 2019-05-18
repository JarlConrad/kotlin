package deliege.adrien.cms

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment

class MysqlModel(val pool: ConnectionPool) : Model {
    override fun postComment(article_id: Int, text: String) {
        pool.useConnection { connection ->
            connection.prepareStatement("INSERT INTO comments (article_id, text) VALUES (?, ?)").use { stmt ->
                stmt.setInt(1, article_id)
                stmt.setString(2, text)

                stmt.executeUpdate()
            }
        }
    }

    override fun getCommentListByArticle(id: Int): List<Comment> {
        val list = ArrayList<Comment>()

        pool.useConnection { connection ->
            connection.prepareStatement("SELECT id, article_id, text FROM comments WHERE article_id = ?").use {stmt ->
                stmt.setInt(1, id)

                stmt.executeQuery().use {results ->
                    while (results.next())
                    {
                        list += Comment(
                            results.getInt("id"),
                            results.getInt("article_id"),
                            results.getString("text")
                            //results.getString("text")
                        )
                    }
                }
            }
        }
        return list
    }



    override fun getArticle(id: Int): Article? {
        pool.useConnection { connection ->
            connection.prepareStatement("SELECT * FROM articles WHERE id = ?").use {stmt ->
                stmt.setInt(1, id)

                stmt.executeQuery().use {result ->
                    if (result.next())
                    {
                        return Article(
                            result.getInt("id"),
                            result.getString("title"),
                            result.getString("text")
                        )
                    }
                }
            }
        }
        return null
    }

    override fun getArticleList(): List<Article> {
        val list = ArrayList<Article>()

        pool.useConnection { connection ->
            connection.prepareStatement("SELECT id, title FROM articles").use {stmt ->
                stmt.executeQuery().use {results ->
                    while (results.next())
                    {
                        list += Article(
                            results.getInt("id"),
                            results.getString("title"),
                            null
                            //results.getString("text")
                        )
                    }
                }
            }
        }
        return list
    }

}