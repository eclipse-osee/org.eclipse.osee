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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XCancellationReasonTextWidget extends XText implements IArtifactWidget {

   private AbstractWorkflowArtifact sma;

   public XCancellationReasonTextWidget(AbstractWorkflowArtifact sma)  {
      super("Cancallation Reason");
      setArtifact(sma);
   }

   @Override
   public Result isDirty()  {
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
   public void saveToArtifact()  {
      if (Strings.isValid(getText())) {
         sma.setCancellationReason(getText(), null);
      }
   }

   @Override
   public void setArtifact(Artifact artifact)  {
      if (artifact instanceof AbstractWorkflowArtifact) {
         this.sma = (AbstractWorkflowArtifact) artifact;
         setText(sma.getCancelledReason());
      }
   }

   @Override
   public AbstractWorkflowArtifact getArtifact() {
      return sma;
   }

}
