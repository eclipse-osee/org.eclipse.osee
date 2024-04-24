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

package org.eclipse.osee.framework.ui.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * @author Donald G. Dunne
 * @author Loren K. Ashley
 */

public final class AWorkbench {

   public static enum MessageType {

      Confirm {

         @Override
         protected int getKind() {
            return MessageDialog.CONFIRM;
         }

         @Override
         protected String[] getButtonLabels() {
            return new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL};
         }

      },

      Error {

         @Override
         protected int getKind() {
            return MessageDialog.ERROR;
         }

         @Override
         protected String[] getButtonLabels() {
            return new String[] {IDialogConstants.OK_LABEL};
         }

      },

      Informational {

         @Override
         protected int getKind() {
            return MessageDialog.INFORMATION;
         }

         @Override
         protected String[] getButtonLabels() {
            return new String[] {IDialogConstants.OK_LABEL};
         }

      };

      abstract protected int getKind();

      abstract protected String[] getButtonLabels();
   }

   /**
    * This option includes a scrolling text box in the pop-up. The text box will be populated with the long message.
    */

   public static final int SCROLLING_TEXT_BOX = 1;

   public static IWorkbenchPage getActivePage() {
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      return workbenchWindow != null ? workbenchWindow.getActivePage() : null;
   }

   public static Shell getActiveShell() {
      return getDisplay().getActiveShell();
   }

   public static Display getDisplay() {
      return PlatformUI.getWorkbench().getDisplay();
   }

   public static List<IEditorReference> getEditors() {
      List<IEditorReference> editors = new ArrayList<>();
      for (IWorkbenchPage page : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()) {
         for (IEditorReference editor : page.getEditorReferences()) {
            editors.add(editor);
         }
      }
      return editors;
   }

   public static List<IEditorReference> getEditors(String editorId) {
      List<IEditorReference> editors = new ArrayList<>();
      for (IEditorReference editor : getEditors()) {
         if (editor.getId().equals(editorId)) {
            editors.add(editor);
         }
      }
      return editors;
   }

   public static Color getSystemColor(int id) {
      return getDisplay().getSystemColor(id);
   }

   public static IViewPart getView(String viewId) {
      return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewId);
   }

   public static void openPerspective(final String perspId) {
      final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      IPerspectiveDescriptor activePerspective = workbenchWindow.getActivePage().getPerspective();
      if (activePerspective == null || !activePerspective.getId().equals(perspId)) {
         Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {
               try {
                  workbenchWindow.getWorkbench().showPerspective(perspId, workbenchWindow);
               } catch (WorkbenchException ex) {
                  OseeLog.log(OseeUiActivator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      }
   }

   public static void popup(Composite comp, String title, String message) {
      MessageDialog.openInformation(comp.getShell(), title, message);
   }

   public static void popup(final MessageType messageType, final String title, final String message) {
      if (!PlatformUI.isWorkbenchRunning()) {
         OseeLog.log(AWorkbench.class, Level.SEVERE, message);
      } else {
         getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
               if (messageType == MessageType.Informational) {
                  MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);
               } else if (messageType == MessageType.Error) {
                  MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);

               } else if (messageType == MessageType.Confirm) {
                  MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);

               }
            }
         });
      }
   }

   /**
    * Displays a pop-up window for the user. When the Eclipse workbench is not running the message is logged with
    * {@link OseeLog#log}. The following <code>options</code> are supported:
    * <dl>
    * <dt>none (0):</dt>
    * <dd>A pop-up window of the type specified by <code>messageType</code> is displayed with the window title specified
    * by <code>title</code> and the <code>message</code> displayed on the pop-up background. <code>longMessage</code> is
    * not used. This will result in the same pop-up as if the method
    * {@link AWorkbench#popup(MessageType, String, String)} was called with the parameters <code>messageType</code>,
    * <code>title</code>, <code>longMessage</code>.</dd>
    * <dt>{@link AWorkbench#SCROLLING_TEXT_BOX}:</dt>
    * <dd>A pop-up window of the type specified by <code>messageType</code> is displayed with the window title specified
    * by <code>title</code>, the <code>message</code> displayed on the pop-up background, and a scrolling text box with
    * the <code>longMessage</code>.</dd>
    * </dl>
    *
    * @param messageType the type of message pop-up window.
    * @param title the window title.
    * @param message a short message displayed on the pop-up background.
    * @param longMessage a longer message displayed according to the <code>options</code>.
    * @param options the pop-up window display options.
    */

   public static void popup(final MessageType messageType, final String title, String message, final String longMessage,
      int options) {

      if (!PlatformUI.isWorkbenchRunning()) {

         //@formatter:off
         final var logMessage =
            new Message()
                   .title( title )
                   .indentInc()
                   .block( message )
                   .followsIfNonNull( "Reason Follows", longMessage )
                   .toString();
         //@formatter:on

         OseeLog.log(AWorkbench.class, Level.SEVERE, logMessage);

      }

      switch (options) {

         case 0:
         default: {
            AWorkbench.popup(messageType, title, message);
            return;
         }

         case SCROLLING_TEXT_BOX: {
            //@formatter:off
            AWorkbench
               .getDisplay()
               .syncExec
                  (
                     new Runnable()
                     {

                        @Override
                        public void run() {

                           final var workbench = PlatformUI.getWorkbench();
                           final var window = workbench.getActiveWorkbenchWindow();
                           final var shell = window.getShell();

                            final var messageDialog =
                              new MessageDialog
                                     (
                                        shell,
                                        title,
                                        null,
                                        message,
                                        messageType.getKind(),
                                        0,
                                        messageType.getButtonLabels()
                                     ) {

                              @Override
                              protected Control createCustomArea(Composite parent) {

                                 final var textGridData = new GridData();

                                 textGridData.horizontalAlignment = SWT.FILL;
                                 textGridData.grabExcessHorizontalSpace = true;
                                 textGridData.minimumWidth = 128;
                                 textGridData.widthHint = 128;

                                 textGridData.verticalAlignment = SWT.FILL;
                                 textGridData.grabExcessVerticalSpace = true;
                                 textGridData.minimumHeight = 256;
                                 textGridData.heightHint = 256;

                                 final var text = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );

                                 text.setEditable(false);
                                 text.setLayoutData( textGridData );

                                 text.setText(longMessage);

                                 return text;
                              }

                              void display() {

                                 final var defaultStyles = this.getShellStyle();
                                 this.setShellStyle( defaultStyles | SWT.RESIZE );
                                 this.open();
                              }
                           };

                           messageDialog.display();

                        }
                     }
                  );

            return;
         }

      }

   }

   public static void popup(Result result) {
      popup((String) null, result);
   }

   public static void popup(final String message) {
      popup(message, message);
   }

   public static void popup(String title, Result result) {
      AWorkbench.popup(Strings.isValid(title) ? title : result.isTrue() ? "Success" : "ERROR", Strings.isValid(
         result.getText()) ? result.getText() : result.isTrue() ? "Success" : "Error Encountered.  See Error Log View");

   }

   public static void popup(String title, String message) {
      popup(MessageType.Informational, title, message);
   }


   public static void popup(String title, String messageFormat, Object... data) {
      popup(MessageType.Informational, title, String.format(messageFormat, data));
   }

   public static void popupf(String string, Object... data) {
      popup(String.format(string, data));
   }

   /**
    * Popup a workbench viewer eg: AWorkbench.popupView(IPageLayout.ID_PROBLEM_VIEW);
    */
   public static boolean popupView(String iPageLayoutView) {
      IViewPart p = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iPageLayoutView);
      if (p != null) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(p);
         return true;
      }
      return false;
   }
}