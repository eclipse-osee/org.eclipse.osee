---
summary: "Troubleshooting guide for compiling the OSEE IDE and launching the demo application server from Eclipse (startup errors and their fixes)."
tags: [ide, eclipse, server, launch, osgi, troubleshooting, demo]
fileMatch: "plugins/org.eclipse.osee.server.p2/**"
---

# IDE Compile And Launch

Troubleshooting log for building the OSEE IDE in Eclipse and launching the demo
application server (`OSEE Application Server [PostGreSQL]` launch config, bundle
`org.eclipse.osee.server.p2`). Each entry: the symptom, the root cause, and what
actually fixed it. Add new entries as issues come up.

## Launch Setup Reference

Verified facts about the local demo launch, so they are not re-derived.

- **Launch config** - `OSEE Application Server [PostGreSQL]`, program args include
  `-console -consoleLog -data workspace -clean`. `-clean` wipes the OSGi
  configuration/cache every launch, so stale OSGi cache is rarely the cause of a
  startup failure.
- **Key VM args** - `-Dorg.osgi.service.http.port=8089`, `-Dosee.db=orgdemo`,
  `-Dosee.authentication.protocol=orgdemo`, broker at `tcp://localhost:61616`,
  web UI origin `http://localhost:4200`.
- **Build dependency** - launching does NOT compile Java. It runs against whatever
  the Eclipse workspace has already built into each bundle's `bin/`. If the
  workspace is not fully built, the server starts against missing/stale classes.

## Known Startup Issues

### Bundle Cannot Find Its Own Classes After `git clean -fdx`

<div style="margin: 0 2rem; padding: 0.75rem 1rem; background: #fff8e1; border-left: 4px solid #f9a825; border-radius: 4px;">
<b>Note</b> - This is the most disruptive one and looks scary (hundreds of stack
traces) but has a trivial fix. Do not chase it as a code bug.
</div>

- **Symptom** - A flood of identical errors at startup, each of the form
  `java.lang.ClassNotFoundException: <bundle>.SomeClass cannot be found by
  <bundle>_1.0.0.qualifier`, where the class belongs to the bundle's **own**
  package (e.g. `org.eclipse.osee.framework.plugin.core.internal.Activator`,
  `OsgiSlf4jLogger`, `BootstrapUsers`, every `WorkDef*`). Bundles also report
  `... is not active`.
- **Root cause** - The bundles resolved fine; their compiled `.class` files simply
  are not on disk. This happens after `git clean -fdx` (which deletes all ignored
  build output - `bin/`, `target/`) when the server is launched before Eclipse has
  finished recompiling the workspace. The `-clean` launch flag does not help - it
  clears OSGi runtime state, not the missing bytecode.
- **Fix** -
  1. **Project &rarr; Clean&hellip; &rarr; Clean all projects** and let the full
     workspace build finish (build progress indicator goes idle, Problems view
     settles).
  2. Confirm classes exist, e.g.
     `plugins/org.eclipse.osee.framework.plugin.core/bin/` is populated.
  3. Relaunch the server.
- **Gotcha** - If **Build Automatically** is off, a `-clean` launch right after
  `git clean -fdx` always comes up with no classes. Turn it on, or do the manual
  clean build, before launching.

### ServletConfig Has Not Been Initialized (JAX-RS Startup Race)

- **Symptom** - `Error activating application [JaxRsContainerImpl ...] on
  [org.eclipse.osee.web.deploy.OseeWebApplication]` with
  `java.lang.IllegalStateException: ServletConfig has not been initialized` at
  `CxfJaxRsFactory.newCxfServer` &rarr; `servlet.getServletName()`.
- **Root cause** - A startup ordering race in unchanged server code: the JAX-RS
  application component tried to start its CXF server before the HTTP whiteboard
  (Jetty/Felix http on port 8089) finished registering the base servlet, so the
  servlet was never `init(ServletConfig)`-ed. Typically seen when the server is
  launched against a half-built or mid-rebuild workspace, or when port 8089 is
  still held by a prior run.
- **Fix** -
  1. Let the full rebuild finish, then relaunch clean.
  2. Confirm nothing is squatting on 8089 (and no rogue Java processes) before
     relaunching:
     `Get-NetTCPConnection -LocalPort 8089 -ErrorAction SilentlyContinue`.
- **Note** - Not caused by application/model code changes; it is environmental
  startup timing. If it persists on a free port with a completed build, investigate
  whether the Equinox/Felix HTTP service bundle actually started.
