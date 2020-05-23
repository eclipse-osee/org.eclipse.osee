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

package org.eclipse.osee.ats.ide.util;

import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
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
      if (sma.isHistorical()) {
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
