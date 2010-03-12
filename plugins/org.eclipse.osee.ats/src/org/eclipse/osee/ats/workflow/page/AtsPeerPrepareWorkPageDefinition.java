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
public class AtsPeerPrepareWorkPageDefinition extends WorkPageDefinition {

   public static String ID = PeerToPeerWorkflowDefinition.ID + "." + PeerToPeerReviewArtifact.PeerToPeerReviewState.Prepare.name();

   public AtsPeerPrepareWorkPageDefinition() {
      this(PeerToPeerReviewArtifact.PeerToPeerReviewState.Prepare.name(), ID, null);
   }

   public AtsPeerPrepareWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem("ats.Title");
      addWorkItem(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.LEGACY_PCR_ID_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.ROLE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.LOCATION_ATTRIBUTE.getStoreName());
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.NEED_BY_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName());
   }
}
