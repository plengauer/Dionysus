:start
%JAVA_17_HOME%\bin\java.exe -verbose:gc -Xmx100m -Djava.util.logging.config.file=logging.properties -Dupdate=true -Ddionysus.delay=180 -jar Dionysus.jar default
goto start