/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XOptionHandler;

/**
 * @author Donald G. Dunne
 */
public class WorkPageDefinition extends WorkItemDefinition {

   public static String ARTIFACT_NAME = "Work Page Definition";

   private List<String> workItemIds = new ArrayList<String>();
   // Map to store XOptions that will override the default choices when the XWidget was declared
   private Map<String, XOptionHandler> workDefToXOptionHandler = new HashMap<String, XOptionHandler>();

   public WorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
   }

   public WorkPageDefinition(Artifact artifact) throws Exception {
      this(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_NAME.getAttributeTypeName(), ""),
            artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
                  WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), (String) null));
      for (Artifact art : artifact.getRelatedArtifacts(CoreRelationEnumeration.WorkItem__Child)) {
         String widId = art.getDescriptiveName();
         workItemIds.add(widId);
      }
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws Exception {
      Artifact art = super.toArtifact(writeType);
      List<Artifact> children = new ArrayList<Artifact>();
      for (WorkItemDefinition wid : getWorkItems()) {
         Artifact widArt = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(wid.getId());
         if (widArt == null) {
            throw new IllegalStateException(
                  "While processing Work Page \"" + getId() + "\":  No Artifact found for WorkItemDefinition \"" + wid.getId() + "\"");
         }
         children.add(widArt);
      }
      // This supports both relating new children and when WriteType.Overwrite of updating
      art.setRelations(CoreRelationEnumeration.WorkItem__Child, children);
      //      System.err.println("WorkPageDefinition - need to call to reorder relations");
      // TODO reorder relations
      //      art.setRelationOrder(CoreRelationEnumeration.WorkItem__Child, children);
      return art;
   }

   public void addWorkItem(String workItemDefintionId) {
      workItemIds.add(workItemDefintionId);
   }

   public XOptionHandler getXOptionHandler(WorkItemDefinition workItemDefinition) {
      return workDefToXOptionHandler.get(workItemDefinition);
   }

   /**
    * Adds the workItemDefinition and logs the xOptions that will be used to override the default configured options.
    * This does NOT change the default configured options, just overrides for this page's display.
    * 
    * @param workItemDefinition
    * @param xOption
    */
   public void addWorkItem(String workItemDefintionId, XOption... xOption) {
      addWorkItem(workItemDefintionId);
      if (xOption.length > 0) {
         workDefToXOptionHandler.put(workItemDefintionId, new XOptionHandler(xOption));
      }
   }

   public void removeWorkItem(String workItemDefintionId) {
      workItemIds.remove(workItemDefintionId);
   }

   /**
    * @return the workItems
    */
   public List<WorkItemDefinition> getWorkItems() throws Exception {
      return getWorkItems(false);
   }

   /**
    * @return the workItems
    */

   public List<WorkItemDefinition> getWorkItems(boolean includeInherited) throws Exception {
      List<WorkItemDefinition> wids = new ArrayList<WorkItemDefinition>();
      getWorkItemsInherited(wids, includeInherited);
      return wids;

   }

   private void getWorkItemsInherited(List<WorkItemDefinition> workItemDefinitions, boolean includeInherited) throws Exception {
      workItemDefinitions.addAll(WorkItemDefinitionFactory.getWorkItemDefinition(workItemIds));
      if (includeInherited && getParentId() != null) {
         WorkPageDefinition widParent =
               (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(getParentId());
         if (widParent != null) widParent.getWorkItemsInherited(workItemDefinitions, includeInherited);
      }
   }

   public WorkItemDefinition getWorkItemDefinition(String id) throws Exception {
      if (workItemIds.contains(id)) return WorkItemDefinitionFactory.getWorkItemDefinition(id);
      return null;
   }

   /**
    * @param workItems the workItems to set
    */
   public void setWorkItems(List<String> workItemDefintionIds) {
      this.workItemIds.clear();
      this.workItemIds.addAll(workItemDefintionIds);
   }

   public boolean isCompletePage() {
      return getName().equals("Completed");
   }

   public boolean isCancelledPage() {
      return getName().equals("Cancelled");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition#getArtifactTypeName()
    */
   @Override
   public String getArtifactTypeName() {
      return ARTIFACT_NAME;
   }

}
