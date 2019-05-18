package deliege.adrien.cms

import deliege.adrien.cms.model.Article
import deliege.adrien.cms.model.Comment
import deliege.adrien.cms.model.UserSession
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
import io.ktor.util.hex
import kotlinx.coroutines.launch
import java.io.File

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

        install(Authentication) {
            basic(name = "auth") {
                realm = "Ktor Server"
                validate { credentials ->
                    if (credentials.name == credentials.password) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
                skipWhen { call -> call.sessions.get<UserSession>() != null }
            }
            basic(name = "auth2") {
                realm = "Ktor Server"
                validate { credentials ->
                    if (credentials.name == "hey" && credentials.password == "ho") {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
                skipWhen { call -> call.sessions.get<UserSession>() != null }
            }
        }

        install(Sessions) {
            val secretHashKey = hex("6819b57a326945c1968f45236589")

            cookie<UserSession>(
                "LOGIN_COOKIE",
                directorySessionStorage(rootDir = File(".sessions"), cached = true)
            ) {
                cookie.path = "/" // Specify cookie's path '/' so it can be used in the whole site
                transform(SessionTransportTransformerMessageAuthentication(secretHashKey, "HmacSHA256"))
            }
        }

        routing {

            authenticate ("auth2") {
                get("/admin") {
                    val principal: UserIdPrincipal? = call.authentication.principal<UserIdPrincipal>()
                    //val userSessions = call.sessions.get<UserSession>()
                    //val session = call.sessions.get<UserSession>() ?: UserSession(principal.name)

//                    if (userSessions == null)
//                    {
//                        if (principal != null) {
//                            call.sessions.set(UserSession(name = principal.name).toString())
//                        }
//                    }
                    if (principal != null) {
                        call.respondText("Hello "+principal.name)
                        call.sessions.set(UserSession(name = principal.name).toString())
                    }
                }
            }
            route("/logout") {
                get {
                    call.sessions.clear<UserSession>()
                    call.respondText("logout")
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

//            post("/logout") {
//                call.sessions.clear<MySession>()
//                redirect("/")
//            }

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

