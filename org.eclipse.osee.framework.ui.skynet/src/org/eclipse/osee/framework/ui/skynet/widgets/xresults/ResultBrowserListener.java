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

package org.eclipse.osee.framework.ui.skynet.widgets.xresults;

import java.util.regex.Pattern;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;

/**
 * @author Donald G. Dunne
 */
public class ResultBrowserListener implements LocationListener {

   /**
    * 
    */
   public ResultBrowserListener() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.swt.browser.LocationListener#changing(org.eclipse.swt.browser.LocationEvent)
    */
   public void changing(LocationEvent event) {
      try {
         String location = event.location;
         if (location.contains("javascript:print")) return;
         String cmdStr = location.replaceFirst("about:blank", "");
         cmdStr = cmdStr.replaceFirst("blank", "");
         ResultBrowserHyperCmd resultBrowserHyperCmd = ResultBrowserHyperCmd.getCmdStrHyperCmd(cmdStr);
         String value = ResultBrowserHyperCmd.getCmdStrValue(cmdStr);
         if (resultBrowserHyperCmd == ResultBrowserHyperCmd.openAction) {
            event.doit = false;
            OseeAts.getAtsLib().openArtifact(value, OseeAts.OpenView.ActionEditor);
         }
         if (resultBrowserHyperCmd == ResultBrowserHyperCmd.openArtifctBranch) {
            event.doit = false;
            try {
               java.util.regex.Matcher m = Pattern.compile("^(.*?)\\((.*?)\\)$").matcher(value);
               if (m.find()) {
                  String hrid = m.group(1);
                  Integer branchId = Integer.parseInt(m.group(2));
                  OseeAts.getAtsLib().openArtifact(hrid, branchId, OseeAts.OpenView.ActionEditor);
               }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         } else if (resultBrowserHyperCmd == ResultBrowserHyperCmd.openArtifactEditor) {
            event.doit = false;
            OseeAts.getAtsLib().openArtifact(value, OseeAts.OpenView.ArtifactEditor);
         } else if (resultBrowserHyperCmd == ResultBrowserHyperCmd.openArtifactHyperViewer) {
            event.doit = false;
            OseeAts.getAtsLib().openArtifact(value, OseeAts.OpenView.ArtifactHyperViewer);
         } else if (resultBrowserHyperCmd == ResultBrowserHyperCmd.openBranch) {
            event.doit = false;
            int branchId = new Integer(value);
            Branch branch = BranchPersistenceManager.getInstance().getBranch(branchId);
            BranchView.revealBranch(branch);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, "Can't process hyperlink.", ex, true);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.swt.browser.LocationListener#changed(org.eclipse.swt.browser.LocationEvent)
    */
   public void changed(LocationEvent event) {
   }

}
