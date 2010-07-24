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

import java.util.logging.Level;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.InheritanceLinkModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationLinkModel;

/**
 * @author Roberto E. Escobar
 */
public class DeleteCommand extends Command {

   public static final Object DELETE_FROM_ODM_DIAGRAM = new Object();
   private Command commandDelegate;

   public DeleteCommand() {
      super("Delete");
   }

   @Override
   public boolean canExecute() {
      return commandDelegate != null && commandDelegate.canExecute();
   }

   @Override
   public void execute() {
      redo();
   }

   public Command setPartToBeDeleted(Object toDelete, Object parent, boolean isDeleteFromDiagram) {
      if (toDelete instanceof AttributeDataType) {
         commandDelegate = new DeleteAttributeCommand(toDelete, parent);
      } else if (toDelete instanceof RelationDataType) {
         commandDelegate = new DeleteRelationCommand(toDelete, parent);
      } else if (isDeleteFromDiagram && toDelete instanceof ArtifactDataType) {
         commandDelegate = new DeleteArtifactCommand(toDelete, parent);
      } else if (isDeleteFromDiagram && toDelete instanceof ConnectionModel) {
         commandDelegate = new DeleteConnectionCommand(toDelete, parent);
      } else {
         commandDelegate = null;
      }
      return this;
   }

   @Override
   public void redo() {
      commandDelegate.execute();
   }

   @Override
   public void undo() {
      commandDelegate.undo();
   }

   private static class DeleteArtifactCommand extends Command {
      private final ArtifactDataType artifact;
      private final ODMDiagram container;

      public DeleteArtifactCommand(Object model, Object parent) {
         artifact = (ArtifactDataType) model;
         container = (ODMDiagram) parent;
      }

      @Override
      public void execute() {
         redo();
      }

      @Override
      public void redo() {
         if (container != null) {
            container.remove(artifact);
         }
      }

      @Override
      public void undo() {
         if (container != null) {
            container.add(artifact);
         }
      }
   }

   private static class DeleteAttributeCommand extends Command {
      private final AttributeDataType attribute;
      private final ArtifactDataType container;

      public DeleteAttributeCommand(Object model, Object parent) {
         attribute = (AttributeDataType) model;
         container = (ArtifactDataType) parent;
      }

      @Override
      public void execute() {
         redo();
      }

      @Override
      public void redo() {
         if (container != null) {
            container.remove(attribute);
         }
      }

      @Override
      public void undo() {
         if (container != null) {
            container.add(attribute);
         }
      }
   }

   private static class DeleteRelationCommand extends Command {
      private final RelationDataType relation;
      private final ArtifactDataType container;

      public DeleteRelationCommand(Object model, Object parent) {
         relation = (RelationDataType) model;
         container = (ArtifactDataType) parent;
      }

      @Override
      public void execute() {
         redo();
      }

      @Override
      public void redo() {
         if (container != null) {
            container.remove(relation);
         }
      }

      @Override
      public void undo() {
         if (container != null) {
            container.add(relation);
         }
      }
   }

   private static class DeleteConnectionCommand extends Command {

      private final ConnectionModel link;
      private final NodeModel src, target;
      private int srcIndex, targetIndex, superIndex, parentIndex;
      private ArtifactDataType superClass, subClass;

      public DeleteConnectionCommand(Object link, Object parent) {
         super("Delete Connection");
         this.link = (ConnectionModel) link;
         src = this.link.getSource();
         target = this.link.getTarget();
      }

      @Override
      public boolean canExecute() {
         return link != null && src != null && target != null;
      }

      @Override
      public void execute() {
         redo();
      }

      @Override
      public void redo() {
         boolean removeConnection = true;

         if (link instanceof InheritanceLinkModel) {
            superClass = (ArtifactDataType) src;
            subClass = (ArtifactDataType) target;

            try {
               subClass.setSuperType(null);
            } catch (OseeStateException ex) {
               OseeLog.log(ODMEditorActivator.class, Level.SEVERE,
                  String.format("Unable to remove inheritance link between [%s] - [%s]", superClass, subClass), ex);
               removeConnection = false;
            }
         }

         if (removeConnection) {
            srcIndex = src.getSourceConnections().indexOf(link);
            targetIndex = target.getTargetConnections().indexOf(link);
            if (srcIndex != -1 && targetIndex != -1) {
               src.getSourceConnections().remove(srcIndex);
               target.getTargetConnections().remove(targetIndex);
            }
         }
         //         if (isHardDelete && link instanceof RelationLinkModel) {
         //            reference = ((ReferenceView) link).getEReference();
         //            parent = reference.getEContainingClass();
         //            parentIndex = parent.getEStructuralFeatures().indexOf(reference);
         //            if (parentIndex != -1) parent.getEStructuralFeatures().remove(parentIndex);
         //         }
      }

      @Override
      public void undo() {
         boolean addConnection = true;
         if (link instanceof InheritanceLinkModel) {
            try {
               subClass.setSuperType(superClass);
            } catch (OseeStateException ex) {
               OseeLog.log(ODMEditorActivator.class, Level.SEVERE,
                  String.format("Unable to add inheritance link between [%s] - [%s]", superClass, subClass), ex);
               addConnection = false;
            }
         }

         if (addConnection && srcIndex != -1 && targetIndex != -1) {
            src.getSourceConnections().add(srcIndex, link);
            target.getTargetConnections().add(targetIndex, link);
         }

         if (link instanceof RelationLinkModel) {
            //            if (parentIndex != -1) parent.getEStructuralFeatures().add(parentIndex, reference);
         }
      }

   }
}
