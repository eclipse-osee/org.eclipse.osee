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

import java.sql.SQLException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
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
      this(parent, name, false);
   }

   public XNavigateItemAction(XNavigateItem parent, String name, boolean promptFirst) {
      super(parent, name);
      this.action = null;
      this.promptFirst = promptFirst;
   }

   public XNavigateItemAction(XNavigateItem parent, Action action) {
      this(parent, action, null, false);
   }

   public XNavigateItemAction(XNavigateItem parent, Action action, Image image, boolean promptFirst) {
      super(parent, action.getText(), image);
      this.action = action;
      this.promptFirst = promptFirst;
   }

   public void run() throws SQLException {
      if (action != null) {
         if (promptFirst) {
            Displays.ensureInDisplayThread(new Runnable() {
               /*
                * (non-Javadoc)
                * 
                * @see java.lang.Runnable#run()
                */
               public void run() {
                  if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) action.run();
               }
            });
         } else
            action.run();
      }
   }

   public boolean isPromptFirst() {
      return promptFirst;
   }

   public void setPromptFirst(boolean promptFirst) {
      this.promptFirst = promptFirst;
   }

}
