---
summary: "How to add third-party dependencies to the OSEE server OSGi runtime"
tags: [server, osgi, dependencies, maven, orbit]
fileMatch: "**/osgi.converter/**,**/dep.feature/**,**/dep.parent/**,**/*.target"
---

# Adding Dependencies to the OSEE Server

OSEE runs in an OSGi environment. Third-party libraries must be available as OSGi bundles before they can be imported by plugin MANIFEST.MF files. There are two paths to get a dependency into the runtime:

## Path 1: Eclipse Orbit (preferred)

Orbit is Eclipse's curated repository of OSGi-wrapped third-party libraries. If a dependency is available there, use it directly — no conversion needed.

### Steps

1. **Check Orbit for the bundle.** Browse the Orbit aggregation index or the Eclipse release plugins directory. The currently configured Orbit URLs used by the OSEE build are in `plugins/org.eclipse.osee.dep.parent/pom.xml` — look for `<repository>` entries referencing `download.eclipse.org/tools/orbit`. Use those to verify you're checking the correct and latest Orbit version:
   - https://download.eclipse.org/tools/orbit/simrel/orbit-aggregation/2025-06/index.html
   - https://download.eclipse.org/releases/2025-06/202506111000/plugins/

   Search for the bundle symbolic name (e.g., `org.apache.cxf.cxf-rt-rs-sse`).

2. **Add to the target platform.** In `plugins/org.eclipse.osee.support.config/ClientServerTargetPlatform.target`:
   ```xml
   <plugin id="org.apache.cxf.cxf-rt-rs-sse"/>
   ```

3. **Add to feature.xml files.** The bundle must go in **two places**:

   **a) The dep feature** (`features/org.eclipse.osee.dep.feature/feature.xml`) — the master third-party dependency feature. All external bundles go here regardless of which plugins use them:
   ```xml
   <plugin
         id="org.apache.cxf.cxf-rt-rs-sse"
         download-size="0"
         install-size="0"
         version="0.0.0"
         unpack="false"/>
   ```

   **b) Every server/product feature that includes the consuming plugin.** Search for the plugin that uses the dependency (e.g., `org.eclipse.osee.orcs.rest`) in all `feature.xml` files and add the dependency bundle to those same features:
   ```xml
   <plugin
         id="org.apache.cxf.cxf-rt-rs-sse"
         download-size="0"
         install-size="0"
         version="0.0.0"
         unpack="false"/>
   ```

4. **Import in your plugin's MANIFEST.MF.** Add the package to `Import-Package`:
   ```
   javax.ws.rs.sse,
   ```

## Path 2: OSGi Converter (for non-Orbit dependencies)

If the dependency is NOT in Orbit, use the p2-maven-plugin to convert a Maven artifact into an OSGi bundle.

### Steps

1. **Verify the artifact is available in Maven:**
   - Depending on your environment, this may be a public Maven repository or an internal mirror.
      - Check your Maven settings.xml file for the configured Maven repositories. 
   - Search for the groupId:artifactId you need.

2. **Add to the OSGi converter pom.xml.** Edit `plugins/org.eclipse.osee.osgi.converter/pom.xml` and add an `<artifact>` entry:
   ```xml
   <artifact>
       <id>org.apache.cxf:cxf-rt-rs-sse:jar:3.6.6</id>
       <source>true</source>
   </artifact>
   ```

   For artifacts that need OSGi manifest adjustments:
   ```xml
   <artifact>
       <id>org.example:my-library:jar:1.0.0</id>
       <transitive>false</transitive>
       <source>true</source>
       <override>true</override>
       <instructions>
           <Import-Package>*;resolution:=optional</Import-Package>
           <Export-Package>*</Export-Package>
           <_removeheaders>Require-Capability</_removeheaders>
           <_reproducible>true</_reproducible>
       </instructions>
   </artifact>
   ```

   Key fields:
   - `<id>` — Maven coordinates: `groupId:artifactId:packaging:version`
   - `<transitive>false</transitive>` — Only include this artifact, not its transitive deps (add those explicitly if needed)
   - `<override>true</override>` — Overwrite if bundle already exists
   - `<instructions>` — BND instructions for OSGi manifest generation

3. **Run the converter build** to produce the OSGi bundle:
   ```bash
   cd plugins/org.eclipse.osee.osgi.converter
   mvn clean package
   ```

4. **Note the bundle symbolic name.** After the build, check the generated jar's `META-INF/MANIFEST.MF` for the `Bundle-SymbolicName`. This is what you'll use in feature.xml and target platform entries. It often matches the Maven artifactId but not always.

5. **Add to the target platform.** In `plugins/org.eclipse.osee.support.config/ClientServerTargetPlatform.target`:
   ```xml
   <plugin id="org.apache.cxf.cxf-rt-rs-sse"/>
   ```

6. **Add to feature.xml files.** The bundle must go in **two places**:

   **a) The dep feature** (`features/org.eclipse.osee.dep.feature/feature.xml`) — the master third-party dependency feature. All external bundles go here regardless of which plugins use them:
   ```xml
   <plugin
         id="org.apache.cxf.cxf-rt-rs-sse"
         download-size="0"
         install-size="0"
         version="0.0.0"
         unpack="false"/>
   ```

   **b) Every server/product feature that includes the consuming plugin.** Search for the plugin that uses the dependency (e.g., `org.eclipse.osee.orcs.rest`) in all `feature.xml` files and add the dependency bundle to those same features:
   ```xml
   <plugin
         id="org.apache.cxf.cxf-rt-rs-sse"
         download-size="0"
         install-size="0"
         version="0.0.0"
         unpack="false"/>
   ```

7. **Import in your plugin's MANIFEST.MF:**
   ```
   javax.ws.rs.sse,
   ```

## Which features need the dependency?

Run this search to find which features include your plugin:
```
grep -r "your.plugin.id" features/*/feature.xml
```

Add the dependency bundle to the dep feature AND every feature.xml that lists the consuming plugin.

## Common gotchas

- **Bundle symbolic name ≠ Maven artifactId.** Always verify the actual symbolic name in the generated jar's MANIFEST.MF after running the converter.
- **Version alignment.** When the platform already ships other bundles from the same library (e.g., `org.apache.cxf.cxf-core`), ensure your new bundle is from the same version. Mismatched versions cause `ClassNotFoundException` or `NoSuchMethodError` at runtime.
- **`version="0.0.0"` in feature.xml** means "any version available." This is standard practice — the actual version is resolved from what's in the target platform.
- **`<transitive>false</transitive>`** is important. If you set `true`, the converter will pull in all transitive Maven dependencies and wrap them as bundles too, which can pollute your runtime. Add transitive deps explicitly if needed.
- **Always add to dep feature.** Even if the bundle is only used by one plugin in one feature, it still goes in `dep.feature/feature.xml`. This is the canonical location for all third-party dependencies.

## Reference

- Orbit index: See `plugins/org.eclipse.osee.dep.parent/pom.xml` for the currently configured Orbit repository URL
- OSGi converter: `plugins/org.eclipse.osee.osgi.converter/pom.xml`
- Target platform: `plugins/org.eclipse.osee.support.config/ClientServerTargetPlatform.target`
- Dep feature: `features/org.eclipse.osee.dep.feature/feature.xml`
