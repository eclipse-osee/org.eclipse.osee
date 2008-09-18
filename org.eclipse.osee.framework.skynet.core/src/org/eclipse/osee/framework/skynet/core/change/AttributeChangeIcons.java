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

import static org.eclipse.osee.framework.skynet.core.change.ChangeType.CONFLICTING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.INCOMING;
import static org.eclipse.osee.framework.skynet.core.change.ChangeType.OUTGOING;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.ARTIFACT_DELETED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.CHANGE;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.DELETED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.MERGED;
import static org.eclipse.osee.framework.skynet.core.change.ModificationType.NEW;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.plugin.util.OverlayImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class AttributeChangeIcons {
   private static final SkynetActivator skynetActivator = SkynetActivator.getInstance();
   private static final String BASE_IMAGE_STRING = "molecule";
   private static boolean imagesInitialized;

   public static Image getImage(ChangeType changeType, ModificationType modType) {
      checkImageRegistry();
      return skynetActivator.getImage(BASE_IMAGE_STRING + changeType + modType);
   }

   private synchronized static void checkImageRegistry() {
      if (!imagesInitialized) {
         imagesInitialized = true;

         ImageDescriptor outNew = SkynetActivator.getInstance().getImageDescriptor("out_new.gif");
         ImageDescriptor outChange = SkynetActivator.getInstance().getImageDescriptor("out_change.gif");
         ImageDescriptor outDeleted = SkynetActivator.getInstance().getImageDescriptor("out_delete.gif");
         ImageDescriptor incNew = SkynetActivator.getInstance().getImageDescriptor("inc_new.gif");
         ImageDescriptor incChange = SkynetActivator.getInstance().getImageDescriptor("inc_change.gif");
         ImageDescriptor incDeleted = SkynetActivator.getInstance().getImageDescriptor("inc_delete.gif");
         ImageDescriptor conChange = SkynetActivator.getInstance().getImageDescriptor("con_change.gif");
         ImageDescriptor conDeleted = SkynetActivator.getInstance().getImageDescriptor("con_delete.gif");
         ImageDescriptor conNew = SkynetActivator.getInstance().getImageDescriptor("con_new.gif");
         ImageDescriptor merge = SkynetActivator.getInstance().getImageDescriptor("branch_merge.gif");

         Image baseImage = skynetActivator.getImage(BASE_IMAGE_STRING + ".gif");

         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + DELETED, new OverlayImage(baseImage,
               outDeleted));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + ARTIFACT_DELETED, new OverlayImage(baseImage,
                 outDeleted));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + CHANGE, new OverlayImage(baseImage,
               outChange));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + MERGED, new OverlayImage(baseImage, merge));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + OUTGOING + NEW, new OverlayImage(baseImage, outNew));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + DELETED, new OverlayImage(baseImage,
               incDeleted));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + CHANGE, new OverlayImage(baseImage,
               incChange));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + INCOMING + NEW, new OverlayImage(baseImage, incNew));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + DELETED, new OverlayImage(baseImage,
               conDeleted));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + CHANGE, new OverlayImage(baseImage,
               conChange));
         skynetActivator.addImageToRegistry(BASE_IMAGE_STRING + CONFLICTING + NEW, new OverlayImage(baseImage, conNew));
      }
   }
}