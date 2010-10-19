/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.model;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * A rectangular shape.
 * 
 * @author Donald G. Dunne
 */
public class WorkPageShape extends RectangleShape {
   private static final String[] attributeProperties = new String[] { //
      CoreAttributeTypes.WorkPageName.getName(), //
         CoreAttributeTypes.WorkId.getName(), //
         CoreAttributeTypes.WorkParentId.getName(),//
      };

   private final WorkPageDefinition workPageDefinition;

   public static String START_PAGE = "Start Page";
   private Artifact artifact;
   public static enum StartPageEnum {
      Yes,
      No
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
            CoreAttributeTypes.WorkId.getName(),
            workflowDiagram.getWorkFlowDefinition().getId() + "." + getPropertyValue(CoreAttributeTypes.WorkPageName.getName()));
      }
   }

   @Override
   protected void initializePropertyDescriptors(List<IPropertyDescriptor> descriptorList) {
      super.initializePropertyDescriptors(descriptorList);
      for (String type : attributeProperties) {
         descriptorList.add(new TextPropertyDescriptor(type, type)); // id and description pair
      }
      descriptorList.add(new ComboBoxPropertyDescriptor(START_PAGE, START_PAGE, new String[] {
         StartPageEnum.Yes.name(),
         StartPageEnum.No.name()}));
   }

   @Override
   protected void initializePropertyValues() throws OseeCoreException {
      if (propertyValues == null) {
         super.initializePropertyValues();
         super.setPropertyValue(CoreAttributeTypes.WorkPageName.getName(), workPageDefinition.getPageName());
         super.setPropertyValue(CoreAttributeTypes.WorkId.getName(), workPageDefinition.getId());
         super.setPropertyValue(CoreAttributeTypes.WorkParentId.getName(),
            workPageDefinition.getParentId() == null ? "" : workPageDefinition.getParentId());
      }
   }

   public boolean isStartPage() {
      if (getPropertyValue(START_PAGE) == null) {
         return false;
      }
      return (Integer) getPropertyValue(START_PAGE) == StartPageEnum.Yes.ordinal();
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

   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * @return the workPageDefinition
    */
   public WorkPageDefinition getWorkPageDefinition() {
      return workPageDefinition;
   }

   @Override
   public Result validForSave() {
      try {
         String pageName = (String) getPropertyValue(CoreAttributeTypes.WorkPageName.getName());
         if (!Strings.isValid(pageName)) {
            return new Result(CoreAttributeTypes.WorkPageName.getName() + " can not be null");
         }
         String pageId = (String) getPropertyValue(CoreAttributeTypes.WorkId.getName());
         if (!Strings.isValid(pageId)) {
            return new Result(CoreAttributeTypes.WorkId.getName() + " can not be null");
         }
         String parentPageId = (String) getPropertyValue(CoreAttributeTypes.WorkParentId.getName());
         if (Strings.isValid(parentPageId)) {
            if (WorkItemDefinitionFactory.getWorkItemDefinition(parentPageId) == null) {
               return new Result("Parent Id " + parentPageId + " Work Page Definition must exist and does not.");
            }
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
      if (workPageDefinition.getId().equals(workPageDefinitionId)) {
         return true;
      }
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
      return (String) getPropertyValue(CoreAttributeTypes.WorkPageName.getName());
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
      return (String) getPropertyValue(CoreAttributeTypes.WorkId.getName());
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
         if (CoreAttributeTypes.WorkPageName.getName().equals(propertyId)) {
            super.setPropertyValue(CoreAttributeTypes.WorkPageName.getName(), value);
            firePropertyChange(CoreAttributeTypes.WorkPageName.getName(), null, value);
            setPropertyValue(CoreAttributeTypes.WorkId.getName(),
               getWorkflowDiagram().getWorkFlowDefinition().getId() + "." + value);
         } else if (CoreAttributeTypes.WorkId.getName().equals(propertyId)) {
            super.setPropertyValue(CoreAttributeTypes.WorkId.getName(), value);
            firePropertyChange(CoreAttributeTypes.WorkId.getName(), null, value);
         } else if (CoreAttributeTypes.WorkParentId.getName().equals(propertyId)) {
            super.setPropertyValue(CoreAttributeTypes.WorkParentId.getName(), value);
            firePropertyChange(CoreAttributeTypes.WorkParentId.getName(), null, value);
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
      String name = (String) getPropertyValue(CoreAttributeTypes.WorkPageName.getName());
      String workId = (String) getPropertyValue(CoreAttributeTypes.WorkId.getName());
      String parentWorkId = (String) getPropertyValue(CoreAttributeTypes.WorkParentId.getName());

      workPageDefinition.setPageName(name);
      workPageDefinition.setId(workId);
      workPageDefinition.setParentId(parentWorkId);
      Artifact artifact = getArtifact();
      if (artifact == null) {
         artifact = workPageDefinition.toArtifact(WriteType.New);
      } else {
         artifact.setSoleAttributeValue(CoreAttributeTypes.WorkPageName, name);
         artifact.setSoleAttributeValue(CoreAttributeTypes.WorkId, workId);
         if (!Strings.isValid(parentWorkId)) {
            artifact.deleteSoleAttribute(CoreAttributeTypes.WorkParentId);
         } else {
            artifact.setSoleAttributeValue(CoreAttributeTypes.WorkParentId, parentWorkId);
         }
      }
      artifact.setName(workId);
      AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(artifact, transaction);
      WorkItemDefinitionFactory.deCache(workPageDefinition);
      artifact.persist(transaction);
      return Result.TrueResult;
   }
}
