<html>
<head>
    <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<#if error??>
    <p style="color:red;">${error}</p>
</#if>
<form action="/article/store" method="post" enctype="application/x-www-form-urlencoded">
    <div>Title</div>
    <div><input type="text" name="title" /></div>
    <div>Text</div>
    <div><textarea name="text"> </textarea></div>
    <div><input type="submit" value="Valider" /></div>
</form>
</body>
</html>