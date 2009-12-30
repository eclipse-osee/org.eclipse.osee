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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author Donald G. Dunne
 */
public class CopyActionDetailsAction extends Action {

   private Clipboard clipboard;
   private final StateMachineArtifact sma;

   public CopyActionDetailsAction(StateMachineArtifact sma) {
      this.sma = sma;
      String title = "Copy";
      title = "Copy " + sma.getArtifactTypeName() + " details to clipboard";
      setText(title);
      setToolTipText(getText());
   }

   private void performCopy() {
      if (clipboard == null) this.clipboard = new Clipboard(null);
      clipboard.setContents(
            new Object[] {"\"" + sma.getArtifactTypeName() + "\" - " + sma.getHumanReadableId() + " - \"" + sma.getName() + "\""},
            new Transfer[] {TextTransfer.getInstance()});
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
