/*******************************************************************************
 * Copyright (c) 2004, 2005 Donald G. Dunne and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Donald G. Dunne - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.editor.model;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;
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

   private final WorkPageDefinition workPageDefinition;
   private static String NAME = "Name";
   private static String[] attributeProperties =
         new String[] {NAME, WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName(),
               WorkItemAttributes.WORK_ID.getAttributeTypeName(),
               WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName()};
   public static String START_PAGE = "Start Page";
   public static enum StartPageEnum {
      Yes, No
   };

   public WorkPageShape() {
      this(new WorkPageDefinition("New", "ats.page." + GUID.generateGuidStr(), null));
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
         Artifact artifact = getArtifact();
         if (artifact != null) {
            for (String type : attributeProperties) {
               super.setPropertyValue(type, artifact.getAttributesToString(type));
            }
         }
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
      if (workPageDefinition != null) {
         return WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(workPageDefinition.getId());
      }
      return null;
   }

   /**
    * @return the workPageDefinition
    */
   public WorkPageDefinition getWorkPageDefinition() {
      return workPageDefinition;
   }

   public WorkPageShape(WorkPageDefinition workPageDefinition) {
      this.workPageDefinition = workPageDefinition;
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
      return workPageDefinition.getPageName();
   }

   @Override
   public String getToolTip() {
      return workPageDefinition.toString();
   }

   @Override
   public String toString() {
      return workPageDefinition.toString();
   }

   public String getId() {
      return workPageDefinition.getId();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof WorkPageShape) {
         return ((WorkPageShape) obj).getId().equals(getId());
      }
      return super.equals(obj);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.Shape#setPropertyValue(java.lang.Object, java.lang.Object)
    */
   @Override
   public void setPropertyValue(Object propertyId, Object value) {
      try {
         initializePropertyValues();
         if (NAME.equals(propertyId)) {
            super.setPropertyValue(NAME, value);
         } else if (WorkItemAttributes.WORK_ID.getAttributeTypeName().equals(propertyId)) {
            super.setPropertyValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), value);
         } else if (WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName().equals(propertyId)) {
            super.setPropertyValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), value);
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

}
