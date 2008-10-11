/*
 * Created on Oct 10, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

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
      for (Artifact art : artifact.getRelatedArtifacts(CoreRelationEnumeration.WorkItem__Child)) {
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
      art.setRelations(CoreRelationEnumeration.WorkItem__Child, children);
      art.setRelationOrder(CoreRelationEnumeration.WorkItem__Child, children);
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
