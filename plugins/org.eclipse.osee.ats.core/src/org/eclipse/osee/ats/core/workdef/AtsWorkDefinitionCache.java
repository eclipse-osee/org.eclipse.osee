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
package org.eclipse.osee.ats.core.workdef;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IWorkDefinitionMatch;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionCache {

   // Cache the WorkDefinition used for each AbstractWorkflowId so don't have to recompute each time
   private final Map<Long, IWorkDefinitionMatch> awaUuidToWorkDefinition =
      new ConcurrentHashMap<Long, IWorkDefinitionMatch>();
   // Cache the WorkDefinition object for each WorkDefinition id so don't have to reload
   // This grows as WorkDefinitions are requested/loaded
   private final Map<String, IWorkDefinitionMatch> workDefIdToWorkDefintion =
      new ConcurrentHashMap<String, IWorkDefinitionMatch>();

   public void cache(IAtsWorkDefinition workDef, WorkDefinitionMatch match) {
      cache(workDef.getName(), match);
   }

   public void cache(String id, WorkDefinitionMatch match) {
      workDefIdToWorkDefintion.put(id, match);
   }

   public void cache(IAtsWorkItem workItem, IWorkDefinitionMatch match) {
      awaUuidToWorkDefinition.put(workItem.getId(), match);
   }

   public IWorkDefinitionMatch getWorkDefinition(IAtsWorkItem workItem) {
      return awaUuidToWorkDefinition.get(workItem.getId());
   }

   //IAtsWorkDefinition
   public IWorkDefinitionMatch getWorkDefinition(String id) {
      return workDefIdToWorkDefintion.get(id);
   }

   public Iterable<IWorkDefinitionMatch> getAllWorkDefinitions() {
      return workDefIdToWorkDefintion.values();
   }

   public void invalidate(IAtsWorkDefinition workDef) {
      workDefIdToWorkDefintion.remove(workDef.getName());
   }

   public void invalidate() {
      awaUuidToWorkDefinition.clear();
      workDefIdToWorkDefintion.clear();
   }

   public void cache(String id, IAtsWorkDefinition workDef) {
      WorkDefinitionMatch match = new WorkDefinitionMatch(id, null);
      match.setWorkDefinition(workDef);
      workDefIdToWorkDefintion.put(id, match);
   }

}
