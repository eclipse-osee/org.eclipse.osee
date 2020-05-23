/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.workdef;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;

/**
 * @author Donald G. Dunne
 */
public interface IAtsDecisionReviewDefinition {

   String getName();

   String getDescription();

   String getReviewTitle();

   String getRelatedToState();

   ReviewBlockType getBlockingType();

   StateEventType getStateEventType();

   boolean isAutoTransitionToDecision();

   List<String> getAssignees();

   List<IAtsDecisionReviewOption> getOptions();

   @Override
   String toString();

}
