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

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class RemoveTrackChangesHandler implements IStatusHandler {

   private final int YES = 0;
   private final int YES_TO_ALL = 1;
   private final int NO = 2;

   @Override
   public Object handleStatus(IStatus status, Object source) {
      final MutableBoolean isOkToRemove = new MutableBoolean(false);
      final String message = (String) source;

      final Pair<MutableBoolean, Integer> answer = new Pair<>(isOkToRemove, NO);

      if (RenderingUtil.arePopupsAllowed()) {
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {

               MoreChangesHandlingDialog dialog =
                  new MoreChangesHandlingDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                     "Confirm Removal Of Track Changes ", null, message, MessageDialog.QUESTION, new String[] {
                        IDialogConstants.YES_LABEL,
                        IDialogConstants.YES_TO_ALL_LABEL,
                        IDialogConstants.NO_LABEL},
                     0);
               dialog.updateStyle();

               boolean doesUserConfirm = dialog.open() == YES || dialog.open() == YES_TO_ALL;
               isOkToRemove.setValue(doesUserConfirm);
               answer.setSecond(dialog.open());
            }
         });
      } else {
         // For Test Purposes
         isOkToRemove.setValue(true);
         OseeLog.log(Activator.class, Level.INFO, "Test - accept track change removal.");
      }
      return answer;
   }
   private class MoreChangesHandlingDialog extends MessageDialog {
      public MoreChangesHandlingDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
         super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
            defaultIndex);
      }

      public void updateStyle() {
         int newStyle = getShellStyle();
         newStyle &= SWT.SHEET;
         setShellStyle(getShellStyle() | newStyle);
      }
   }

}
