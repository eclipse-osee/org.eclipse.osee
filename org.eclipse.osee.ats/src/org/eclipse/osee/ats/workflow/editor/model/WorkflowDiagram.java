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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition.TransitionType;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;

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
   private final List<Shape> deletedShapes = new ArrayList<Shape>();
   private final WorkFlowDefinition workFlowDefinition;

   public WorkflowDiagram(WorkFlowDefinition workFlowDefinition) {
      super();
      this.workFlowDefinition = workFlowDefinition;
   }

   @Override
   public Result doSave(SkynetTransaction transaction) throws OseeCoreException {
      Result result = validForSave();
      if (result.isFalse()) return result;
      try {

         List<WorkPageShape> workPageShapes = new ArrayList<WorkPageShape>();
         for (Shape shape : getChildren()) {
            if (shape instanceof WorkPageShape) {
               workPageShapes.add((WorkPageShape) shape);
            }
         }

         // Remove all states that do not exist anymore
         for (Shape shape : deletedShapes) {
            if (WorkPageShape.class.isAssignableFrom(shape.getClass())) {
               WorkPageShape workPageShape = (WorkPageShape) shape;
               if (workPageShape.getArtifact() != null) {
                  workPageShape.getArtifact().delete(transaction);
                  workFlowDefinition.removeWorkItem(((WorkPageShape) shape).getId());
               }
            }
         }

         // Save new states and modifications to states
         for (WorkPageShape workPageShape : workPageShapes) {
            result = workPageShape.doSave(transaction);
            if (result.isFalse()) return result;
         }

         // Set start page
         for (WorkPageShape workPageShape : workPageShapes) {
            if (workPageShape.isStartPage()) {
               workFlowDefinition.setStartPageId(workPageShape.getWorkPageDefinition().getPageName());
               break;
            }
         }

         // Validate transitions
         workFlowDefinition.clearTransitions();
         for (Connection connection : getConnections()) {
            if (TransitionConnection.class.isAssignableFrom(connection.getClass())) {
               TransitionConnection transConn = (TransitionConnection) connection;
               if (transConn instanceof DefaultTransitionConnection) {
                  workFlowDefinition.addPageTransition(
                        ((WorkPageShape) transConn.getSource()).getWorkPageDefinition().getPageName(),
                        ((WorkPageShape) transConn.getTarget()).getWorkPageDefinition().getPageName(),
                        TransitionType.ToPageAsDefault);
               } else if (transConn instanceof ReturnTransitionConnection) {
                  workFlowDefinition.addPageTransition(
                        ((WorkPageShape) transConn.getSource()).getWorkPageDefinition().getPageName(),
                        ((WorkPageShape) transConn.getTarget()).getWorkPageDefinition().getPageName(),
                        TransitionType.ToPageAsReturn);
               } else if (transConn instanceof TransitionConnection) {
                  workFlowDefinition.addPageTransition(
                        ((WorkPageShape) transConn.getSource()).getWorkPageDefinition().getPageName(),
                        ((WorkPageShape) transConn.getTarget()).getWorkPageDefinition().getPageName(),
                        TransitionType.ToPage);
               }
            }
         }
         workFlowDefinition.loadPageData(true);

         Artifact artifact = workFlowDefinition.toArtifact(WriteType.Update);
         AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(artifact, transaction);
         artifact.persistAttributes(transaction);

         WorkItemDefinitionFactory.deCache(workFlowDefinition);

         // Validate saved workflows and all corresponding workItemDefinitions
         // prior to completion of save
         result = AtsWorkDefinitions.validateWorkItemDefinition(workFlowDefinition);
         if (result.isFalse()) return result;
         for (Shape shape : getChildren()) {
            if (WorkPageShape.class.isAssignableFrom(shape.getClass())) {
               WorkPageDefinition workPageDefinition = ((WorkPageShape) shape).getWorkPageDefinition();
               result = AtsWorkDefinitions.validateWorkItemDefinition(workPageDefinition);
               if (result.isFalse()) return result;
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Result.TrueResult;
   }

   @Override
   public Result validForSave() throws OseeCoreException {

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
         if (WorkPageShape.class.isAssignableFrom(shape.getClass())) {
            if (((WorkPageShape) shape).isStartPage()) {
               if (((WorkPageShape) shape).isCancelledState()) return new Result(
                     "Cancelled state can not be start page");
               if (((WorkPageShape) shape).isCompletedState()) return new Result(
                     "Completed state can not be start page");
               num++;
            }
         }
      }
      if (num > 1 || num == 0) return new Result("Must have 1 start page; Currently " + num);

      // Validate state names
      List<String> stateNames = new ArrayList<String>();
      for (Shape shape : getChildren()) {
         if (WorkPageShape.class.isAssignableFrom(shape.getClass())) {
            String name =
                  (String) ((WorkPageShape) shape).getPropertyValue(WorkItemAttributes.WORK_PAGE_NAME.getAttributeTypeName());
            if (stateNames.contains(name)) {
               return new Result(
                     "Workflow can not have more than one state of same name.  Multiples found of name " + name);
            }
            stateNames.add(name);
         }
      }

      // Validate transitions (each state must have a transition to or from
      for (Shape shape : getChildren()) {
         if (WorkPageShape.class.isAssignableFrom(shape.getClass())) {
            if (shape.getSourceConnections().size() == 0 && shape.getTargetConnections().size() == 0) {
               return new Result("States must have at least one transition to or from.  None found for " + shape);
            }
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
         if (shape.equals(s)) return true;
      }
      return false;
   }

   /** Return a List of Shapes in this diagram. The returned List should not be modified. */
   public List<Shape> getChildren() {
      return shapes;
   }

   public Set<Connection> getConnections() {
      Set<Connection> connections = new HashSet<Connection>();
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
   public WorkFlowDefinition getWorkFlowDefinition() {
      return workFlowDefinition;
   }

}