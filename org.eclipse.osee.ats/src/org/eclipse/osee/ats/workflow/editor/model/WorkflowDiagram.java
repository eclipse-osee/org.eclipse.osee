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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;

/**
 * A container for multiple shapes. This is the "root" of the model data structure.
 * 
 * @author Donald G Dunne
 */
public class WorkflowDiagram extends ModelElement {

   /** Property ID to use when a child is added to this diagram. */
   public static final String CHILD_ADDED_PROP = "WorkflowDiagram.ChildAdded";
   /** Property ID to use when a child is removed from this diagram. */
   public static final String CHILD_REMOVED_PROP = "WorkflowDiagram.ChildRemoved";
   private final List<Shape> shapes = new ArrayList<Shape>();
   private final WorkFlowDefinition workFlowDefinition;

   public WorkflowDiagram(WorkFlowDefinition workFlowDefinition) {
      super();
      this.workFlowDefinition = workFlowDefinition;
   }

   @Override
   public Result validForSave() {

      // Validate # Completed states
      int num = 0;
      for (Shape shape : getChildren()) {
         num += (shape instanceof CompletedWorkPageShape) ? 1 : 0;
      }
      if (num > 1 || num == 0) return new Result("Must have only 1 Completed state; Currently " + num);

      // Validate # Cancelled states
      num = 0;
      for (Shape shape : getChildren()) {
         num += (shape instanceof CancelledWorkPageShape) ? 1 : 0;
      }
      if (num > 1 || num == 0) return new Result("Must have only 1 Cancelled state; Currently " + num);

      // Validate # other states
      num = 0;
      for (Shape shape : getChildren()) {
         num += (!(shape instanceof CompletedWorkPageShape) && !(shape instanceof CancelledWorkPageShape)) ? 1 : 0;
      }
      if (num == 0) return new Result("Must have > 0 states; Currently " + num);

      // Validate # other states
      num = 0;
      for (Shape shape : getChildren()) {
         if (shape instanceof WorkPageShape) {
            num += ((WorkPageShape) shape).isStartPage() ? 1 : 0;
         }
      }
      if (num > 1 || num == 0) return new Result("Must have 1 start page; Currently " + num);

      // Validate state names
      List<String> stateNames = new ArrayList<String>();
      for (Shape shape : getChildren()) {
         if (shape instanceof WorkPageShape) {
            String name =
                  (String) ((WorkPageShape) shape).getPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName());
            if (stateNames.contains(name)) {
               return new Result("Workflow can not have more than one state of same name. Multiple of " + name);
            }
            stateNames.add(name);
         }
      }

      // Validate children shapes
      for (Shape shape : getChildren()) {
         Result result = shape.validForSave();
         if (result.isFalse()) return result;
      }
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return "Work Flow Definition: " + workFlowDefinition.getName();
   }

   /**
    * Add a shape to this diagram.
    * 
    * @param s a non-null shape instance
    * @return true, if the shape was added, false otherwise
    */
   public boolean addChild(Shape s) {
      if (s != null && shapes.add(s)) {
         firePropertyChange(CHILD_ADDED_PROP, null, s);
         return true;
      }
      return false;
   }

   public boolean hasChild(Shape s) {
      for (Object obj : shapes) {
         Shape shape = (Shape) obj;
         if (shape.equals(s)) return true;
      }
      return false;
   }

   /** Return a List of Shapes in this diagram. The returned List should not be modified. */
   public List<Shape> getChildren() {
      return shapes;
   }

   /**
    * Remove a shape from this diagram.
    * 
    * @param s a non-null shape instance;
    * @return true, if the shape was removed, false otherwise
    */
   public boolean removeChild(Shape s) {
      if (s != null && shapes.remove(s)) {
         firePropertyChange(CHILD_REMOVED_PROP, null, s);
         return true;
      }
      return false;
   }

   /**
    * @return the workFlowDefinition
    */
   public WorkFlowDefinition getWorkFlowDefinition() {
      return workFlowDefinition;
   }

}