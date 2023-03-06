/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.markedit;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.markedit.edit.OmeEditTab;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class HorizontalAction extends Action {

   private final OmeEditTab omeEditTab;

   public HorizontalAction(OmeEditTab omeEditTab) {
      super("View Horizontal", SWT.PUSH);
      this.omeEditTab = omeEditTab;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.HORIZONTAL);
   }

   @Override
   public void run() {
      omeEditTab.toggleHorizontal();
   }

}
