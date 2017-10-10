/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.handler;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate.ContentType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Roberto E. Escobar
 * @author Karol M. Wilk
 */

public class UIOutlineResolutionHandler implements IStatusHandler {

   @SuppressWarnings("unchecked")
   @Override
   public Object handleStatus(IStatus status, Object source) throws CoreException {
      if (source instanceof ArrayList<?>) {
         final List<String> list = (ArrayList<String>) source;
         final MutableBoolean mutableBoolean = new MutableBoolean(false);
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
               String message = String.format(
                  "Previous valid outline number was \"%s\", is \"%s\" the next outline number? If not, I will treat it as content.",
                  list.get(0), list.get(1));
               mutableBoolean.setValue(
                  MessageDialog.openQuestion(AWorkbench.getActiveShell(), "Help me decide...", message));
            }
         };
         Displays.ensureInDisplayThread(runnable, true);
         return mutableBoolean.booleanValue() ? ContentType.OUTLINE_TITLE : ContentType.CONTENT;
      }

      throw new CoreException(
         new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Invalid source object in UIOutlineResolutionHandler"));
   }
}
