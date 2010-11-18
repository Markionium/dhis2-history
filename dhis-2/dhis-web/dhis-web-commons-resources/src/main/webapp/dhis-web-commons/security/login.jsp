<jsp:useBean id="userAuditService" type="org.hisp.dhis.useraudit.UserAuditService" scope="application" />
<jsp:useBean id="userAuditStore" type="org.hisp.dhis.useraudit.UserAuditStore" scope="application" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <title>DHIS 2</title>        
        <script type="text/javascript">
           function f(){
			document.getElementById( 'j_username' ).focus();
		   }
        </script>
        <link type="text/css" rel="stylesheet" media="screen" href="../css/login.css">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body onload="f()">
        <div class="loginField" align="center">
            <p><img alt="" src="logo_banner.png"></p>
                <%
                    Object obj = session.getAttribute( "SPRING_SECURITY_LAST_USERNAME" );
                    boolean formVisible = true;
                    if( obj != null )
                    {
                        String username = obj.toString();
                        if( userAuditService.getLoginFailures(username) >= userAuditService.getMaxAttempts() )
                        {
                            formVisible = false;
                %>
            <span class="loginMessage">Maximum Tries exceeded. Please try after <%=userAuditService.getLockoutTimeframe() %> mins</span>
            <%
                        }
                    }
            %>
            <% if(formVisible){%>
            <form action="../../dhis-web-commons-security/login.action" method="post">
                <table>
                    <tr>
                        <td colspan="2" style="height:40px"></td>
                    </tr>
                    <tr>
                        <td><label for="j_username">Username</label></td>
                        <td><input type="text" id="j_username" name="j_username" style="width:18em"></td>
                    </tr>
                    <tr>
                        <td><label for="j_password">Password</label></td>
                        <td><input type="password" id="j_password" name="j_password" style="width:18em"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td><input type="submit" value="Login" style="width:9em">
                            <input type="reset" value="Clear" style="width:9em"></td>
                    </tr>
                </table>
            </form>
            <% } %>
        </div>
    </body>
</html>
