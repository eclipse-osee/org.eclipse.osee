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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
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
      Result result = reportable.isDirtyResult();
      if (AtsUtilCore.isInTest()) {
         throw new OseeStateException("Dirty Report", result.isFalse() ? "Not Dirty" : "Dirty -> " + result.getText());
      } else {
         AWorkbench.popup("Dirty Report", result.isFalse() ? "Not Dirty" : "Dirty -> " + result.getText());
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DIRTY);
   }

}
