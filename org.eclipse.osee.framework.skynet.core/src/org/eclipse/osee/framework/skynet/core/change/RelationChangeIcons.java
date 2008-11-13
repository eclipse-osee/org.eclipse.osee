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

package org.eclipse.osee.framework.skynet.core.change;

import static org.eclipse.osee.framework.core.enums.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.CHANGE;
import static org.eclipse.osee.framework.core.enums.ModificationType.DELETED;
import static org.eclipse.osee.framework.core.enums.ModificationType.NEW;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.INCOMING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class RelationChangeIcons {
   private static final String BASE_IMAGE_STRING = "relate";
   private static final SkynetActivator plugin = SkynetActivator.getInstance();
   private static boolean imagesInitialized = false;

   public static Image getImage(ChangeType changeType, ModificationType modType) {
      checkImageRegistry();
      return plugin.getImage(BASE_IMAGE_STRING + changeType + modType);
   }

   private synchronized static void checkImageRegistry() {
      if (!imagesInitialized) {
         imagesInitialized = true;

         ImageDescriptor outNew = plugin.getImageDescriptor("out_new.gif");
         ImageDescriptor outChange = plugin.getImageDescriptor("out_change.gif");
         ImageDescriptor outDeleted = plugin.getImageDescriptor("out_delete.gif");
         ImageDescriptor incNew = plugin.getImageDescriptor("inc_new.gif");
         ImageDescriptor incChange = plugin.getImageDescriptor("inc_change.gif");
         ImageDescriptor incDeleted = plugin.getImageDescriptor("inc_delete.gif");
         ImageDescriptor conChange = plugin.getImageDescriptor("con_change.gif");
         ImageDescriptor conDeleted = plugin.getImageDescriptor("con_delete.gif");

         Image baseImage = plugin.getImage(BASE_IMAGE_STRING + ".gif");

         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + DELETED, new OverlayImage(baseImage, outDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + ARTIFACT_DELETED, new OverlayImage(baseImage, outDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + CHANGE, new OverlayImage(baseImage, outChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + NEW, new OverlayImage(baseImage, outNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + DELETED, new OverlayImage(baseImage, incDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + CHANGE, new OverlayImage(baseImage, incChange));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + NEW, new OverlayImage(baseImage, incNew));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + DELETED, new OverlayImage(baseImage, conDeleted));
         plugin.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + CHANGE, new OverlayImage(baseImage, conChange));
      }
   }
}