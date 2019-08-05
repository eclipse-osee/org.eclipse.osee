/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
