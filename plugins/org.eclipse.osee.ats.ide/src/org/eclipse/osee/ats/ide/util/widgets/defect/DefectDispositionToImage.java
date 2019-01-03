/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets.defect;

import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.workflow.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class DefectDispositionToImage {
   public static Image getImage(Disposition sev) {
      if (sev == Disposition.Accept) {
         return ImageManager.getImage(FrameworkImage.ACCEPT);
      } else if (sev == Disposition.Reject) {
         return ImageManager.getImage(FrameworkImage.REJECT);
      } else if (sev == Disposition.Duplicate) {
         return ImageManager.getImage(FrameworkImage.DUPLICATE);
      } else if (sev == Disposition.None) {
         return ImageManager.getImage(AtsImage.CENTER);
      }
      return null;
   }

}
