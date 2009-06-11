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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionDialog extends org.eclipse.ui.dialogs.ListDialog {

   /**
    * 
    */
   public TeamDefinitionDialog(String title, String message) {
      super(Display.getCurrent().getActiveShell());
      this.setTitle(title);
      this.setMessage(message);
      this.setContentProvider(new ArrayContentProvider() {
         @SuppressWarnings("unchecked")
         @Override
         public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Collection) {
               Collection list = (Collection) inputElement;
               return (list.toArray(new TeamDefinitionArtifact[list.size()]));
            }
            return super.getElements(inputElement);
         }
      });
      setLabelProvider(new LabelProvider() {
         @Override
         public String getText(Object element) {
            if (element instanceof TeamDefinitionArtifact) {
               return ((TeamDefinitionArtifact) element).getDescriptiveName();
            }
            return "Unknown element type";
         }

         @Override
         public Image getImage(Object element) {
            if (element instanceof TeamDefinitionArtifact) {
               return ImageManager.getImage((TeamDefinitionArtifact) element);
            }
            return null;
         }

      });
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTableViewer().setSorter(new ArtifactNameSorter());
      return c;
   }

}
