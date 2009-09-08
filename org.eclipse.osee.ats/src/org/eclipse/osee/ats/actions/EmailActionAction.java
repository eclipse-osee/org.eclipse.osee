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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.ArtifactEmailWizard;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class EmailActionAction extends Action {

   private final SMAManager smaMgr;

   public EmailActionAction(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
      String title = "Email";
      try {
         title = "Email " + smaMgr.getSma().getArtifactSuperTypeName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      setText(title);
      setToolTipText(getText());
   }

   private void performEmail() throws OseeCoreException {
      ArtifactEmailWizard ew = new ArtifactEmailWizard(smaMgr.getSma());
      WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), ew);
      dialog.create();
      dialog.open();
   }

   @Override
   public void run() {
      try {
         performEmail();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EMAIL);
   }

}
