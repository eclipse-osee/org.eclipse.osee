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

package org.eclipse.osee.ats.actions;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetailsAction extends Action {

   private Clipboard clipboard;
   private final SMAManager smaMgr;

   public CopyActionDetailsAction(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
      String title = "Copy";
      try {
         title = "Copy " + smaMgr.getSma().getArtifactTypeName() + " details to clipboard";
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      setText(title);
      setToolTipText(getText());
   }

   private void performCopy() {
      try {
         if (clipboard == null) this.clipboard = new Clipboard(null);
         clipboard.setContents(
               new Object[] {"\"" + smaMgr.getSma().getArtifactTypeName() + "\" - " + smaMgr.getSma().getHumanReadableId() + " - \"" + smaMgr.getSma().getName() + "\""},
               new Transfer[] {TextTransfer.getInstance()});
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void run() {
      performCopy();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.COPY_TO_CLIPBOARD);
   }

}
