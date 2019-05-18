<html>
<head>

</head>
<body>
    <h1>${article.title}</h1>
    <p>${article.text}</p>
    <ul>
        <#list comments as comment>
            <li>
                ${comment.text}
            </li>
        </#list>
    </ul>

    <form action="/article/${article.id}" method="post" enctype="multipart/form-data">
        <textarea name="comment" id="comment" cols="30" rows="10"></textarea>
        <button type="submit">Ajouter un commentaire</button>
    </form>
</body>
</html>