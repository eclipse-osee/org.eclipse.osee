/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactChangeHandler implements IStatusHandler {
   @Override
   public Object handleStatus(IStatus status, Object source) throws CoreException {
      final MutableInteger result = new MutableInteger(0);
      final String message = (String) source;

      Runnable runnable = new Runnable() {
         @Override
         public void run() {
            if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Confirm Artifact Type Change ", message)) {
               result.setValue(1);
            }
         }

      };

      Displays.ensureInDisplayThread(runnable, true);
      return result.getValue() == 1;
   }
}