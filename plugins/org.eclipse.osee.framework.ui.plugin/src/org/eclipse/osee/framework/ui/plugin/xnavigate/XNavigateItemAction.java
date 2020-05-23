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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * Used to perform a specific java action
 * 
 * @author Donald G. Dunne
 */
public class XNavigateItemAction extends XNavigateItem {

   private final Action action;
   private boolean promptFirst = false;

   public XNavigateItemAction(XNavigateItem parent, String name) {
      this(parent, name, null);
   }

   public XNavigateItemAction(XNavigateItem parent, String name, KeyedImage oseeImage) {
      this(parent, name, false, oseeImage);
   }

   public XNavigateItemAction(XNavigateItem parent, String name, boolean promptFirst, KeyedImage oseeImage) {
      super(parent, name, oseeImage);
      this.action = null;
      this.promptFirst = promptFirst;
   }

   public XNavigateItemAction(XNavigateItem parent, Action action, KeyedImage oseeImage) {
      this(parent, action, oseeImage, false);
   }

   public XNavigateItemAction(XNavigateItem parent, Action action, KeyedImage oseeImage, boolean promptFirst) {
      super(parent, action.getText(), oseeImage);
      this.action = action;
      this.promptFirst = promptFirst;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      if (action != null) {
         if (promptFirst) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName() + "?")) {
                     action.run();
                  }
               }
            });
         } else if (action.getStyle() == IAction.AS_CHECK_BOX) {
            action.setChecked(!action.isChecked());
            if (action.isChecked()) {
               action.run();
            }
         } else {
            action.run();
         }
      }
   }

   public boolean isPromptFirst() {
      return promptFirst;
   }

   public void setPromptFirst(boolean promptFirst) {
      this.promptFirst = promptFirst;
   }

}
