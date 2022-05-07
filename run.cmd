SET JAVA_TOOL_OPTIONS=-javaagent:.\opentelemetry-javaagent.jar
SET OTEL_RESOURCE_ATTRIBUTES=service.name=Dionysus,service.version=1.0.0
SET OTEL_TRACES_EXPORTER=otlp
SET OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=https://ldj78075.sprint.dynatracelabs.com/api/v2/otlp/v1/traces
SET OTEL_EXPORTER_OTLP_TRACES_PROTOCOL=http/protobuf
SET OTEL_EXPORTER_OTLP_TRACES_HEADERS=Authorization=Api-Token %DIONYSUS_DYNATRACE_API_TOKEN%
:start
%JAVA_17_HOME%\bin\java.exe -verbose:gc -Xmx100m -Djava.util.logging.config.file=logging.properties -Dupdate=true -Ddionysus.delay=180 -jar Dionysus.jar default
goto start