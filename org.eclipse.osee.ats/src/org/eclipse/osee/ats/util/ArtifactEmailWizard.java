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

import java.util.ArrayList;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailGroup;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailWizard;

public class ArtifactEmailWizard extends EmailWizard {

   public ArtifactEmailWizard(StateMachineArtifact sma) {
      this(sma, null);
   }

   public ArtifactEmailWizard(StateMachineArtifact sma, ArrayList<Object> toAddress) {
      super();
      setInitialAddress(toAddress);
      setHtmlMessage(sma.getPreviewHtml(PreviewStyle.HYPEROPEN, PreviewStyle.NO_SUBSCRIBE_OR_FAVORITE));
      setSubject(" Regarding " + sma.getArtifactTypeName() + " - " + sma.getDescriptiveName());
      setEmailableGroups(getEmailableGroups(sma));
   }

   private ArrayList<EmailGroup> getEmailableGroups(StateMachineArtifact sma) {
      return sma.getEmailableGroups();
   }
}
