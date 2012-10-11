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
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.world.search.MultipleHridSearchData;
import org.eclipse.osee.ats.world.search.MultipleHridSearchOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenChangeReportByIdAction extends Action {

   private String overrideId = null;
   private boolean pend;

   public void setPend(boolean pend) {
      this.pend = pend;
   }

   public OpenChangeReportByIdAction() {
      this("Open Change Report by ID(s)");
   }

   public OpenChangeReportByIdAction(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {
      MultipleHridSearchData data = new MultipleHridSearchData(getText(), AtsEditor.ChangeReport);
      if (Strings.isValid(overrideId)) {
         data.setEnteredIds(overrideId);
      }
      MultipleHridSearchOperation srchOperation = new MultipleHridSearchOperation(data);
      if (pend) {
         srchOperation.run(null);
      } else {
         Operations.executeAsJob(srchOperation, true);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE);
   }

   public void setOverrideId(String overrideId) {
      this.overrideId = overrideId;
   }

}
