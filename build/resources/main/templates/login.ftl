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
<#if error??>
    <p style="color:red;">${error}</p>
</#if>
<p>Pour ce connecter utiliser un username identique au password</p>
<form action="/login" method="post" enctype="application/x-www-form-urlencoded">
    <div>User:</div>
    <div><input type="text" name="username" /></div>
    <div>Password:</div>
    <div><input type="password" name="password" /></div>
    <div><input type="submit" value="Login" /></div>
</form>
</body>
</html>