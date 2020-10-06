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

package org.eclipse.osee.ats.api.review;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemProvider;

/**
 * @author Donald G. Dunne
 */
public interface IAtsAbstractReview extends IAtsWorkItem, IAtsActionableItemProvider {

   String getRelatedToState();

   boolean isStandAloneReview();

   @Override
   default boolean hasAction() {
      return !isStandAloneReview();
   }

}
