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
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

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

   public XNavigateItemAction(XNavigateItem parent, String name, OseeImage oseeImage) {
      this(parent, name, false, oseeImage);
   }

   public XNavigateItemAction(XNavigateItem parent, String name, boolean promptFirst, OseeImage oseeImage) {
      super(parent, name, oseeImage);
      this.action = null;
      this.promptFirst = promptFirst;
   }

   public XNavigateItemAction(XNavigateItem parent, Action action, OseeImage oseeImage) {
      this(parent, action, oseeImage, false);
   }

   public XNavigateItemAction(XNavigateItem parent, Action action, OseeImage oseeImage, boolean promptFirst) {
      super(parent, action.getText(), oseeImage);
      this.action = action;
      this.promptFirst = promptFirst;
   }

   @Deprecated
   public XNavigateItemAction(XNavigateItem parent, Action action, Image oseeImage, boolean promptFirst) {
      super(parent, action.getText(), oseeImage);
      this.action = action;
      this.promptFirst = promptFirst;
   }
   
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      if (action != null) {
         if (promptFirst) {
            Displays.ensureInDisplayThread(new Runnable() {
               /*
                * (non-Javadoc)
                * 
                * @see java.lang.Runnable#run()
                */
               public void run() {
                  if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) {
                     action.run();
                  }
               }
            });
         } else if (action.getStyle() == Action.AS_CHECK_BOX) {
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
