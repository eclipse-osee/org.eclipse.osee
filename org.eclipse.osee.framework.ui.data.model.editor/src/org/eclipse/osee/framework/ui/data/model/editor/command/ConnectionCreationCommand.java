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

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.NodeModel;

/**
 * @author Roberto E. Escobar
 */
public class ConnectionCreationCommand extends Command {

   private ConnectionModel link;
   private NodeModel source;
   private NodeModel target;

   public ConnectionCreationCommand(ConnectionModel newObj, NodeModel src) {
      super("Connection Creation");
      this.link = newObj;
      this.source = src;
   }

   public boolean canExecute() {
      boolean result = source != null && target != null && link != null;
      //      if (link instanceof InheritanceView) {
      //         result =
      //               result && !((EClass) ((NamedElementView) source).getDataTypeElement()).getESuperTypes().contains(
      //                     ((NamedElementView) target).getDataTypeElement());
      //      }
      return result;
   }

   public void execute() {
      //      if (link instanceof ReferenceView) {
      //         ReferenceView refView = (ReferenceView) link;
      //         EClass srcClass = (EClass) ((NamedElementView) source).getDataTypeElement();
      //         EClass targetClass = (EClass) ((NamedElementView) target).getDataTypeElement();
      //         if (refView.isOppositeShown() && refView.getEReference().getEOpposite() != null) {
      //            EReference oppRef = refView.getEReference().getEOpposite();
      //            oppRef.setEType(srcClass);
      //            targetClass.getEStructuralFeatures().add(oppRef);
      //         }
      //         refView.getEReference().setEType(targetClass);
      //         srcClass.getEStructuralFeatures().add(refView.getEReference());
      //      } else if (link instanceof InheritanceView) {
      //         ((EClass) ((NamedElementView) source).getDataTypeElement()).getESuperTypes().add(
      //               (EClass) ((NamedElementView) target).getDataTypeElement());
      //      }
      if (source == target) {
         link.getBendpoints().add(new AbsoluteBendpoint(source.getLocation().getTranslated(-10, 10)));
         link.getBendpoints().add(new AbsoluteBendpoint(source.getLocation().getTranslated(-10, -10)));
         link.getBendpoints().add(new AbsoluteBendpoint(source.getLocation().getTranslated(10, -10)));
      }
      link.setSource(source);
      link.setTarget(target);
   }

   public void setTarget(NodeModel target) {
      this.target = target;
   }

   public void undo() {
      link.setSource(null);
      link.setTarget(null);
      if (source == target) {
         link.getBendpoints().clear();
      }
      //      if (link instanceof ReferenceView) {
      //         ReferenceView refView = (ReferenceView) link;
      //         EClass srcClass = (EClass) ((NamedElementView) source).getDataTypeElement();
      //         EClass targetClass = (EClass) ((NamedElementView) target).getDataTypeElement();
      //         srcClass.getEStructuralFeatures().remove(refView.getEReference());
      //         refView.getEReference().setEType(null);
      //         if (refView.isOppositeShown() && refView.getEReference().getEOpposite() != null) {
      //            EReference oppRef = refView.getEReference().getEOpposite();
      //            targetClass.getEStructuralFeatures().remove(oppRef);
      //            oppRef.setEType(null);
      //         }
      //      } else if (link instanceof InheritanceView) {
      //         ((EClass) ((NamedElementView) source).getDataTypeElement()).getESuperTypes().remove(
      //               ((NamedElementView) target).getDataTypeElement());
      //      }
   }

}
