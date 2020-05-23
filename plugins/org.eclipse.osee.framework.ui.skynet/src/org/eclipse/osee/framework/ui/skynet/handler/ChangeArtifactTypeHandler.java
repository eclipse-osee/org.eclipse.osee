/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.framework.ui.skynet.handler;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public class ChangeArtifactTypeHandler implements IStatusHandler {
   @Override
   public Object handleStatus(IStatus status, Object source) {
      boolean toReturn = false;

      if (RenderingUtil.arePopupsAllowed()) {
         final MutableBoolean result = new MutableBoolean(false);
         final String message = (String) source;
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
               if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Confirm Artifact Type Change ", message)) {
                  result.setValue(true);
               }
            }
         };
         Displays.pendInDisplayThread(runnable);
         toReturn = result.booleanValue();
      } else {
         toReturn = true;
      }
      return toReturn;
   }
}