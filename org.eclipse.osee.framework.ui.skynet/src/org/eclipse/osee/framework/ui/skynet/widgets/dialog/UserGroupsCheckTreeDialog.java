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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class UserGroupsCheckTreeDialog extends ArtifactCheckTreeDialog {

   public UserGroupsCheckTreeDialog(Collection<Artifact> artifacts) {
      super(artifacts);
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      super.getTreeViewer().setLabelProvider(new ArtifactLabelProvider());
      return c;
   }

   public class ArtifactLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return ImageManager.getImage((Artifact) arg0);
      }

      public String getText(Object arg0) {
         return ((Artifact) arg0).getName() + " - (" + ((Artifact) arg0).getArtifactTypeName() + ")";
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }

   }

}
