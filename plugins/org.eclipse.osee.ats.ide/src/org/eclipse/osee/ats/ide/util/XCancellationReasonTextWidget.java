/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.util;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XCancellationReasonTextWidget extends XText implements IArtifactWidget, IWfeEventHandle {

   private AbstractWorkflowArtifact sma;

   public XCancellationReasonTextWidget(AbstractWorkflowArtifact sma, final WorkflowEditor editor) {
      super("Cancallation Reason");
      setArtifact(sma);
      editor.registerEvent(this, AtsAttributeTypes.CancelledReason);
   }

   @Override
   public Result isDirty() {
      if (!Widgets.isAccessible(getControl())) {
         return Result.FalseResult;
      }
      if (!getText().equals(sma.getCancelledReason())) {
         return new Result(true, "Cancallation Reason dirty");
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      if (Strings.isValid(getText())) {
         sma.setCancellationReason(getText(), null);
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof AbstractWorkflowArtifact) {
         this.sma = (AbstractWorkflowArtifact) artifact;
         setText(sma.getCancelledReason());
      }
   }

   @Override
   public AbstractWorkflowArtifact getArtifact() {
      return sma;
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return sma;
   }

}
