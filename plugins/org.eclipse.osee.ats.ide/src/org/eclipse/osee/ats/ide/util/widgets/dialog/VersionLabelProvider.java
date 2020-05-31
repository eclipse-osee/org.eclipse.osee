/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class VersionLabelProvider extends LabelProvider {

   private static Image releasedVersion;
   private static Image lockedVersion;
   public static final String CLEAR = "--clear--";

   @Override
   public String getText(Object element) {
      if (element instanceof IAtsVersion) {
         IAtsVersion version = (IAtsVersion) element;
         String str = version.getName();
         if (version.isNextVersion()) {
            str += "   (Next Release)";
         } else if (version.isReleased()) {
            str += "   (Released)";
         }
         return str;
      } else if (element instanceof String && element.equals(CLEAR)) {
         return (String) element;
      }
      return "Unknown element type";
   }

   @Override
   public Image getImage(Object element) {
      if (element instanceof IAtsVersion) {
         IAtsVersion version = (IAtsVersion) element;
         if (version.isNextVersion()) {
            return ImageManager.getImage(FrameworkImage.VERSION_NEXT);
         }
         if (version.isReleased()) {
            if (releasedVersion == null) {
               OverlayImage overlay = new OverlayImage(ImageManager.getImage(FrameworkImage.VERSION),
                  ImageManager.getImageDescriptor(AtsImage.RELEASED), Location.BOT_RIGHT);
               releasedVersion = overlay.createImage();
            }
            return releasedVersion;
         }
         if (version.isLocked()) {
            if (lockedVersion == null) {
               OverlayImage overlay = new OverlayImage(ImageManager.getImage(FrameworkImage.VERSION),
                  ImageManager.getImageDescriptor(AtsImage.VERSION_LOCKED), Location.BOT_RIGHT);
               lockedVersion = overlay.createImage();
            }
            return lockedVersion;
         }
         return ImageManager.getImage(FrameworkImage.VERSION);
      }
      return null;
   }
}
