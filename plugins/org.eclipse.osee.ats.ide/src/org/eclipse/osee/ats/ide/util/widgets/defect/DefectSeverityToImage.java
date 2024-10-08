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

import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DefectSeverityToImage {

   public static org.eclipse.swt.graphics.Image getImage(Severity sev) {
      if (sev == Severity.Major) {
         return ImageManager.getImage(FrameworkImage.SEVERITY_MAJOR);
      } else if (sev == Severity.Minor) {
         return ImageManager.getImage(FrameworkImage.SEVERITY_MINOR);
      } else if (sev == Severity.Issue) {
         return ImageManager.getImage(FrameworkImage.SEVERITY_ISSUE);
      } else if (sev == Severity.None) {
         return ImageManager.getImage(AtsImage.CENTER);
      }
      return null;
   }

}
