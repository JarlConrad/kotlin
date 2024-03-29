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
        <br>
        <a class="login" href="/">Retour</a>
    </nav>
    <h1>${article.title}</h1>
    <p>${article.text}</p>
    <ul>
        <#list comments as comment>
            <li>
                ${comment.text}
                <#if session?has_content>
                    <form action="/article/${article.id}/comment/delete/${comment.id}" method="POST">
                        <button class="delete" type="submit">Supprimer</button>
                    </form>
                </#if>
            </li>
        </#list>
    </ul>

    <form action="/article/${article.id}" method="post" enctype="multipart/form-data">
        <textarea name="comment" id="comment" cols="30" rows="10"></textarea>
        <button type="submit">Ajouter un commentaire</button>
    </form>
</body>
</html>