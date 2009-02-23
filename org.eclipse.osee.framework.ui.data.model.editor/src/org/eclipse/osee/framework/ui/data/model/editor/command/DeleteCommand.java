/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.data.model.editor.command;

import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;

/**
 * @author Roberto E. Escobar
 */
public class DeleteCommand extends Command {

   public static final Object DELETE_FROM_ODM_DIAGRAM = new Object();
   private Command commandDelegate;

   public DeleteCommand() {
      super("Delete");
   }

   public boolean canExecute() {
      return commandDelegate != null && commandDelegate.canExecute();
   }

   public void execute() {
      redo();
   }

   public Command setPartToBeDeleted(Object toDelete, Object parent, boolean isArtifactDelete) {
      if (toDelete instanceof AttributeDataType) {
         commandDelegate = new DeleteAttributeCommand(toDelete, parent);
      } else if (toDelete instanceof RelationDataType) {
         commandDelegate = new DeleteRelationCommand(toDelete, parent);
      } else if (isArtifactDelete && toDelete instanceof ArtifactDataType) {
         commandDelegate = new DeleteArtifactCommand(toDelete, parent);
      } else if (toDelete instanceof ConnectionModel) {
         System.out.println("Delete connection");
      } else {
         commandDelegate = null;
      }
      return this;
   }

   public void redo() {
      commandDelegate.execute();
   }

   public void undo() {
      commandDelegate.undo();
   }

   private static class DeleteArtifactCommand extends Command {
      private ArtifactDataType artifact;
      private ODMDiagram container;

      public DeleteArtifactCommand(Object model, Object parent) {
         artifact = (ArtifactDataType) model;
         container = (ODMDiagram) parent;
      }

      public void execute() {
         redo();
      }

      public void redo() {
         if (container != null) {
            container.remove(artifact);
         }
      }

      public void undo() {
         if (container != null) {
            container.add(artifact);
         }
      }
   }

   private static class DeleteAttributeCommand extends Command {
      private AttributeDataType attribute;
      private ArtifactDataType container;

      public DeleteAttributeCommand(Object model, Object parent) {
         attribute = (AttributeDataType) model;
         container = (ArtifactDataType) parent;
      }

      public void execute() {
         redo();
      }

      public void redo() {
         if (container != null) {
            container.remove(attribute);
         }
      }

      public void undo() {
         if (container != null) {
            container.add(attribute);
         }
      }
   }

   private static class DeleteRelationCommand extends Command {
      private RelationDataType relation;
      private ArtifactDataType container;

      public DeleteRelationCommand(Object model, Object parent) {
         relation = (RelationDataType) model;
         container = (ArtifactDataType) parent;
      }

      public void execute() {
         redo();
      }

      public void redo() {
         if (container != null) {
            container.remove(relation);
         }
      }

      public void undo() {
         if (container != null) {
            container.add(relation);
         }
      }
   }
}
