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

package org.eclipse.osee.ats.core.task.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.task.create.IAtsTaskSetDefinitionProvider;
import org.eclipse.osee.ats.api.task.create.IAtsTaskSetDefinitionProviderService;

/**
 * Service to retrieve all task set definitions that have been registered. Should not be used by applications, only by
 * the TaskDefinitionService
 *
 * @author Donald G. Dunne
 */
public class AtsTaskSetDefinitionProviderService implements IAtsTaskSetDefinitionProviderService {

   private static Map<Long, CreateTasksDefinitionBuilder> idToTaskSetDef = new HashMap<>();
   private static final Collection<IAtsTaskSetDefinitionProvider> taskSetDefProviders = new ArrayList<>();
   private static final Collection<IAtsTaskSetDefinitionProvider> taskSetDefProviderProcessed = new ArrayList<>();
   private static AtsTaskSetDefinitionProvider atsTaskSetDefProv;

   @Override
   public void addTaskSetDefinitionProvider(IAtsTaskSetDefinitionProvider taskSetDefProvider) {
      AtsTaskSetDefinitionProviderService.taskSetDefProviders.add(taskSetDefProvider);
   }

   public void ensureLoaded() {
      // Add default
      if (!taskSetDefProviderProcessed.contains(atsTaskSetDefProv)) {
         atsTaskSetDefProv = new AtsTaskSetDefinitionProvider();
         taskSetDefProviders.add(atsTaskSetDefProv);
      }
      // Add any not processed
      for (IAtsTaskSetDefinitionProvider workDefProvider : taskSetDefProviders) {
         if (!taskSetDefProviderProcessed.contains(workDefProvider)) {
            for (CreateTasksDefinitionBuilder workDef : workDefProvider.getTaskSetDefinitions()) {
               idToTaskSetDef.put(workDef.getId(), workDef);
            }
            taskSetDefProviderProcessed.add(workDefProvider);
         }
      }
   }

   @Override
   public CreateTasksDefinitionBuilder getTaskSetDefinition(Long id) {
      ensureLoaded();
      return idToTaskSetDef.get(id);
   }

   @Override
   public Collection<CreateTasksDefinitionBuilder> getAll() {
      ensureLoaded();
      return idToTaskSetDef.values();
   }

   @Override
   public void addTaskSetDefinition(CreateTasksDefinitionBuilder workDef) {
      ensureLoaded();
      idToTaskSetDef.put(workDef.getId(), workDef);
   }

   @Override
   public CreateTasksDefinitionBuilder getTaskSetDefinition(AtsTaskDefToken taskDefToken) {
      return getTaskSetDefinition(taskDefToken.getId());
   }

}
