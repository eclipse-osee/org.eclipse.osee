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
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class RevertLabelProvider extends LabelProvider implements IStyledLabelProvider {
   private static final String HIGHLIGHT_WRITE_BG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.writeaccess_highlight"; //$NON-NLS-1$
   private static final Styler HIGHLIGHT_STYLE =
         StyledString.createColorRegistryStyler(null, HIGHLIGHT_WRITE_BG_COLOR_NAME);
   private static final String DASH = " - ";
   private final Map<Image, Image> disabledMap;

   public RevertLabelProvider() {
      super();

      this.disabledMap = new HashMap<Image, Image>();
   }

   @Override
   public Image getImage(Object element) {
      Image imageToReturn = null;

      if (element instanceof TransferObject) {
         TransferObject transferObject = (TransferObject) element;
         Image artImage = ImageManager.getImage(transferObject.getArtifact());

         if (transferObject.getStatus().equals(TransferStatus.ERROR)) {
            imageToReturn = disabledMap.get(artImage);

            if (imageToReturn == null) {
               imageToReturn = new Image(artImage.getDevice(), artImage, SWT.IMAGE_DISABLE);
               disabledMap.put(artImage, imageToReturn);
            }
         } else {
            imageToReturn = artImage;
         }
      }
      return imageToReturn;
   }

   @Override
   public StyledString getStyledText(Object element) {
      StyledString styledString = new StyledString();

      if (element instanceof TransferObject) {
         TransferObject transferObject = (TransferObject) element;
         TransferStatus status = transferObject.getStatus();

         if (status == TransferStatus.ERROR) {
            styledString.append(transferObject.getArtifact().getName(), StyledString.DECORATIONS_STYLER);
            styledString.append(DASH);
            styledString.append(status.getMessage(), HIGHLIGHT_STYLE);
         } else {
            styledString.append(transferObject.getArtifact().getName());
            styledString.append(DASH);
            styledString.append(status.getMessage(), StyledString.COUNTER_STYLER);
         }
      }
      return styledString;
   }

}
