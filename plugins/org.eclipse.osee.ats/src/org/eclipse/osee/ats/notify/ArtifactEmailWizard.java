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
package org.eclipse.osee.ats.notify;

import java.util.List;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailWizard;

/**
 * @author Donald G. Dunne
 */
public class ArtifactEmailWizard extends EmailWizard {

   public ArtifactEmailWizard(AbstractWorkflowArtifact sma) throws OseeCoreException {
      this(sma, null);
   }

   public ArtifactEmailWizard(AbstractWorkflowArtifact sma, List<Object> toAddress) throws OseeCoreException {
      super(
         AtsNotificationManagerUI.getPreviewHtml(sma, PreviewStyle.HYPEROPEN, PreviewStyle.NO_SUBSCRIBE_OR_FAVORITE),
         " Regarding " + sma.getArtifactTypeName() + " - " + sma.getName(),
         AtsNotificationManager.getEmailableGroups(sma), toAddress);
   }

}
