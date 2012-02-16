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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

/**
 * @author Donald G. Dunne
 */
public class ReadOnlyHyperlinkListener implements IHyperlinkListener {

   private final AbstractWorkflowArtifact sma;

   public ReadOnlyHyperlinkListener(AbstractWorkflowArtifact sma) {
      this.sma = sma;
   }

   @Override
   public void linkActivated(HyperlinkEvent e) {
      if (sma.isHistoricalVersion()) {
         AWorkbench.popup("Historical Error",
            "You can not change a historical version of " + sma.getArtifactTypeName() + ":\n\n" + sma);
      } else {
         AWorkbench.popup("Authentication Error",
            "You do not have permissions to edit " + sma.getArtifactTypeName() + ":" + sma);
      }
   }

   @Override
   public void linkEntered(HyperlinkEvent e) {
      // do nothing
   }

   @Override
   public void linkExited(HyperlinkEvent e) {
      // do nothing
   }

}
