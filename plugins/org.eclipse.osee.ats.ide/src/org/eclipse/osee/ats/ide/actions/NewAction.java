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

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.actions.newaction.CreateNewActionBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class NewAction extends AbstractAtsAction {

   public NewAction() {
      super("Create New Action");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.NEW_ACTION));
      setToolTipText("Create New Action");
   }

   @Override
   public void runWithException() {
      CreateNewActionBlam blam = new CreateNewActionBlam();
      BlamEditor.edit(blam);
   }

}