/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.task;

import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class CreateManualTaskPlaceholder extends AbstractAtsAction {

   public CreateManualTaskPlaceholder(String text) {
      super();
      setText(text);
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.TASK));
   }

   @Override
   public void runWithException() {
      Program.launch("https://wiki.eclipse.org/OSEE/ATS/Users_Guide/Usage#OSEE_Tasks");

   }
}
