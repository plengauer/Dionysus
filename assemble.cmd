copy /Y Dionysus.xml .\out\
copy /Y run.cmd .\out\
copy /Y *.properties .\out\
copy /Y *.jar .\out\
xcopy %JAVA_17_HOME% .\out\jre\ /Y /S
pause