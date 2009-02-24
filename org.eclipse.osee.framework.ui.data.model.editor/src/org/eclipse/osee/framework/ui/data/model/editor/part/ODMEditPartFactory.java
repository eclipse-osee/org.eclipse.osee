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
package org.eclipse.osee.framework.ui.data.model.editor.part;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.InheritanceLinkModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.ODMDiagram;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.RelationLinkModel;
import org.eclipse.osee.framework.ui.data.model.editor.model.helper.ContainerModel;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditPartFactory implements EditPartFactory {

   public ODMEditPartFactory() {
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
    */
   public EditPart createEditPart(EditPart context, Object model) {
      EditPart editPart = null;
      if (model instanceof ODMDiagram) {
         editPart = new DiagramEditPart(model);
      } else if (model instanceof ArtifactDataType) {
         editPart = new ArtifactEditPart(model);
      } else if (model instanceof RelationDataType) {
         editPart = new RelationEditPart(model);
      } else if (model instanceof AttributeDataType) {
         editPart = new AttributeEditPart(model);
      } else if (model instanceof ContainerModel || model instanceof List) {
         editPart = new ContainerEditPart(model);
      } else if (model instanceof InheritanceLinkModel) {
         editPart = new InheritanceEditPart(model);
      } else if (model instanceof RelationLinkModel) {
         editPart = new RelationLinkEditPart(model);
      } else if (model instanceof ConnectionModel) {
         editPart = new ConnectionEditPart(model);
      } else if (model instanceof String) {
         editPart = new StringEditPart((String) model);
      }
      if (editPart == null) {
         String message = String.format("Error no EditPart defined for: [%s]", model.getClass().getName());
         editPart = new StringEditPart(message);
         OseeLog.log(ODMEditorActivator.class, Level.SEVERE, message);
      } else {
         editPart.setModel(model);
      }
      return editPart;
   }
}