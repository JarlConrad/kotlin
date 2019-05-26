package deliege.adrien.cms

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment
import deliege.adrien.cms.model.MySession
import deliege.adrien.cms.tpl.ArticleContext
import deliege.adrien.cms.tpl.IndexContext
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.launch

class App

@KtorExperimentalAPI
fun main()
{
    val appComponents =
        AppComponents("jdbc:mysql://localhost:8889/kotlin?serverTimezone=UTC", "root", "root")

    embeddedServer(Netty, 8080) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(App::class.java.classLoader, "templates")
        }

        install(Sessions) {
            cookie<MySession>("SESSION")
        }

        routing {

            install(Authentication) {
                form("login") {
                    userParamName = "username"
                    passwordParamName = "password"
                    challenge = FormAuthChallenge.Unauthorized
                    validate { credentials -> if (credentials.name == credentials.password) UserIdPrincipal(credentials.name) else null }
                }
            }

            //////////////////////
            // GET ARTICLE LIST //
            //////////////////////
            get("/") {
                val controller = appComponents.getArticleListPresenter(object:ArticleListPresenter.View {
                    val session = call.sessions.get<MySession>()
                    override fun displayArticleList( list : List<Article>) {
                        val context = IndexContext(list, session)
                        launch {
                            call.respond(FreeMarkerContent("index.ftl", context, "e"))
                        }
                    }
                })
                controller.start()
            }

            ////////////////////////////////////////
            // GET ONE ARTICLE OR POST NEW COMMENT//
            ////////////////////////////////////////
            route("/article/{id}") {
                get {
                    val controller = appComponents.getArticlePresenter(object : ArticlePresenter.View {
                        val session = call.sessions.get<MySession>()
                        override fun displayArticle(article: Article, comments: List<Comment>) {
                            val context = ArticleContext(article, comments, session)
                            launch {
                                call.respond(FreeMarkerContent("article.ftl", context, "e"))
                            }
                        }

                        override fun displayNotFound() {
                            launch {
                                call.respond(HttpStatusCode.NotFound)
                            }
                        }

                    })
                    val id = call.parameters["id"]!!.toIntOrNull()
                    if (id != null) {
                        controller.start(id)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                post {
                    val controller = appComponents.getCommentPresenter()
                    val id = call.parameters["id"]!!.toIntOrNull()
                    val post = call.receiveParameters()
                    if (id != null)
                    {
                        controller.store(id, post["comment"]!!)
                    }
                    call.respondRedirect("/article/$id", permanent = false)
                }
            }

            /////////////////////
            // POST NEW ARTICLE//
            /////////////////////
            route("/article/store") {
                get {
                    val session = call.sessions.get<MySession>()
                    if (session != null) {
                        call.respond(FreeMarkerContent("article_store.ftl", null))
                    } else {
                        call.respondRedirect("/", permanent = false)
                    }
                }

                post {
                    val controller = appComponents.getArticlePresenter(object : ArticlePresenter.View {
                        val session = call.sessions.get<MySession>()
                        override fun displayArticle(article: Article, comments: List<Comment>) {
                            val context = ArticleContext(article, comments, session)
                            launch {
                                call.respond(FreeMarkerContent("article.ftl", context, "e"))
                            }
                        }

                        override fun displayNotFound() {
                            launch {
                                call.respond(HttpStatusCode.NotFound)
                            }
                        }
                    })

                    val post = call.receiveParameters()
                    val text = post["text"]
                    val title = post["title"]

                    if (text != null && title != null) {
                        controller.store(title, text)
                    }

                    call.respondRedirect("/", permanent = false)
                }
            }

            ////////////////////
            // DELETE ARTICLE //
            ////////////////////
            post("/article/{id}/delete") {
                val controller = appComponents.getArticlePresenter(object : ArticlePresenter.View {
                    val session = call.sessions.get<MySession>()
                    override fun displayArticle(article: Article, comments: List<Comment>) {
                        val context = ArticleContext(article, comments, session)
                        launch {
                            call.respond(FreeMarkerContent("article.ftl", context, "e"))
                        }
                    }

                    override fun displayNotFound() {
                        launch {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    }
                })
                val id = call.parameters["id"]!!.toIntOrNull()
                if (id != null) {
                    controller.delete(id)
                }
                call.respondRedirect("/", permanent = false)
            }

            ////////////////////
            // DELETE COMMENT //
            ////////////////////
            post ("/article/{id}/comment/delete/{comment_id}") {
                val controller = appComponents.getCommentPresenter()
                val id = call.parameters["id"]!!.toIntOrNull()
                val comment_id = call.parameters["comment_id"]!!.toIntOrNull()
                if (comment_id != null) {
                    controller.delete(comment_id)
                }
                call.respondRedirect("/article/$id", permanent = false)
            }

            //////////
            // LOGIN//
            //////////
            route("/login") {
                get {
                    val session = call.sessions.get<MySession>()
                    if (session != null) {
                        call.respondRedirect("/", permanent = false)
                    } else {
                        call.respond(FreeMarkerContent("login.ftl", null))
                    }

                }
                authenticate("login") {
                    post {
                        val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                        call.sessions.set("SESSION", MySession(principal.name))
                        call.respondRedirect("/", permanent = false)
                    }
                }
            }

            ////////////
            // LOGOUT //
            ////////////
            get("/logout") {
                val session = call.sessions.get<MySession>()
                if (session != null) {
                    call.sessions.clear<MySession>()
                    call.respondRedirect("/", permanent = false)
                } else {
                    call.respond(FreeMarkerContent("login.ftl", null))
                }
            }

            static("/static") {
                resources("static")
            }
        }
    }.start(wait = true)
}

