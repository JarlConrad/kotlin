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

            route("/login") {
                get {
                    val session = call.sessions.get<MySession>()
                    if (session != null) {
                        call.respondRedirect("/admin", permanent = false)
                    } else {
                        call.respond(FreeMarkerContent("login.ftl", null))
                    }

                }
                authenticate("login") {
                    post {
                        val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                        call.sessions.set("SESSION", MySession(principal.name))
                        call.respondRedirect("/admin", permanent = false)
                    }
                }
            }

            get("/logout") {
                val session = call.sessions.get<MySession>()
                if (session != null) {
                    call.sessions.clear<MySession>()
                    call.respondRedirect("/", permanent = false)
                } else {
                    call.respond(FreeMarkerContent("login.ftl", null))
                }
            }

            get("/admin") {
                val session = call.sessions.get<MySession>()
                if (session != null) {
                    call.respondText("User is logged")
                } else {
                    call.respondRedirect("/login", permanent = false)
                }
            }

            route("/article/{id}") {
                get {
                    val controller = appComponents.getArticlePresenter(object : ArticlePresenter.View {
                        override fun displayArticle(article: Article, comments: List<Comment>) {
                            val context = ArticleContext(article, comments)
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
                        controller.start(id, post["comment"]!!)
                    }
                    call.respondRedirect("/article/$id", permanent = false)
                }
            }

            get("/") {
                val controller = appComponents.getArticleListPresenter(object:ArticleListPresenter.View {
                    override fun displayArticleList( list : List<Article>) {
                        val context = IndexContext(list)
                        launch {
                            call.respond(FreeMarkerContent("index.ftl", context, "e"))
                        }
                    }
                })
                controller.start()
            }

            static("/static") {
                resources("static")
            }
        }
    }.start(wait = true)
}

