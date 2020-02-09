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
package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenWorkflowByIdAction extends Action {

   private String overrideId = null;
   private boolean pend = false;

   public void setPend(boolean pend) {
      this.pend = pend;
   }

   public OpenWorkflowByIdAction() {
      this("Open Workflow Editor by ID(s)");
   }

   public OpenWorkflowByIdAction(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {

      MultipleIdSearchData data = new MultipleIdSearchData(getText(), AtsEditor.WorkflowEditor);
      if (Strings.isValid(overrideId)) {
         data.setEnteredIds(overrideId);
      }
      MultipleIdSearchOperation operation = new MultipleIdSearchOperation(data);
      if (pend) {
         try {
            Operations.executeWorkAndCheckStatus(operation);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else {
         Operations.executeAsJob(operation, true);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TEAM_WORKFLOW);
   }

   public void setOverrideId(String overrideId) {
      this.overrideId = overrideId;
   }

}
