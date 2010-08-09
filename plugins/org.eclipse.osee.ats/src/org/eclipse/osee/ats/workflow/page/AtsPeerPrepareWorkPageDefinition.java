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

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.flow.PeerToPeerWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerPrepareWorkPageDefinition extends WorkPageDefinition {

   public static String ID =
      PeerToPeerWorkflowDefinition.ID + "." + PeerToPeerReviewArtifact.PeerToPeerReviewState.Prepare.name();

   public AtsPeerPrepareWorkPageDefinition() {
      this(PeerToPeerReviewArtifact.PeerToPeerReviewState.Prepare.name(), ID, null);
   }

   public AtsPeerPrepareWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem("ats.Title");
      addWorkItem(AtsAttributeTypes.ATS_RELATED_TO_STATE);
      addWorkItem(AtsAttributeTypes.ATS_LEGACY_PCR_ID);
      addWorkItem(AtsAttributeTypes.ATS_ROLE);
      addWorkItem(AtsAttributeTypes.ATS_LOCATION);
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(AtsAttributeTypes.ATS_RELATED_TO_STATE);
      addWorkItem(AtsAttributeTypes.ATS_REVIEW_BLOCKS);
      addWorkItem(AtsAttributeTypes.ATS_NEED_BY);
      addWorkItem(AtsAttributeTypes.ATS_ESTIMATED_HOURS);
   }
}
