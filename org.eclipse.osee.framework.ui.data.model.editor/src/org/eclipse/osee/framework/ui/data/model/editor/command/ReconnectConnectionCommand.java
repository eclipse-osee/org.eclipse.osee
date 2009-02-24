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
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;

/**
 * @author Roberto E. Escobar
 */
public class ReconnectConnectionCommand extends Command {

   private boolean isSource;
   private ConnectionModel connectionModel;
   private NodeModel newNode, oldNode;
   private int oldIndex, viewIndex;
   //   private EReference ref;
   private ArtifactDataType oldClass;
   private ArtifactDataType newClass;
   private ArtifactDataType superClass;
   private ArtifactDataType ancestor;

   public ReconnectConnectionCommand(ConnectionModel connectionModel, NodeModel newNode, boolean isSource) {
      super("Reconnect connection");
      this.connectionModel = connectionModel;
      this.newNode = newNode;
      this.isSource = isSource;
   }

   public boolean canExecute() {
      return connectionModel != null && newNode != null;
   }

   public void execute() {
      superClass = (ArtifactDataType) connectionModel.getTarget();
      oldClass = (ArtifactDataType) connectionModel.getSource();

      newClass = (ArtifactDataType) newNode;

      if (isSource) {
         ancestor = newClass.getAncestorType();
         newClass.setParent(superClass);
      } else {
         ancestor = oldClass.getAncestorType();
         oldClass.setParent(newClass);
      }

      if (isSource) {
         oldNode = connectionModel.getSource();
         viewIndex = oldNode.getOutgoingConnections().indexOf(connectionModel);
         connectionModel.setSource((ArtifactDataType) newNode);
      } else {
         oldNode = connectionModel.getTarget();
         viewIndex = oldNode.getIncomingConnections().indexOf(connectionModel);
         connectionModel.setTarget((ArtifactDataType) newNode);
      }

      // Reference
      //      if (link instanceof ReferenceView) {
      //         ref = ((ReferenceView) link).getEReference();
      //         oldClass = (ArtifactDataType) ((NamedElementView) oldNode).getDataTypeElement();
      //         newClass = (ArtifactDataType) ((NamedElementView) newNode).getDataTypeElement();
      //         if (isSource) {
      //            oldIndex = oldClass.getEStructuralFeatures().indexOf(ref);
      //            oldClass.getEStructuralFeatures().remove(oldIndex);
      //            newClass.getEStructuralFeatures().add(ref);
      //         } else {
      //            ref.setEType(newClass);
      //         }
      //      }
   }

   public void undo() {
      //      if (link instanceof ReferenceView) {
      //         if (isSource) {
      //            newClass.getEStructuralFeatures().remove(ref);
      //            oldClass.getEStructuralFeatures().add(oldIndex, ref);
      //         } else
      //            ref.setEType(oldClass);
      //      }
      if (isSource) {
         newNode.getOutgoingConnections().remove(connectionModel);
         oldNode.getOutgoingConnections().add(viewIndex, connectionModel);
      } else {
         newNode.getIncomingConnections().remove(connectionModel);
         oldNode.getIncomingConnections().add(viewIndex, connectionModel);
      }
      oldNode = null;

      if (isSource) {
         newClass.setParent(ancestor);
      } else {
         oldClass.setParent(ancestor);
      }
   }
}
