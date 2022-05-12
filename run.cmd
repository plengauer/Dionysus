set JAVA_TOOL_OPTIONS=-javaagent:.\opentelemetry-javaagent.jar
set OTEL_RESOURCE_ATTRIBUTES=service.name=Dionysus,service.version=1.0.0
set OTEL_TRACES_EXPORTER=otlp
set OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=https://ldj78075.sprint.dynatracelabs.com/api/v2/otlp/v1/traces
set OTEL_EXPORTER_OTLP_TRACES_PROTOCOL=http/protobuf
set OTEL_EXPORTER_OTLP_TRACES_HEADERS=Authorization=Api-Token %DIONYSUS_DYNATRACE_API_TOKEN%
:start
if exist external_update (
  for %%f in (.\*.new) do xcopy /Y %%f %%~nf
  for /D %%f in (.\*.new) do xcopy /Y /E %%f %%~nf
  del external_update
)
jre\bin\java.exe -verbose:gc -Xmx100m -Djava.util.logging.config.file=logging.properties -Dupdate=true -Dexternal-update-file=external_update -jar Dionysus.jar default
goto start