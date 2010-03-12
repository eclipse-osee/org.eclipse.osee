/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public abstract class WorkItemWithChildrenDefinition extends WorkItemDefinition {

   private final List<String> workItemIds = new ArrayList<String>();

   public WorkItemWithChildrenDefinition(String itemName, String pageId, String parentId) {
      super(itemName, pageId, parentId);
   }

   public WorkItemWithChildrenDefinition(Artifact artifact, String itemName, String pageName, String pageId, String parentId) throws OseeCoreException {
      this(itemName, pageId, parentId);
      for (Artifact art : artifact.getRelatedArtifacts(CoreRelationTypes.WorkItem__Child)) {
         String widId = art.getSoleAttributeValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), (String) null);
         workItemIds.add(widId);
      }
   }

   public List<WorkItemDefinition> getWorkItems(boolean includeInherited) throws OseeCoreException {
      List<WorkItemDefinition> wids = new ArrayList<WorkItemDefinition>();
      getWorkItemsInherited(wids, includeInherited);
      return wids;

   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException {
      Artifact art = super.toArtifact(writeType);
      List<Artifact> children = new ArrayList<Artifact>();
      for (WorkItemDefinition wid : getWorkItems(false)) {
         Artifact widArt = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(wid.getId());
         if (widArt == null) {
            throw new IllegalStateException(
                  "While processing Work Item \"" + getId() + "\":  No Artifact found for WorkItemDefinition \"" + wid.getId() + "\"");
         }
         children.add(widArt);
      }
      // This supports both relating new children and when WriteType.Overwrite of updating
      art.setRelations(CoreRelationTypes.WorkItem__Child, children);
      art.setRelationOrder(CoreRelationTypes.WorkItem__Child, children);
      return art;
   }

   public void addWorkItem(String workItemDefintionId) {
      workItemIds.add(workItemDefintionId);
   }

   public void removeWorkItem(String workItemDefintionId) {
      workItemIds.remove(workItemDefintionId);
   }

   private void getWorkItemsInherited(List<WorkItemDefinition> workItemDefinitions, boolean includeInherited) throws OseeCoreException {
      workItemDefinitions.addAll(WorkItemDefinitionFactory.getWorkItemDefinition(workItemIds));
      if (includeInherited && getParentId() != null) {
         WorkItemWithChildrenDefinition widParent =
               (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(getParentId());
         if (widParent != null) widParent.getWorkItemsInherited(workItemDefinitions, includeInherited);
      }
   }

   public WorkItemDefinition getWorkItemDefinition(String id) throws OseeCoreException {
      if (workItemIds.contains(id)) {
         return WorkItemDefinitionFactory.getWorkItemDefinition(id);
      }
      return null;
   }

   /**
    * @param workItems the workItems to set
    */
   public void setWorkItems(List<String> workItemDefintionIds) {
      this.workItemIds.clear();
      this.workItemIds.addAll(workItemDefintionIds);
   }

}
