<%-- 
    Document   : index
    Created on : Mar 3, 2014, 4:14:40 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="js/jquery-1.10.2.js"></script>
        <script type="text/javascript" src="js/jquery.autocomplete.js"></script>
        <script type="text/javascript" src="js/jquery.autocomplete.min.js"></script>
        <title>JSP Page</title>
<script>
  $(function() {
var countries = [
   { value: 'Andorra', data: 'AD' },
   // ...
   { value: 'Zimbabwe', data: 'ZZ' }
];

$('#autocomplete').autocomplete({
    lookup: countries,
    onSelect: function (suggestion) {
        alert('You selected: ' + suggestion.value + ', ' + suggestion.data);
    }
});
</script>
    </head>
    <body>
        <h1>Hello World!</h1>
        <input type="text" name="country" id="autocomplete"/>
    </body>
</html>
