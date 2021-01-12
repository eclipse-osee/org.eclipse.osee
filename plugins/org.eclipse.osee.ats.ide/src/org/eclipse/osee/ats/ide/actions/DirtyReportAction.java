/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DirtyReportAction extends AbstractAtsAction {

   private final IDirtyReportable reportable;

   public DirtyReportAction(IDirtyReportable reportable) {
      super("Show Artifact Dirty Report");
      this.reportable = reportable;
      setToolTipText("Show what attribute or relation making editor dirty.");
   }

   @Override
   public void runWithException() {
      XResultData rd = new XResultData();
      rd.log("WorkflowEditor Dirty Report\n-----------------------------------------------\n");
      reportable.isDirtyResult(rd);
      if (AtsUtil.isInTest()) {
         throw new OseeStateException("Dirty Report", rd.isSuccess() ? "Not Dirty" : "Dirty -> " + rd.toString());
      } else {
         rd.log("\n-----------------------------------------------");
         if (rd.isSuccess()) {
            rd.log("Editor is Not Dirty");
         } else {
            rd.error("Editor is Dirty");
         }
         XResultDataUI.report(rd, "Dirty Report - " + (rd.isSuccess() ? "Not Dirty" : "Dirty"));
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DIRTY);
   }

}
