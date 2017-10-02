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
package org.eclipse.osee.ote.ui.define.jobs;

import java.util.Arrays;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class ReportErrorsJob extends UIJob {

   private final Object[] objectsWithErrors;
   private final String message;

   private ReportErrorsJob(String title, String message, Object... objectsWithErrors) {
      super(title);
      setUser(false);
      setPriority(LONG);
      this.objectsWithErrors = objectsWithErrors;
      this.message = message;
   }

   @Override
   public IStatus runInUIThread(IProgressMonitor monitor) {
      final String errorMessage = Arrays.deepToString(objectsWithErrors).replaceAll(",", ",\n");
      Shell shell = AWorkbench.getActiveShell();
      ResourceErrorDialog dialog = new ResourceErrorDialog(shell, getName(), message, errorMessage);
      dialog.open();
      return Status.OK_STATUS;
   }

   public static void openError(final String title, final String message, final Object... objectsWithErrors) {
      openError(title, message, null, objectsWithErrors);
   }

   public static void openError(final String title, final String message, final IJobChangeListener listener, final Object... objectsWithErrors) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            ReportErrorsJob errorDialog = new ReportErrorsJob(title, message, objectsWithErrors);
            if (listener != null) {
               errorDialog.addJobChangeListener(listener);
            }
            errorDialog.schedule();
         }
      });
   }
   private final class ResourceErrorDialog extends MessageDialog {

      private final String errorMessage;

      public ResourceErrorDialog(Shell parentShell, String dialogTitle, String dialogMessage, String errorMessage) {
         super(parentShell, dialogTitle,
            PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK), dialogMessage,
            MessageDialog.ERROR, new String[] {IDialogConstants.OK_LABEL}, 0);
         this.errorMessage = errorMessage;
      }

      @Override
      protected Control createCustomArea(Composite parent) {
         Composite composite = new Composite(parent, SWT.NONE);
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         composite.setLayout(new GridLayout());

         Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
         GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
         data.heightHint = 300;
         data.widthHint = 300;
         data.minimumWidth = 300;
         data.minimumHeight = 300;
         text.setLayoutData(data);
         text.setEditable(false);
         text.setText(errorMessage);
         text.setBackground(AWorkbench.getSystemColor(SWT.COLOR_WHITE));
         return composite;
      }
   }
}
