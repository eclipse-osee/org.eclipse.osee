/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.review;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsReviewService {

   boolean isValidationReviewRequired(IAtsWorkItem workItem) throws OseeCoreException;

   /**
    * Create a new decision review configured and transitioned to handle action validation
    *
    * @param force will force the creation of the review without checking that a review should be created
    */
   IAtsDecisionReview createValidateReview(IAtsTeamWorkflow teamWf, boolean force, Date transitionDate, IAtsUser transitionUser, IAtsChangeSet changes) throws OseeCoreException;

   Collection<IAtsAbstractReview> getReviewsFromCurrentState(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   ReviewBlockType getReviewBlockType(IAtsAbstractReview reviewArt) throws OseeCoreException;

   boolean isStandAloneReview(IAtsAbstractReview review);

   Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf);

   IAtsPeerReviewRoleManager createPeerReviewRoleManager(IAtsPeerToPeerReview peerRev);

}
