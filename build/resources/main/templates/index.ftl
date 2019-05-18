<html>
<head>
        <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
        <ul>
          <#list list as article>
            <li>
              <a href="/article/${article.id}">${article.title}</a>
            </li>
          </#list>
        </ul>
</body>
</html>