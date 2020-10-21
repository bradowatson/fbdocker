FROM tomcat

COPY FantasyBaseball.war /usr/local/tomcat/webapps/
COPY fbaseball.basnippet /opt/fbaseball.basnippet
