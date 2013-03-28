/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.workdef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionCache {

   // Cache the WorkDefinition used for each AbstractWorkflowId so don't have to recompute each time
   private final Map<String, WorkDefinitionMatch> awaArtIdToWorkDefinition =
      new ConcurrentHashMap<String, WorkDefinitionMatch>();
   // Cache the WorkDefinition object for each WorkDefinition id so don't have to reload
   // This grows as WorkDefinitions are requested/loaded
   private final Map<String, WorkDefinitionMatch> workDefIdToWorkDefintion =
      new ConcurrentHashMap<String, WorkDefinitionMatch>();

   public void cache(IAtsWorkDefinition workDef, WorkDefinitionMatch match) {
      cache(workDef.getName(), match);
   }

   public void cache(String id, WorkDefinitionMatch match) {
      workDefIdToWorkDefintion.put(id, match);
   }

   public void cache(IAtsWorkItem workItem, WorkDefinitionMatch match) {
      awaArtIdToWorkDefinition.put(workItem.getHumanReadableId(), match);
   }

   public WorkDefinitionMatch getWorkDefinition(IAtsWorkItem workItem) {
      return awaArtIdToWorkDefinition.get(workItem.getHumanReadableId());
   }

   //IAtsWorkDefinition
   public WorkDefinitionMatch getWorkDefinition(String id) {
      return workDefIdToWorkDefintion.get(id);
   }

   public Iterable<WorkDefinitionMatch> getAllWorkDefinitions() {
      return workDefIdToWorkDefintion.values();
   }

   public void invalidate(IAtsWorkDefinition workDef) {
      workDefIdToWorkDefintion.remove(workDef.getName());
   }

   public void invalidateAll() {
      awaArtIdToWorkDefinition.clear();
      workDefIdToWorkDefintion.clear();
   }

}
