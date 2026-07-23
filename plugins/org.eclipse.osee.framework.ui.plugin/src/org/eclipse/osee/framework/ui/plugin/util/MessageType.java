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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * @author Loren K. Ashley
 */
public enum MessageType {

   /**
    * Creates a confirmation dialog with an "OK" and "CANCEL" buttons.
    */
   Confirm {
      @Override
      protected int getKind() {
         return MessageDialog.CONFIRM;
      }

      @Override
      protected String[] getButtonLabels(int options) {
         return MessageType.OK_CANCEL_BUTTON_SET;
      }
   },

   /**
    * Creates an error dialog with an "OK" button. A "CANCEL" button will only be included when the option
    * {@link AWorkbench#INCLUDE_CANCEL_BUTTON} was specified.
    */
   Error {

      @Override
      protected int getKind() {
         return MessageDialog.ERROR;
      }

      @Override
      protected String[] getButtonLabels(int options) {

         return ((options & AWorkbench.INCLUDE_CANCEL_BUTTON) > 0) ? MessageType.OK_CANCEL_BUTTON_SET : MessageType.OK_BUTTON_SET;
      }

   },

   /**
    * Creates an information dialog with an "OK" button. A "CANCEL" button is not provided.
    */
   Informational {

      @Override
      protected int getKind() {
         return MessageDialog.INFORMATION;
      }

      @Override
      protected String[] getButtonLabels(int options) {
         return MessageType.OK_BUTTON_SET;
      }

   };

   private static final String[] OK_BUTTON_SET = new String[] {IDialogConstants.OK_LABEL};

   public static final String[] OK_CANCEL_BUTTON_SET =
      new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL};

   abstract protected int getKind();

   abstract protected String[] getButtonLabels(int options);
}