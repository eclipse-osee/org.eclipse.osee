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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * A container for multiple shapes. This is the "root" of the model data structure.
 * 
 * @author Donald G. Dunne
 */
public class WorkDefinitionDiagram extends ModelElement {

   /** Property ID to use when a child is added to this diagram. */
   public static final String CHILD_ADDED_PROP = "WorkflowDiagram.ChildAdded";
   /** Property ID to use when a child is removed from this diagram. */
   public static final String CHILD_REMOVED_PROP = "WorkflowDiagram.ChildRemoved";
   private final List<Shape> shapes = new ArrayList<>();
   private final List<Shape> deletedShapes = new ArrayList<>();
   private final IAtsWorkDefinition workDef;

   public WorkDefinitionDiagram(IAtsWorkDefinition workDef) {
      super();
      this.workDef = workDef;
   }

   @Override
   public Result doSave(SkynetTransaction transaction) {
      return Result.TrueResult;
   }

   @Override
   public Result validForSave() {
      return Result.TrueResult;
   }

   @Override
   public String toString() {
      return "Work Definition: " + workDef.getName();
   }

   /**
    * Add a shape to this diagram.
    * 
    * @param s a non-null shape instance
    * @return true, if the shape was added, false otherwise
    */
   public boolean addChild(Shape s) {
      if (s != null && shapes.add(s)) {
         deletedShapes.remove(s);
         s.setWorkflowDiagram(this);
         firePropertyChange(CHILD_ADDED_PROP, null, s);
         return true;
      }
      return false;
   }

   public boolean hasChild(Shape s) {
      for (Object obj : shapes) {
         Shape shape = (Shape) obj;
         if (shape.equals(s)) {
            return true;
         }
      }
      return false;
   }

   /** Return a List of Shapes in this diagram. The returned List should not be modified. */
   public List<Shape> getChildren() {
      return shapes;
   }

   public Set<Connection> getConnections() {
      Set<Connection> connections = new HashSet<>();
      for (Shape shape : getChildren()) {
         connections.addAll(shape.getSourceConnections());
         connections.addAll(shape.getTargetConnections());
      }
      return connections;
   }

   /**
    * Remove a shape from this diagram.
    * 
    * @param s a non-null shape instance;
    * @return true, if the shape was removed, false otherwise
    */
   public boolean removeChild(Shape s) {
      if (s != null && shapes.remove(s)) {
         deletedShapes.add(s);
         firePropertyChange(CHILD_REMOVED_PROP, null, s);
         return true;
      }
      return false;
   }

   /**
    * @return the workFlowDefinition
    */
   public IAtsWorkDefinition getWorkDefinition() {
      return workDef;
   }

}