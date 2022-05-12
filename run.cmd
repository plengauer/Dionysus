set JAVA_TOOL_OPTIONS=-javaagent:.\opentelemetry-javaagent.jar
set OTEL_RESOURCE_ATTRIBUTES=service.name=Dionysus,service.version=1.0.0
set OTEL_TRACES_EXPORTER=otlp
set OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=https://ldj78075.sprint.dynatracelabs.com/api/v2/otlp/v1/traces
set OTEL_EXPORTER_OTLP_TRACES_PROTOCOL=http/protobuf
set OTEL_EXPORTER_OTLP_TRACES_HEADERS=Authorization=Api-Token %DIONYSUS_DYNATRACE_API_TOKEN%
:start
if exist .external_update.txt (
  for %%f in (.\*.new) do copy %%f %%~nf
  for /D %%f in (.\*.new) do xcopy /Y /E %%f %%~nf
  copy .external_update.txt .version.txt
  del .external_update.txt
)
jre\bin\java.exe -verbose:gc -Xmx100m -Djava.util.logging.config.file=logging.properties -Dupdate=true -Dexternal-update=true -jar Dionysus.jar default
goto start