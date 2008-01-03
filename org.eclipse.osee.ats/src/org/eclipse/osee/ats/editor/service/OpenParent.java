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
package org.eclipse.osee.ats.editor.service;

import java.sql.SQLException;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class OpenParent extends WorkPageService {

   public OpenParent(SMAManager smaMgr) {
      super(smaMgr);
   }

   private void performOpen() {
      try {
         AtsLib.openAtsAction(smaMgr.getSma().getParentSMA(), AtsOpenOption.OpenOneOrPopupSelect);
      } catch (SQLException ex) {
         // Do Nothing
      }
   }

   @Override
   public Action createToolbarService() {
      if (!isParent()) return null;
      Action action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performOpen();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("openParent.gif"));
      return action;
   }

   private boolean isParent() {
      try {
         return smaMgr.getSma().getParentSMA() != null;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Open Parent";
   }

}
