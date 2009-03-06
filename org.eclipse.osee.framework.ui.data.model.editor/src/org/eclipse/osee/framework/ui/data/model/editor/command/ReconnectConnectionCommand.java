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
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;

/**
 * @author Roberto E. Escobar
 */
public class ReconnectConnectionCommand extends Command {

   private ConnectionModel<ArtifactDataType> connectionModel;
   //   private NodeModel newNode, oldNode;
   //   private int oldIndex, viewIndex;

   private ArtifactDataType oldSource;
   private ArtifactDataType oldTarget;
   private ArtifactDataType newSource;
   private ArtifactDataType newTarget;

   public ReconnectConnectionCommand(ConnectionModel<ArtifactDataType> connectionModel) {
      super("Reconnect connection");
      this.connectionModel = connectionModel;
      this.oldSource = connectionModel.getSource();
      this.oldTarget = connectionModel.getTarget();
   }

   public void setNewSource(ArtifactDataType source) {
      if (source == null) {
         throw new IllegalArgumentException();
      }
      setLabel("move connection startpoint");
      newSource = source;
      newTarget = null;
   }

   public void setNewTarget(ArtifactDataType target) {
      if (target == null) {
         throw new IllegalArgumentException();
      }
      setLabel("move connection endpoint");
      newSource = null;
      newTarget = target;
   }

   public boolean canExecute() {
      if (newSource != null) {
         return checkSourceReconnection();
      } else if (newTarget != null) {
         return checkTargetReconnection();
      }
      return false;
   }

   private boolean checkSourceReconnection() {
      if (newSource.equals(oldTarget)) {
         return false;
      }
      //      if (newSource != null) {
      for (ConnectionModel<ArtifactDataType> connection : newSource.getTargetConnections()) {
         // return false if a newSource -> oldTarget connection exists already
         // and it is a different instance than the connection-field
         if (connection.getTarget().equals(oldTarget) && !this.connectionModel.equals(connection)) {
            return false;
         }
      }
      //      } else {
      //         return false;
      //      }
      return true;
   }

   private boolean checkTargetReconnection() {
      if (newTarget.equals(oldSource)) {
         return false;
      }
      // return false, if the connection exists already
      //      if (newTarget != null) {
      for (ConnectionModel<ArtifactDataType> connection : newTarget.getSourceConnections()) {
         // return false if a oldSource -> newTarget connection exists already
         // and it is a different instance that the connection-field
         if (connection.getSource().equals(oldSource) && !this.connectionModel.equals(connection)) {
            return false;
         }
      }
      //      } else {
      //         return false;
      //      }
      return true;
   }

   public void execute() {
      if (newSource != null) {
         this.connectionModel.reconnect(newSource, oldTarget);
      } else if (newTarget != null) {
         this.connectionModel.reconnect(oldSource, newTarget);
      } else {
         throw new IllegalStateException("Should not happen");
      }
   }

   public void undo() {
      this.connectionModel.reconnect(oldSource, oldTarget);
   }
   //   public void execute() {
   //      superClass = (ArtifactDataType) connectionModel.getTarget();
   //      oldClass = (ArtifactDataType) connectionModel.getSource();
   //
   //      newClass = (ArtifactDataType) newNode;
   //
   //      if (isSource) {
   //         ancestor = newClass.getAncestorType();
   //         newClass.setParent(superClass);
   //      } else {
   //         ancestor = oldClass.getAncestorType();
   //         oldClass.setParent(newClass);
   //      }
   //
   //      if (isSource) {
   //         oldNode = connectionModel.getSource();
   //         viewIndex = oldNode.getOutgoingConnections().indexOf(connectionModel);
   //         connectionModel.setSource((ArtifactDataType) newNode);
   //      } else {
   //         oldNode = connectionModel.getTarget();
   //         viewIndex = oldNode.getIncomingConnections().indexOf(connectionModel);
   //         connectionModel.setTarget((ArtifactDataType) newNode);
   //      }
   //
   //      // Reference
   //      //      if (link instanceof ReferenceView) {
   //      //         ref = ((ReferenceView) link).getEReference();
   //      //         oldClass = (ArtifactDataType) ((NamedElementView) oldNode).getDataTypeElement();
   //      //         newClass = (ArtifactDataType) ((NamedElementView) newNode).getDataTypeElement();
   //      //         if (isSource) {
   //      //            oldIndex = oldClass.getEStructuralFeatures().indexOf(ref);
   //      //            oldClass.getEStructuralFeatures().remove(oldIndex);
   //      //            newClass.getEStructuralFeatures().add(ref);
   //      //         } else {
   //      //            ref.setEType(newClass);
   //      //         }
   //      //      }
   //   }

   //   public void undo() {
   //      //      if (link instanceof ReferenceView) {
   //      //         if (isSource) {
   //      //            newClass.getEStructuralFeatures().remove(ref);
   //      //            oldClass.getEStructuralFeatures().add(oldIndex, ref);
   //      //         } else
   //      //            ref.setEType(oldClass);
   //      //      }
   //      if (isSource) {
   //         newNode.getOutgoingConnections().remove(connectionModel);
   //         oldNode.getOutgoingConnections().add(viewIndex, connectionModel);
   //      } else {
   //         newNode.getIncomingConnections().remove(connectionModel);
   //         oldNode.getIncomingConnections().add(viewIndex, connectionModel);
   //      }
   //      oldNode = null;
   //
   //      if (isSource) {
   //         newClass.setParent(ancestor);
   //      } else {
   //         oldClass.setParent(ancestor);
   //      }
   //   }
}
