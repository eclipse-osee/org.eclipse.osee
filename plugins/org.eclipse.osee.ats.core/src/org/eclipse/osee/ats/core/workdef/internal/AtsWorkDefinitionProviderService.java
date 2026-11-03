/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.core.workdef.internal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProviderService;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Collects all OSGI-registered {@link IAtsWorkDefinitionBuilder} services, builds the applicable ones, and
 * serves the merged set. Should not be used by applications, only by the WorkDefinitionService.
 * <p>
 * Builders are retained (not just their built result) so {@link #clearCaches()} can rebuild the applicable set on
 * demand. This matters because {@link IAtsWorkDefinitionBuilder#isApplicable()} is not constant: the demo builders
 * gate on {@code DemoUtil.isDemoDatabase()} (system property {@code osee.db == "orgdemo"}), which the demo DB-config
 * op flips <i>after</i> the builders have already bound. Rebuilding on clear re-evaluates {@code isApplicable()} so a
 * server that becomes a demo database at runtime (e.g. the CI populate path, which does not launch with
 * {@code -Dosee.db=orgdemo}) still contributes the demo Work Definitions.
 *
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionProviderService.class, immediate = true)
public class AtsWorkDefinitionProviderService implements IAtsWorkDefinitionProviderService {

   /** All bound builders, retained so the applicable set can be rebuilt when caches are cleared. */
   private static final List<IAtsWorkDefinitionBuilder> builders = new CopyOnWriteArrayList<>();
   /** WorkDefs added programmatically (tests), kept across rebuilds. */
   private static final Map<Long, WorkDefinition> added = new ConcurrentHashMap<>();
   /** The served set: applicable builders' output plus the programmatic adds. Rebuilt lazily. */
   private static final Map<Long, WorkDefinition> idToWorkDef = new ConcurrentHashMap<>();
   private static volatile boolean loaded = false;

   @Reference(service = //
   IAtsWorkDefinitionBuilder.class, //
      cardinality = ReferenceCardinality.MULTIPLE, //
      policy = ReferencePolicy.DYNAMIC, //
      policyOption = ReferencePolicyOption.GREEDY, //
      bind = "addWorkDefinitionBuilder", //
      unbind = "removeWorkDefinitionBuilder" //
   )
   public void addWorkDefinitionBuilder(IAtsWorkDefinitionBuilder builder) {
      builders.add(builder);
      loaded = false;
   }

   public void removeWorkDefinitionBuilder(IAtsWorkDefinitionBuilder builder) {
      builders.remove(builder);
      loaded = false;
   }

   public AtsWorkDefinitionProviderService() {
      // for osgi
   }

   /**
    * Build the applicable set from the retained builders (re-evaluating {@link IAtsWorkDefinitionBuilder#isApplicable()}
    * each time) plus any programmatic adds. Enforces unique ids and fails loud on build errors.
    */
   private static synchronized void ensureLoaded() {
      if (loaded) {
         return;
      }
      Map<Long, WorkDefinition> rebuilt = new ConcurrentHashMap<>();
      for (IAtsWorkDefinitionBuilder builder : builders) {
         if (!builder.isApplicable()) {
            continue;
         }
         WorkDefinition workDef = builder.build();
         if (workDef == null) {
            continue;
         }
         WorkDefinition existing = rebuilt.get(workDef.getId());
         if (existing != null && !existing.getName().equals(workDef.getName())) {
            throw new OseeArgumentException("Work Definitions must have unique id; duplicate for [%s]-[%s]",
               workDef.getId(), workDef.getName());
         }
         if (workDef.getResults().isErrors()) {
            throw new OseeArgumentException("Exception Building WorkDef [%s]: %s", workDef.getName(),
               workDef.getResults().toString());
         }
         rebuilt.put(workDef.getId(), workDef);
      }
      rebuilt.putAll(added);
      idToWorkDef.clear();
      idToWorkDef.putAll(rebuilt);
      loaded = true;
   }

   @Override
   public WorkDefinition getWorkDefinition(Long id) {
      ensureLoaded();
      return idToWorkDef.get(id);
   }

   @Override
   public Collection<WorkDefinition> getAll() {
      ensureLoaded();
      return idToWorkDef.values();
   }

   @Override
   public void addWorkDefinition(WorkDefinition workDef) {
      added.put(workDef.getId(), workDef);
      loaded = false;
   }

   @Override
   public void clearCaches() {
      // Force the applicable set to be rebuilt from the retained builders on next access, re-evaluating
      // isApplicable(). Required so a server that becomes a demo database at runtime picks up the demo WorkDefs.
      loaded = false;
   }

}
