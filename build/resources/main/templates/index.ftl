<html>
<head>
        <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
        <nav>
            <#if session?has_content>
                <a class="login" href="/logout">Deconnexion</a>
            <#else>
                <a class="login" href="/login">Connexion</a>
            </#if>
        </nav>
        <ul>
          <#list list as article>
            <li>
              <a href="/article/${article.id}">${article.title}</a>
                <#if session?has_content>
                    <form action="/article/${article.id}/delete" method="POST">
                        <button class="delete" type="submit">Supprimer</button>
                    </form>
                </#if>
            </li>
          </#list>
        </ul>

        <#if session?has_content>
            <a href="/article/store"><button>Ajouter un article</button></a>
        </#if>
</body>
</html>