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
package org.eclipse.osee.framework.ui.skynet.artifact;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactNameConflictHandler {

   public String resolve(IArtifact source) throws CoreException {
      final Pair<String, String> beforeAfterNames = new Pair<String, String>("", "");
      if (source instanceof IArtifact) {
         String startingName = source.getName();
         beforeAfterNames.setFirst(startingName);
         beforeAfterNames.setSecond(startingName);
      }

      Display.getDefault().syncExec(new Runnable() {
         public void run() {
            String startingName = beforeAfterNames.getFirst();
            InputDialog dialog =
                  new InputDialog(Display.getCurrent().getActiveShell(), "Name Artifact", "Enter artifact name",
                        startingName, new NonBlankAndNotSameAsStartingValidator(startingName));
            int result = dialog.open();
            if (result == Window.OK) {
               beforeAfterNames.setSecond(dialog.getValue());
            }
         }
      });
      return beforeAfterNames.getSecond();
   }
}
