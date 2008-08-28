/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;

/**
 * @author Donald G. Dunne
 */
public class WorkPageDefinition extends WorkItemDefinition {

   public static String ARTIFACT_NAME = "Work Page Definition";
   private String pageName;

   private final List<String> workItemIds = new ArrayList<String>();

   public WorkPageDefinition(String pageName, String pageId, String parentId) {
      this(pageId, pageName, pageId, parentId);
   }

   public WorkPageDefinition(String itemName, String pageName, String pageId, String parentId) {
      super(itemName, pageId, parentId);
      this.pageName = pageName;
   }

   public WorkPageDefinition(Artifact artifact) throws OseeCoreException, SQLException {
      this(artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(), (String) null), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_ID.getAttributeTypeName(), (String) null), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), (String) null));
      setType(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(), (String) null));
      setPageName(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(),
            (String) null));
      for (Artifact art : artifact.getRelatedArtifacts(CoreRelationEnumeration.WorkItem__Child)) {
         String widId = art.getSoleAttributeValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), (String) null);
         workItemIds.add(widId);
      }
   }

   public boolean hasWorkRule(String ruleId) throws OseeCoreException, SQLException {
      return getWorkItemDefinition(ruleId) != null;
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException, SQLException {
      Artifact art = super.toArtifact(writeType);
      List<Artifact> children = new ArrayList<Artifact>();
      for (WorkItemDefinition wid : getWorkItems(false)) {
         Artifact widArt = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(wid.getId());
         if (widArt == null) {
            throw new IllegalStateException(
                  "While processing Work Page \"" + getId() + "\":  No Artifact found for WorkItemDefinition \"" + wid.getId() + "\"");
         }
         children.add(widArt);
      }
      // Only store start page if it's part of this definition
      if (pageName != null) {
         art.setSoleAttributeFromString(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(), pageName);
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

   /**
    * @return the workItems
    * @throws OseeCoreException TODO
    * @throws SQLException TODO
    */

   public List<WorkItemDefinition> getWorkItems(boolean includeInherited) throws OseeCoreException, SQLException {
      List<WorkItemDefinition> wids = new ArrayList<WorkItemDefinition>();
      getWorkItemsInherited(wids, includeInherited);
      return wids;

   }

   private void getWorkItemsInherited(List<WorkItemDefinition> workItemDefinitions, boolean includeInherited) throws OseeCoreException, SQLException {
      workItemDefinitions.addAll(WorkItemDefinitionFactory.getWorkItemDefinition(workItemIds));
      if (includeInherited && getParentId() != null) {
         WorkPageDefinition widParent =
               (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(getParentId());
         if (widParent != null) widParent.getWorkItemsInherited(workItemDefinitions, includeInherited);
      }
   }

   public WorkItemDefinition getWorkItemDefinition(String id) throws OseeCoreException, SQLException {
      if (workItemIds.contains(id)) return WorkItemDefinitionFactory.getWorkItemDefinition(id);
      return null;
   }

   public List<WorkItemDefinition> getWorkItemDefinitionsByType(String workType) throws OseeCoreException, SQLException {
      List<WorkItemDefinition> wids = new ArrayList<WorkItemDefinition>();
      for (WorkItemDefinition workItemDefinition : getWorkItems(true)) {
         if (workItemDefinition.getType() != null && workItemDefinition.getType().equals(workType)) {
            wids.add(workItemDefinition);
         }
      }
      return wids;
   }

   /**
    * @param workItems the workItems to set
    */
   public void setWorkItems(List<String> workItemDefintionIds) {
      this.workItemIds.clear();
      this.workItemIds.addAll(workItemDefintionIds);
   }

   public boolean isCompletePage() {
      return getPageName().equals("Completed");
   }

   public boolean isCancelledPage() {
      return getPageName().equals("Cancelled");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition#getArtifactTypeName()
    */
   @Override
   public String getArtifactTypeName() {
      return ARTIFACT_NAME;
   }

   public String getPageName() {
      return pageName;
   }

   public void setPageName(String pageName) {
      this.pageName = pageName;
   }

}
