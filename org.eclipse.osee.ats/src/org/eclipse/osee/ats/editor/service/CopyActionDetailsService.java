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

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetailsService extends WorkPageService {

   private Clipboard clipboard;

   public CopyActionDetailsService(SMAManager smaMgr) {
      super(smaMgr);
   }

   private void performCopy() {
      if (clipboard == null) this.clipboard = new Clipboard(null);
      clipboard.setContents(
            new Object[] {"\"" + smaMgr.getSma().getArtifactTypeName() + "\" - " + smaMgr.getSma().getHumanReadableId() + " - \"" + smaMgr.getSma().getDescriptiveName() + "\""},
            new Transfer[] {TextTransfer.getInstance()});
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#createToolbarService()
    */
   @Override
   public Action createToolbarService() {
      Action action = new Action(getName(), Action.AS_PUSH_BUTTON) {
         public void run() {
            performCopy();
         }
      };
      action.setToolTipText(getName());
      action.setImageDescriptor(AtsPlugin.getInstance().getImageDescriptor("copyToClipboard.gif"));
      return action;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.service.WorkPageService#getName()
    */
   @Override
   public String getName() {
      return "Copy " + smaMgr.getSma().getArtifactTypeName() + " details to clipboard";
   }

}
