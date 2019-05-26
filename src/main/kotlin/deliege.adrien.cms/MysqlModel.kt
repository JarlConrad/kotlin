package deliege.adrien.cms

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment

class MysqlModel(val pool: ConnectionPool) : Model {
    override fun postArticle(title: String, text: String) {
        pool.useConnection { connection ->
            connection.prepareStatement("INSERT INTO articles (title, text) VALUES (?, ?)").use { stmt ->
                stmt.setString(1, title)
                stmt.setString(2, text)

                stmt.executeUpdate()
            }
        }
    }

    override fun deleteAllComments(article_id: Int) {
        pool.useConnection { connection ->
            connection.prepareStatement("DELETE FROM comments WHERE article_id = ?").use {stmt ->
                stmt.setInt(1, article_id)

                stmt.executeUpdate()
            }
        }
    }

    override fun deleteComment(id: Int) {
        pool.useConnection { connection ->
            connection.prepareStatement("DELETE FROM comments WHERE id = ?").use {stmt ->
                stmt.setInt(1, id)

                stmt.executeUpdate()
            }
        }
    }

    override fun deleteArticle(id: Int) {
        pool.useConnection { connection ->
            connection.prepareStatement("DELETE FROM articles WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)

                stmt.executeUpdate()
            }
        }
    }

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