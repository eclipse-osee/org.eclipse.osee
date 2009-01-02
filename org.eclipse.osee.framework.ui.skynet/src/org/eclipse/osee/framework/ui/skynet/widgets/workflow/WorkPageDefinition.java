/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorkPageDefinition extends WorkItemWithChildrenDefinition {

   public static String ARTIFACT_NAME = "Work Page Definition";
   private String pageName;

   public WorkPageDefinition(String pageName, String pageId, String parentId) {
      this(pageId, pageName, pageId, parentId);
   }

   public WorkPageDefinition(String itemName, String pageName, String pageId, String parentId) {
      super(itemName, pageId, parentId);
      this.pageName = pageName;
   }

   public WorkPageDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact, artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(), (String) null), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_ID.getAttributeTypeName(), (String) null), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), (String) null));
      setType(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(), (String) null));
      loadWorkDataKeyValueMap(artifact);
      setPageName(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(),
            (String) null));

   }

   public boolean hasWorkRule(String ruleId) throws OseeCoreException {
      return getWorkItemDefinition(ruleId) != null;
   }

   /**
    * Returns work flow definition with the assumption that WorkFlowDefinition workId = pageWorkId minus pageName
    * 
    * @return WorkFlowDefinition
    * @throws OseeCoreException
    */
   public WorkFlowDefinition getWorkFlowDefinitionById() throws OseeCoreException {
      String id = getId().replace("." + pageName, "");
      WorkItemDefinition workItemDefinition = WorkItemDefinitionFactory.getWorkItemDefinition(id);
      if (workItemDefinition instanceof WorkFlowDefinition) {
         return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(id);
      }
      return null;
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException {
      Artifact art = super.toArtifact(writeType);
      // Only store start page if it's part of this definition
      if (pageName != null) {
         art.setSoleAttributeFromString(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(), pageName);
      }
      return art;
   }

   /**
    * @return the workItems
    * @throws OseeCoreException
    */

   public List<WorkItemDefinition> getWorkItemDefinitionsByType(String workType) throws OseeCoreException {
      List<WorkItemDefinition> wids = new ArrayList<WorkItemDefinition>();
      for (WorkItemDefinition workItemDefinition : getWorkItems(true)) {
         if (workItemDefinition.getType() != null && workItemDefinition.getType().equals(workType)) {
            wids.add(workItemDefinition);
         }
      }
      return wids;
   }

   public boolean isCompletePage() {
      return getPageName().equals("Completed");
   }

   public boolean isCancelledPage() {
      return getPageName().equals("Cancelled");
   }

   public boolean isInstanceof(String workPageDefinitionId) throws OseeCoreException {
      return isInstanceofRecurse(this, workPageDefinitionId);
   }

   private boolean isInstanceofRecurse(WorkPageDefinition workPageDefinition, String workPageDefinitionId) throws OseeCoreException {
      if (workPageDefinition.getId().equals(workPageDefinitionId)) return true;
      if (workPageDefinition.getParent() != null) {
         return isInstanceofRecurse((WorkPageDefinition) workPageDefinition.getParent(), workPageDefinitionId);
      }
      return false;
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
