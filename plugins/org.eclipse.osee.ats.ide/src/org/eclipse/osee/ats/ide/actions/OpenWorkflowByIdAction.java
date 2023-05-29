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

import java.util.Objects;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Event;

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
      setToolTipText(getText() + "\nClick to open dialog OR\nCtrl-Click to search ID(s) in clipboard");
   }

   @Override
   public void runWithEvent(Event event) {
      MultipleIdSearchData data = null;
      // Use clipboard value if CTRL is down on click
      if ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
         Clipboard clipboard = new Clipboard(AWorkbench.getDisplay());
         String str = (String) clipboard.getContents(TextTransfer.getInstance());
         if (Strings.isValid(str)) {
            data = new MultipleIdSearchData(getText(), AtsEditor.WorkflowEditor);
            data.setEnteredIds(str);
            data.setIncludeArtIds(true);
            data.setBranch(AtsApiService.get().getAtsBranch());
            data.setOpenEach(true);
         }
      }
      // Else, popup entry dialog
      else {
         data = new MultipleIdSearchData(getText(), AtsEditor.WorkflowEditor);
         if (Strings.isValid(overrideId)) {
            data.setEnteredIds(overrideId);
         }
      }
      Objects.requireNonNull(data);
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
      return ImageManager.getImageDescriptor(AtsImage.WORKFLOW);
   }

   public void setOverrideId(String overrideId) {
      this.overrideId = overrideId;
   }

}
