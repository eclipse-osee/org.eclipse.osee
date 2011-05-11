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

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class VersionArtifactLabelProvider extends LabelProvider {

   @Override
   public String getText(Object element) {
      if (element instanceof Artifact) {
         Artifact verArt = (Artifact) element;
         String str = verArt.getName();
         try {
            if (verArt.getSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate, null) != null) {
               str +=
                  " - Estimated Release: " + DateUtil.getMMDDYY((Date) verArt.getSoleAttributeValue(
                     AtsAttributeTypes.EstimatedReleaseDate, null));
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
      if (Artifacts.isOfType(element, AtsArtifactTypes.Version)) {
         return ArtifactImageManager.getImage((Artifact) element);
      }
      return null;
   }
}
