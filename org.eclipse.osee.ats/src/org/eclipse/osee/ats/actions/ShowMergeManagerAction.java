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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ShowMergeManagerAction extends Action {

   private final SMAManager smaMgr;

   // Since this service is only going to be added for the Implement state, Location.AllState will
   // work
   public ShowMergeManagerAction(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
      setText("Show Merge Manager");
      setToolTipText(getText());
      try {
         setEnabled(smaMgr.getBranchMgr().isWorkingBranchInWork() || smaMgr.getBranchMgr().isCommittedBranchExists());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

   @Override
   public void run() {
      smaMgr.getBranchMgr().showMergeManager();
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.OUTGOING_MERGED);
   }

}
