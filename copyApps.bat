echo "Removing old application war and directory"
del webapp.war
rmdir webapp /S /Q

del first.war
rmdir first /S /Q

del second.war
rmdir second /S /Q

echo "Deploying new webapp"
copy C:\Jonatas\jSonar\MethodCallRestAPIAbstractionProject\jsonarService\jsonarWebApp\target\webapp.war C:\Jonatas\Programs\apache-tomcat-8.5.33\webapps\

IF "%1"=="-s" (
	echo "Deploying First Service"
	copy C:\Jonatas\jSonar\MethodCallRestAPIAbstractionProject\jsonarService\jsonarFirstService\jsonar-first-service\target\first.war C:\Jonatas\Programs\apache-tomcat-8.5.33\webapps\

	echo "Deploying Second Service"
	copy C:\Jonatas\jSonar\MethodCallRestAPIAbstractionProject\jsonarService\jsonarSecondService\jsonar-second-service-main\target\second.war C:\Jonatas\Programs\apache-tomcat-8.5.33\webapps\
)