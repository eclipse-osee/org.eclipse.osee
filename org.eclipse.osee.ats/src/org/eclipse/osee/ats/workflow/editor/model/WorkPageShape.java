/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.model;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * A rectangular shape.
 * 
 * @author Donald G. Dunne
 */
public class WorkPageShape extends RectangleShape {

   private final WorkPageDefinition workPageDefinition;
   private static String[] attributeProperties =
         new String[] {WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(),
               WorkItemAttributes.WORK_ID.getAttributeTypeName(),
               WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName()};
   public static String START_PAGE = "Start Page";
   private Artifact artifact;
   public static enum StartPageEnum {
      Yes, No
   };

   public WorkPageShape() {
      this(new WorkPageDefinition("New" + AtsUtil.getAtsDeveloperIncrementingNum(), "NEW", null));
   }

   public WorkPageShape(WorkPageDefinition workPageDefinition) {
      this.workPageDefinition = workPageDefinition;
      try {
         artifact = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(workPageDefinition.getId());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void setWorkflowDiagram(WorkflowDiagram workflowDiagram) {
      super.setWorkflowDiagram(workflowDiagram);
      if (getId().equals("NEW")) {
         setPropertyValue(
               WorkItemAttributes.WORK_ID.getAttributeTypeName(),
               workflowDiagram.getWorkFlowDefinition().getId() + "." + getPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName()));
      }
   }

   @Override
   protected void initializePropertyDescriptors(List<IPropertyDescriptor> descriptorList) {
      super.initializePropertyDescriptors(descriptorList);
      for (String type : attributeProperties) {
         descriptorList.add(new TextPropertyDescriptor(type, type)); // id and description pair
      }
      descriptorList.add(new ComboBoxPropertyDescriptor(START_PAGE, START_PAGE, new String[] {StartPageEnum.Yes.name(),
            StartPageEnum.No.name()}));
   }

   @Override
   protected void initializePropertyValues() throws OseeCoreException {
      if (propertyValues == null) {
         super.initializePropertyValues();
         super.setPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(),
               workPageDefinition.getPageName());
         super.setPropertyValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), workPageDefinition.getId());
         super.setPropertyValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(),
               workPageDefinition.getParentId() == null ? "" : workPageDefinition.getParentId());
      }
   }

   public boolean isStartPage() {
      if (getPropertyValue(START_PAGE) == null) return false;
      return ((Integer) getPropertyValue(START_PAGE)) == StartPageEnum.Yes.ordinal();
   }

   public void setStartPage(boolean set) {
      setPropertyValue(START_PAGE, set ? StartPageEnum.Yes.ordinal() : StartPageEnum.No.ordinal());
   }

   @Override
   public Object getPropertyValue(Object propertyId) {
      try {
         initializePropertyValues();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return super.getPropertyValue(propertyId);
   }

   public Artifact getArtifact() throws OseeCoreException {
      return artifact;
   }

   /**
    * @return the workPageDefinition
    */
   public WorkPageDefinition getWorkPageDefinition() {
      return workPageDefinition;
   }

   @Override
   public Result validForSave() throws OseeCoreException {
      try {
         String pageName = (String) getPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName());
         if (pageName == null || pageName.equals("")) return new Result(
               WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName() + " can not be null");
         String pageId = (String) getPropertyValue(WorkItemAttributes.WORK_ID.getAttributeTypeName());
         if (pageId == null || pageId.equals("")) return new Result(
               WorkItemAttributes.WORK_ID.getAttributeTypeName() + " can not be null");
         String parentPageId = (String) getPropertyValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName());
         if (parentPageId != null && !parentPageId.equals("")) {
            if (WorkItemDefinitionFactory.getWorkItemDefinition(parentPageId) == null) return new Result(
                  "Parent Id " + parentPageId + " Work Page Definition must exist and does not.");
         }
      } catch (OseeCoreException ex) {
         return new Result("Exception in validation " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   public boolean isInstanceof(String workPageDefinitionId) throws OseeCoreException {
      return isInstanceofRecurse(workPageDefinition, workPageDefinitionId);
   }

   private boolean isInstanceofRecurse(WorkPageDefinition workPageDefinition, String workPageDefinitionId) throws OseeCoreException {
      if (workPageDefinition.getId().equals(workPageDefinitionId)) return true;
      if (workPageDefinition.getParent() != null) {
         return isInstanceofRecurse((WorkPageDefinition) workPageDefinition.getParent(), workPageDefinitionId);
      }
      return false;
   }

   public boolean isCancelledState() throws OseeCoreException {
      return isInstanceof(AtsCancelledWorkPageDefinition.ID);
   }

   public boolean isCompletedState() throws OseeCoreException {
      return isInstanceof(AtsCompletedWorkPageDefinition.ID);
   }

   @Override
   public String getName() {
      return (String) getPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName());
   }

   @Override
   public String getToolTip() {
      return workPageDefinition.toString();
   }

   @Override
   public String toString() {
      return getId();
   }

   public String getId() {
      return (String) getPropertyValue(WorkItemAttributes.WORK_ID.getAttributeTypeName());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof WorkPageShape) {
         return ((WorkPageShape) obj).getId().equals(getId());
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   @Override
   public void setPropertyValue(Object propertyId, Object value) {
      try {
         initializePropertyValues();
         if (WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName().equals(propertyId)) {
            super.setPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(), value);
            firePropertyChange(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(), null, value);
            setPropertyValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(),
                  getWorkflowDiagram().getWorkFlowDefinition().getId() + "." + value);
         } else if (WorkItemAttributes.WORK_ID.getAttributeTypeName().equals(propertyId)) {
            super.setPropertyValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), value);
            firePropertyChange(WorkItemAttributes.WORK_ID.getAttributeTypeName(), null, value);
         } else if (WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName().equals(propertyId)) {
            super.setPropertyValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), value);
            firePropertyChange(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), null, value);
         } else if (START_PAGE.equals(propertyId)) {
            super.setPropertyValue(START_PAGE, value);
            firePropertyChange(START_PAGE, null, value);
         } else {
            super.setPropertyValue(propertyId, value);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Result doSave(SkynetTransaction transaction) throws OseeCoreException {
      String name = (String) getPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName());
      String workId = (String) getPropertyValue(WorkItemAttributes.WORK_ID.getAttributeTypeName());
      String parentWorkId = (String) getPropertyValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName());
      workPageDefinition.setPageName(name);
      workPageDefinition.setId(workId);
      workPageDefinition.setParentId(parentWorkId);
      Artifact artifact = getArtifact();
      if (artifact == null) {
         artifact = workPageDefinition.toArtifact(WriteType.New);
      } else {
         artifact.setSoleAttributeValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(), name);
         artifact.setSoleAttributeValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), workId);
         if (parentWorkId == null || parentWorkId.equals("")) {
            artifact.deleteSoleAttribute(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName());
         } else {
            artifact.setSoleAttributeValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), parentWorkId);
         }
      }
      artifact.setName(workId);
      AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(artifact, transaction);
      WorkItemDefinitionFactory.deCache(workPageDefinition);
      artifact.persist(transaction);
      return Result.TrueResult;
   }
}
