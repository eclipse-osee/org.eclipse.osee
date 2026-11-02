---
summary: "How OSEE logging works (OseeLog/SLF4J/logback chain) and how developers change log levels or add bundle loggers"
tags: [logging, logback, slf4j, osee-log, debugging]
fileMatch: "**/logback*.xml"
---

# OSEE Logging (logback)

This guide explains how logging is wired in OSEE, how to raise a log level to debug a feature, and how to add a logger for a new bundle. It covers both the architecture and the day-to-day developer workflow.

## Level policy (read this first)

OSEE uses log levels with a specific meaning. Follow it:

- **INFO** - detailed diagnostic output that is normally OFF. Use INFO for the verbose, developer-facing detail you turn on temporarily to debug a feature. INFO must not be on by default.
- **WARN** - a real problem that occurred in production and needs attention, but the system continued.
- **ERROR** - a real production error/failure.

Consequences of this policy:
- The default level for everything is **WARN**. A clean run (server, IDE client, or test suite) produces no INFO chatter.
- Do not log routine/expected events at WARN or ERROR just to make them visible - that is what INFO is for (turned on when needed).
- Raising a logger to INFO is a **debugging action**, not a permanent config. Set it back to WARN before committing (the integration suite has a check that fails on a left-in INFO/DEBUG level in the shared logback file).

## Architecture

Most OSEE server/ORCS code logs through an OSGi service, not directly against a logging framework. The chain:

```
application code
   -> org.eclipse.osee.logger.Log        (OSGi service interface)
   -> Sfl4jLogImpl                       (bundle: org.eclipse.osee.logger.slf4j)
   -> SLF4J API (slf4j.api 2.x)
   -> ch.qos.logback.classic             (the binding, discovered via Aries SpiFly)
   -> ConsoleAppender (STDOUT)           (configured by logback-dev.xml)
```

Key points:
- `Sfl4jLogImpl` names each logback logger after the **symbolic name of the bundle that consumes the Log service** (`context.getUsingBundle().getSymbolicName()`). So a logback `<logger name="org.eclipse.osee.orcs.db">` entry controls log output originating from the `org.eclipse.osee.orcs.db` bundle. This is why logback logger names in OSEE are bundle symbolic names, not Java package or class names.
- `Slf4jLogListener` bridges the OSGi `LogService` (framework/bundle/service events) into SLF4J, also keyed by bundle symbolic name.
- SLF4J 2.x finds the logback provider through Java `ServiceLoader`, which in OSGi requires **Aries SpiFly** to be started so the provider is discovered. If SpiFly/logback are not started early, SLF4J falls back to a no-op (NOP) logger and logback config is ignored.

## The config file

The active logback config is selected by the `-Dlogback.configurationFile` VM argument on each launch. Do not assume a fixed path - it varies by build/launch (server p2, IDE client, CI). The shipped dev config is `plugins/org.eclipse.osee.server.p2/logback-dev.xml` and is shared by the server, IDE client, and integration-test launches.

Structure:

```xml
<configuration debug="false">
   <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
         <pattern>%msg -  %-5level %logger{36}%n</pattern>
      </encoder>
   </appender>

   <!-- Per-bundle loggers (name = bundle symbolic name) -->
   <logger name="org.eclipse.osee.orcs.db" level="WARN" />
   <logger name="org.eclipse.osee" level="WARN" />

   <root level="WARN">
      <appender-ref ref="STDOUT" />
   </root>
</configuration>
```

- `debug="false"` - keep OFF. `debug="true"` makes logback print its own internal status banner at startup; only turn on when diagnosing logback config loading itself, and revert.
- `<root level="WARN">` - the default level for everything not matched by a more specific `<logger>`.
- `<logger name="..." level="...">` - a per-bundle override. `name` is the **bundle symbolic name**. More specific names win (e.g. `org.eclipse.osee.orcs.db` overrides `org.eclipse.osee`).
- Logger names are hierarchical by dotted segments, so `org.eclipse.osee` covers all `org.eclipse.osee.*` bundles unless a more specific entry overrides it.

## How to raise a level to debug a feature

You are debugging something in, say, the `org.eclipse.osee.orcs.db` bundle and want its INFO detail.

1. Open the active logback file (the one your launch points at via `-Dlogback.configurationFile`).
2. Find or add the `<logger>` for that bundle and set it to `INFO` (or `DEBUG` for even more):

```xml
<logger name="org.eclipse.osee.orcs.db" level="INFO" />
```

3. Restart the launch (logback reads the file once at startup; editing it does not affect an already-running process).
4. Debug. You will now see that bundle's INFO output.
5. **Set it back to WARN when done, before committing.** The shared file affects everyone, and the integration suite fails if a logger is left above WARN.

To turn up everything temporarily, raise `<root level="INFO">` - but that is very noisy; prefer the narrowest bundle logger that gives you what you need.

## How to add a logger for a new bundle

By default a new bundle inherits `root` (WARN). To be able to toggle it independently, add a `<logger>` whose `name` is the bundle's **symbolic name** (from its `MANIFEST.MF` `Bundle-SymbolicName`):

```xml
<logger name="org.eclipse.osee.mynewbundle" level="WARN" />
```

- Commit it at `WARN` (the default policy). The entry exists so a developer can later flip just that bundle to INFO for debugging.
- Because the logger name is the bundle symbolic name, code in that bundle that logs through the `org.eclipse.osee.logger.Log` service is automatically routed to this logger - no code change needed.
- A logback `<logger>` line for a name that never emits is harmless; logback ignores it. So adding entries pre-emptively costs nothing.

## Note: OseeLog vs logback

There is a second, older logging path: `OseeLog` (in `org.eclipse.osee.framework.logging`) writes through `java.util.logging` (JUL), NOT through SLF4J/logback. Its console threshold comes from `-Dosee.log.default` and per-logger `-Dosee.log.<name>` system properties, and (on the server/test launches) a `java.util.logging` config file. `logback-dev.xml` does NOT control `OseeLog`/JUL output.

Practical takeaway: if changing a `<logger>` level in logback does not affect some line, that line is probably coming through `OseeLog`/JUL (or another framework like ActiveMQ's embedded Log4j) - check the message format. logback lines follow the `%msg -  %-5level %logger{36}` pattern above.

## Console-output regression guard (integration suite)

The ATS IDE integration suite runs a `ConsoleOutputRegressionTest` as its last test. It fails the run on new, unexpected console output so noise does not creep back in:

- unexpected `OseeLog` SEVERE/WARNING events or logged exceptions,
- raw `System.out`/`System.err` output (including any left-enabled `ElapsedTime` timing lines),
- a logback logger/root level left more verbose than WARN (the WARN->INFO debugging toggle you forgot to revert).

Matching is by prefix against a single shared allowlist: `plugins/org.eclipse.osee.client.test.framework/src/org/eclipse/osee/client/test/framework/console-allowlist.txt`.

If the guard fails, prefer fixing the output at its source (remove the debug logging, disable the `ElapsedTime`, revert the logback level). Only when the output is genuinely benign, add a prefix to the allowlist - a small, reviewable diff that makes accepting new console output an explicit decision.

## Repairing logback when it stops working

This section captures the concrete fixes needed to keep logback working across the server, IDE client, and integration-test launches. It was written after an Eclipse platform upgrade (to 2025-06 / platform 4.36, SLF4J 2.x) broke the SLF4J->logback binding and flooded the console. Work through it channel by channel.

### Symptom: SLF4J is not bound (logback config ignored)

Console shows, near the very top of startup:

```
SLF4J(W): No SLF4J providers were found.
SLF4J(W): Defaulting to no-operation (NOP) logger implementation
```

Meaning: SLF4J 2.x could not find the logback provider, so `-Dlogback.configurationFile` is silently ignored and all SLF4J-routed logging is dropped.

Root cause: SLF4J 2.x discovers its provider via the Java `ServiceLoader` (`META-INF/services/org.slf4j.spi.SLF4JServiceProvider`). In OSGi that only works if **Apache Aries SpiFly** is started and has woven the provider lookup BEFORE the first `LoggerFactory.getLogger(...)` call. If SpiFly / logback are not started early enough, SLF4J falls back to NOP.

Fix - ensure these bundles are present and auto-started early, at start level 2 (SpiFly must weave before first log use):
- `org.apache.aries.spifly.dynamic.bundle`
- `ch.qos.logback.classic`
- `ch.qos.logback.core`
- `slf4j.api` (2.x) resolves as a dependency

Where to set this depends on launch type:
- Server / product: the p2 product (`org.eclipse.osee.ide.product`) sets `autoStart="true" startLevel="2"` for spifly + logback in its `<configurations>`; the server `config.ini` starts them. The `org.eclipse.osee.logger.slf4j` bundle manifest requires spifly + logback + slf4j.api (Require-Bundle) so the weaving is in place.
- PDE `.launch` files (test suite, IDE client): add the three bundles to `additional_plugins` with autostart at start level 2, e.g. `ch.qos.logback.classic:1.5.18:default:true:2:default`.
- PDE **JUnit test-suite** launches additionally require `<booleanAttribute key="default_auto_start" value="true"/>`. Without it, the explicit level-2 entries may not actually be started before SLF4J performs its provider lookup, resulting in the NOP warning. `AtsIde_Integration_TestSuite.launch` is the reference configuration.

IMPORTANT for the IDE client (`org.eclipse.pde.ui.RuntimeWorkbench` launch): add the three bundles at start level 2, but do NOT add `default_auto_start=true` / `default_start_level`. A blanket default auto-start force-starts UI bundles (e.g. `org.eclipse.debug.ui`) before the Workbench exists, causing `IllegalStateException: Workbench has not been created yet` cascades. The explicit per-bundle autostart entries are enough to bind logback; the blanket default is harmful here. PDE JUnit launches are different: they require `default_auto_start=true`; the RCP client does not.

A residual single `SLF4J(W): No providers found` line at the very top of a PDE dev launch can persist even when logback then binds correctly (the first getLogger call races SpiFly). Confirm binding actually worked by looking for the startup line the client/server print: `Logback Config File: ...`. If that prints and WARN filtering works, the lone NOP line is a cosmetic dev-launch artifact.

### The five console channels (each quieted differently)

Console noise in OSEE comes from independent logging systems. A fix for one does not affect the others. Identify the channel by its line format, then apply the matching fix:

1. **SLF4J -> logback** (`%msg -  %-5level %logger{36}` pattern). Controlled by `logback-dev.xml` levels (root + per-bundle `<logger>`), selected by `-Dlogback.configurationFile`. Requires the SLF4J binding above to actually take effect.
2. **OseeLog -> java.util.logging (JUL)** (format `Mon DD, YYYY H:MM:SS AM ClassName method` then `LEVEL: msg`). NOT controlled by logback. Threshold from `-Dosee.log.default` (set to `WARNING`) and `-Djava.util.logging.config.file=.../etc/logging.properties`. CXF, SpiFly, and `JdbcClientImpl` log via JUL.
3. **Embedded ActiveMQ broker -> Log4j 1.x** (format `INFO | message`, pipe-delimited). The `vm://localhost` broker (started by the client/test event system) logs through the `log4j.properties` bundled inside `activemq-all-5.14.0.jar`. Log4j 1.x ignores `-Dlog4j.logger.*` system props; the ONLY lever is `-Dlog4j.configuration=file:.../etc/activemq-log4j.properties` pointing at a WARN-level Log4j 1.x config (a small file kept in `org.eclipse.osee.server.p2/etc`).
4. **Raw `System.out` / `System.err`** (no level prefix). From `println`, `printStackTrace()`, suite banners, and `ElapsedTime` (via `XConsoleLogger` to stderr). Fix at the source; suite banners are allowed, per-test prints are not (see the Java test standards).
5. **JVM / Eclipse launcher** (e.g. `WARNING: Using incubator modules: jdk.incubator.vector`, and the `-debug` framework dump). Printed before any logging framework exists; not suppressible via config. Remove `-debug` from the launch Program Arguments to drop the framework/config/classpath dump. The incubator-vector warning comes from Lucene 10 using the Panama Vector API and has no clean suppression - accept it.

### Per-launch VM args checklist (dev launches)

For a quiet launch that honors logback, the VM args should include:
```
-Dosee.log.default=WARNING
-Dlogback.configurationFile=${project_loc:org.eclipse.osee.server.p2}\logback-dev.xml
-Djava.util.logging.config.file=${project_loc:org.eclipse.osee.server.p2}\etc\logging.properties
-Dlog4j.configuration=file:${project_loc:org.eclipse.osee.server.p2}/etc/activemq-log4j.properties
-Declipse.e4.inject.javax.warning=false
```
Notes: the logback path is resolved per launch and can differ by build - never hard-code it; read it from the property. The `logback-dev.xml` is currently shared by server, IDE client, and integration-test launches, so a level change there affects all three.

### Client must not start the server

A large class of client console noise (ActiveMQ broker, CXF extension logging, `OrcsApplication` startup, cold-storage warnings) is actually the *application server* running inside the IDE client. That happens when the client launch includes `org.eclipse.osee.core.server.external.feature`, which pulls in the server REST/ORCS/datastore bundles. Remove that feature from the IDE client `.launch` `selected_features` so the client only talks to a separately-run server. Verify with the OSGi console: `ss | grep orcs.rest` should show no `orcs.rest` (impl) and Jetty should be RESOLVED, not ACTIVE. (Note: the `vm://` event broker is client-side by design and still starts - quiet it via the Log4j arg above, do not try to remove it.)

### Quick diagnosis flow

1. Identify the channel by line format (the five above).
2. If logback lines are missing entirely -> SLF4J binding problem (SpiFly/logback start level).
3. If a logback `<logger>` change has no effect on a line -> that line is NOT logback (JUL, Log4j/ActiveMQ, or raw System.out).
4. If server-ish output appears in the client -> the client is starting the server (remove the server feature).
5. Keep the shared `logback-dev.xml` at WARN; use INFO only as a temporary, reverted debugging toggle.
