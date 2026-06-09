/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.defect;

import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class DefectDispositionToImage {
   public static Image getImage(Disposition sev) {
      if (sev == Disposition.Accept) {
         return ImageManager.getImage(CoreImage.ACCEPT);
      } else if (sev == Disposition.Reject) {
         return ImageManager.getImage(CoreImage.REJECT);
      } else if (sev == Disposition.Duplicate) {
         return ImageManager.getImage(CoreImage.DUPLICATE);
      } else if (sev == Disposition.None) {
         return ImageManager.getImage(AtsImage.CENTER);
      }
      return null;
   }

}
