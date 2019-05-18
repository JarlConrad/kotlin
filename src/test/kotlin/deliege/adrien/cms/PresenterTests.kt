package deliege.adrien.cms

import com.nhaarman.mockitokotlin2.*
import deliege.adrien.cms.control.ArticleListPresenterImpl
import deliege.adrien.cms.control.ArticlePresenterImpl
import deliege.adrien.cms.model.Article
import org.junit.Assert.*
import org.junit.Test

class PresenterTests {

    @Test
    fun testArticleListPresenters()
    {
        /*val model = object : Model {
            override fun getArticleList(): List<Article> = listOf(
                Article(1, "Un", null),
                Article(2, "Un", null)
            )

            override fun getArticle(id: Int): Article? = throw IllegalStateException()
        }*/

        val list = listOf(Article(1, "Un", null), Article(2, "Un", null))

        val model = mock<Model> {
            on { getArticleList() } doReturn list
        }

        val view = mock<ArticleListPresenter.View>()


       /* var displayCalled = false

        val view = object : ArticleListPresenter.View {
            override fun displayArticleList(list: List<Article>) {
                displayCalled = true
                assertEquals(2, list.size)
                assertEquals("Un", list[0].title)
                assertEquals("Deux", list[0].title)
            }

        }*/

        val presenter = ArticleListPresenterImpl(model, view)
        presenter.start()

        verify(model).getArticleList()
        verify(view).displayArticleList(list)
        verifyNoMoreInteractions(model, view)


        //assertTrue(displayCalled)
        // s'assure que la fonction Model.getArticleList a été appelé
        // s'assure que la fonction View.displayArticleList a été appelé avec la liste retournée
    }

    @Test
    fun testArticlePresenter() {
        val article = Article(42, "Quarante-deux", "Lorem ipsum")

        val model = mock<Model> {
            on { getArticle(42) } doReturn article
        }

        val view = mock<ArticlePresenter.View>()

        val presenter = ArticlePresenterImpl(model, view)
        presenter.start(42)

        verify(model).getArticle(42)
        verify(view).displayArticle(article)
        verifyNoMoreInteractions(model, view)
    }

    @Test
    fun testArticlePresenterExist() {

        val model = mock<Model> {
            on { getArticle(any()) } doReturn null
        }

        val view = mock<ArticlePresenter.View>()

        val presenter = ArticlePresenterImpl(model, view)
        presenter.start(42)

        verify(model).getArticle(42)
        verify(view).displayNotFound()
        verifyNoMoreInteractions(model, view)
    }
}