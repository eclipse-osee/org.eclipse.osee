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
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;

/**
 * A container for multiple shapes. This is the "root" of the model data structure.
 * 
 * @author Donald G Dunne
 */
public class WorkflowDiagram extends ModelElement {

   /** Property ID to use when a child is added to this diagram. */
   public static final String CHILD_ADDED_PROP = "ShapesDiagram.ChildAdded";
   /** Property ID to use when a child is removed from this diagram. */
   public static final String CHILD_REMOVED_PROP = "ShapesDiagram.ChildRemoved";
   private final List shapes = new ArrayList();
   private final WorkFlowDefinition workFlowDefinition;

   public WorkflowDiagram(WorkFlowDefinition workFlowDefinition) {
      super();
      this.workFlowDefinition = workFlowDefinition;
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
   public List getChildren() {
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