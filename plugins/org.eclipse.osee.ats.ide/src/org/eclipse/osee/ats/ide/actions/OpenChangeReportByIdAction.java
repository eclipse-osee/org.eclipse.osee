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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
      MultipleIdSearchData data = new MultipleIdSearchData(getText(), AtsEditor.ChangeReport);
      if (Strings.isValid(overrideId)) {
         data.setEnteredIds(overrideId);
      }
      MultipleIdSearchOperation srchOperation = new MultipleIdSearchOperation(data);
      if (pend) {
         try {
            Operations.executeWorkAndCheckStatus(srchOperation);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
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
