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
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerReviewWorkPageDefinition extends WorkPageDefinition {

   public static String ID = PeerToPeerWorkflowDefinition.ID + "." + PeerToPeerReviewArtifact.PeerToPeerReviewState.Review.name();

   public AtsPeerReviewWorkPageDefinition() {
      this(PeerToPeerReviewArtifact.PeerToPeerReviewState.Review.name(), ID, null);
   }

   public AtsPeerReviewWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(ATSAttributes.ROLE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.REVIEW_DEFECT_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
   }

}
