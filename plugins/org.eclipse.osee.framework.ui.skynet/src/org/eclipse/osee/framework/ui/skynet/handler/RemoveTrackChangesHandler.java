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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class RemoveTrackChangesHandler implements IStatusHandler {
   @Override
   public Object handleStatus(IStatus status, Object source) {
      final MutableInteger result = new MutableInteger(0);
      final String message = (String) source;

      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Confirm Removal Of Track Changes ", message)) {
               result.setValue(1);
            }
         }
      });
      return result.getValue() == 1;
   }
}