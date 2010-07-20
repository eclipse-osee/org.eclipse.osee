/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.change.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.change.IChangeReportPreferences;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public class ShowDocumentOrderAction extends Action implements IChangeReportPreferences.Listener {

   private final IChangeReportPreferences documentPreferences;

   public ShowDocumentOrderAction(IChangeReportPreferences documentPreferences) {
      super("Show Changes in Document Order", Action.AS_CHECK_BOX);
      setId("show.change.report.in.document.order");
      this.documentPreferences = documentPreferences;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DOCUMENT));
      updateToolTip();
      documentPreferences.addListener(this);
   }

   @Override
   public void run() {
      documentPreferences.setInDocumentOrder(!documentPreferences.isInDocumentOrder());
   }

   @Override
   public void onDocumentOrderChange(boolean isChecked) {
      setChecked(isChecked);
      updateToolTip();
   }

   private void updateToolTip() {
      String message;
      if (isChecked()) {
         message = "Click to show change report in loaded order";
      } else {
         message = "Click to show change report in document order";
      }
      setToolTipText(message);
   }
}
