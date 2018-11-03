<html>
<<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
<h1>file upload</h1>
<form name="form1" action="/manager/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file">
    <input type="submit" value="submit"><br>
</form>
<h1>text rich upload</h1>
<form name="form2" action="/manager/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_richText">
    <input type="submit" value="submit"><br>
</form>
</body>
