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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

/**
 * @author Donald G. Dunne
 */
public class ReadOnlyHyperlinkListener implements IHyperlinkListener {

   private final SMAManager smaMgr;

   public ReadOnlyHyperlinkListener(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   @Override
   public void linkActivated(HyperlinkEvent e) {
      try {
         if (smaMgr.isHistoricalVersion())
            AWorkbench.popup(
                  "Historical Error",
                  "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());

         else
            AWorkbench.popup(
                  "Authentication Error",
                  "You do not have permissions to edit " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   @Override
   public void linkEntered(HyperlinkEvent e) {
   }

   @Override
   public void linkExited(HyperlinkEvent e) {
   }

}
