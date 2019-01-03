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
package org.eclipse.osee.ats.ide.workdef.viewer.model;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * A rectangular shape.
 * 
 * @author Donald G. Dunne
 */
public class StateDefShape extends RectangleShape {

   private final IAtsStateDefinition stateDef;
   public static String START_PAGE = "Start Page";
   public static String NAME = "Name";

   public StateDefShape(IAtsStateDefinition stateDef) {
      this.stateDef = stateDef;
   }

   @Override
   public void setWorkflowDiagram(WorkDefinitionDiagram workflowDiagram) {
      super.setWorkflowDiagram(workflowDiagram);
   }

   @Override
   protected void initializePropertyDescriptors(List<IPropertyDescriptor> descriptorList) {
      super.initializePropertyDescriptors(descriptorList);
      descriptorList.add(new TextPropertyDescriptor(NAME, stateDef.getName()));
      descriptorList.add(new TextPropertyDescriptor(START_PAGE, isStartState() ? "Yes" : "No"));
   }

   @Override
   public Object getPropertyValue(Object propertyId) {
      if (NAME.equals(propertyId)) {
         return stateDef.getName();
      } else if (START_PAGE.equals(propertyId)) {
         return isStartState() ? "Yes" : "No";
      }
      return super.getPropertyValue(propertyId);
   }

   public boolean isStartState() {
      return getWorkflowDiagram().getWorkDefinition().getStartState().equals(stateDef);
   }

   public IAtsStateDefinition getStateDefinition() {
      return stateDef;
   }

   @Override
   public Result validForSave() {
      return Result.FalseResult;
   }

   public boolean isCancelledState() {
      return getStateDefinition().getStateType().isCancelledState();
   }

   public boolean isCompletedState() {
      return getStateDefinition().getStateType().isCompletedState();
   }

   @Override
   public String getName() {
      return stateDef.getName();
   }

   @Override
   public String getToolTip() {
      return stateDef.toString();
   }

   @Override
   public String toString() {
      return getId();
   }

   public String getId() {
      return getName();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof StateDefShape) {
         return ((StateDefShape) obj).getId().equals(getId());
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   @Override
   public void setPropertyValue(Object propertyId, Object value) {
      initializePropertyValues();
      super.setPropertyValue(propertyId, value);
   }

}
