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

import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class VersionArtifactLabelProvider extends LabelProvider {

   @Override
   public String getText(Object element) {
      if (element instanceof VersionArtifact) {
         VersionArtifact verArt = (VersionArtifact) element;
         String str = verArt.getName();
         try {
            if (verArt.getEstimatedReleaseDate() != null) {
               str += " - Estimated Release: " + DateUtil.getMMDDYY(verArt.getEstimatedReleaseDate());
            }
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
         return str;
      }
      return "Unknown element type";
   }

   @Override
   public Image getImage(Object element) {
      if (element instanceof VersionArtifact) {
         return ArtifactImageManager.getImage((VersionArtifact) element);
      }
      return null;
   }
}
