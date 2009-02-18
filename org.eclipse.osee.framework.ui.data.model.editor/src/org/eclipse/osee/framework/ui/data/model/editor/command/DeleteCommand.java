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
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;

/**
 * @author Roberto E. Escobar
 */
public class DeleteCommand extends Command {

   public static final Object DELETE_FROM_ODM = new Object();
   private boolean isHardDelete;
   private Command commandDelegate;

   public DeleteCommand(boolean isHardDelete) {
      super("Delete");
      this.isHardDelete = isHardDelete;
   }

   public boolean canExecute() {
      return commandDelegate != null && commandDelegate.canExecute();
   }

   public void execute() {
      commandDelegate.execute();
   }

   public Command setPartToBeDeleted(Object model) {
      if (model instanceof AttributeDataType) {
      } else if (model instanceof RelationDataType) {
      } else if (model instanceof ArtifactDataType) {

      } else if (model instanceof ConnectionModel) {

      } else {
         commandDelegate = null;
      }
      return this;
   }

   public void undo() {
      commandDelegate.undo();
   }

   //   private static class DeleteAttributeCommand extends Command {
   //      private AttributeDataType attribute;
   //      private ArtifactDataType container;
   //
   //      public DeleteAttributeCommand(Object model) {
   //         attribute = (AttributeDataType) model;
   //         //         parent = attribute.getEContainingClass();
   //      }
   //
   //      public void execute() {
   //         if (container != null) {
   //            container.remove(attribute);
   //         }
   //      }
   //
   //      public void undo() {
   //         if (container != null) {
   //            container.add(attribute);
   //         }
   //      }
   //   }
   //
   //   private static class DeleteRelationCommand extends Command {
   //      private RelationDataType operation;
   //      private ArtifactDataType container;
   //
   //      public DeleteRelationCommand(Object model) {
   //         operation = (RelationDataType) model;
   //         //         container = operation.getEContainingClass();
   //      }
   //
   //      public void execute() {
   //         if (container != null) {
   //            container.remove(operation);
   //         }
   //      }
   //
   //      public void undo() {
   //         if (container != null) {
   //            container.add(operation);
   //         }
   //      }
   //   }
}
