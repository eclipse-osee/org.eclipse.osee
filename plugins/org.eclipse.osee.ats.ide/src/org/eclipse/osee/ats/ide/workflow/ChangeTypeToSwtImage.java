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

package org.eclipse.osee.ats.ide.workflow;

import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ChangeTypeToSwtImage {

   public static Image getImage(ChangeType type) {
      if (type == ChangeType.Problem) {
         return ImageManager.getImage(FrameworkImage.PROBLEM);
      } else if (type == ChangeType.Improvement) {
         return ImageManager.getImage(FrameworkImage.GREEN_PLUS);
      } else if (type == ChangeType.Support) {
         return ImageManager.getImage(FrameworkImage.SUPPORT);
      } else if (type == ChangeType.Refinement) {
         return ImageManager.getImage(FrameworkImage.REFINEMENT);
      }
      return null;
   }

}
