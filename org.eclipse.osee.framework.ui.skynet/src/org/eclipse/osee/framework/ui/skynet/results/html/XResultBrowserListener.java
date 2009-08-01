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

package org.eclipse.osee.framework.ui.skynet.results.html;

import java.net.URL;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Donald G. Dunne
 */
public class XResultBrowserListener implements LocationListener {

   public XResultBrowserListener() {
      super();
   }

   public void changing(LocationEvent event) {
      try {
         String location = event.location;
         if (location.contains("javascript:print")) return;
         String cmdStr = location.replaceFirst("about:blank", "");
         cmdStr = cmdStr.replaceFirst("blank", "");
         XResultBrowserHyperCmd xResultBrowserHyperCmd = XResultBrowserHyperCmd.getCmdStrHyperCmd(cmdStr);
         String value = XResultBrowserHyperCmd.getCmdStrValue(cmdStr);
         if (xResultBrowserHyperCmd == XResultBrowserHyperCmd.openAction) {
            event.doit = false;
            OseeAts.getAtsLib().openArtifact(value, OseeAts.OpenView.ActionEditor);
         } else if (xResultBrowserHyperCmd == XResultBrowserHyperCmd.openArtifctBranch) {
            event.doit = false;
            try {
               java.util.regex.Matcher m = Pattern.compile("^(.*?)\\((.*?)\\)$").matcher(value);
               if (m.find()) {
                  String hrid = m.group(1);
                  Integer branchId = Integer.parseInt(m.group(2));
                  Artifact artifact = ArtifactQuery.getArtifactFromId(hrid, BranchManager.getBranch(branchId));
                  RendererManager.openInJob(artifact, PresentationType.GENERALIZED_EDIT);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         } else if (xResultBrowserHyperCmd == XResultBrowserHyperCmd.openArtifactEditor) {
            event.doit = false;
            OseeAts.getAtsLib().openArtifact(value, OseeAts.OpenView.ArtifactEditor);
         } else if (xResultBrowserHyperCmd == XResultBrowserHyperCmd.openBranch) {
            event.doit = false;
            int branchId = new Integer(value);
            Branch branch = BranchManager.getBranch(branchId);
            BranchView.revealBranch(branch);
         } else if (xResultBrowserHyperCmd == XResultBrowserHyperCmd.browserInternal) {
            event.doit = false;
            IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
            try {
               IWebBrowser browser = browserSupport.createBrowser("osee.ats.navigator.browser");
               browser.openURL(new URL(value));
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         } else if (xResultBrowserHyperCmd == XResultBrowserHyperCmd.browserExternal) {
            event.doit = false;
            Program.launch(value);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Can't process hyperlink.", ex);
      }
   }

   public void changed(LocationEvent event) {
   }

}
