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
package org.eclipse.osee.ats.api.workdef;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;

/**
 * @author Donald G. Dunne
 */
public interface IAtsDecisionReviewDefinition {

   /**
    * Identification
    */
   public abstract String getName();

   public abstract String getDescription();

   /**
    * Created Review Options
    */
   public abstract String getReviewTitle();

   public abstract String getRelatedToState();

   public abstract ReviewBlockType getBlockingType();

   public abstract StateEventType getStateEventType();

   public abstract boolean isAutoTransitionToDecision();

   public abstract List<String> getAssignees();

   public abstract List<IAtsDecisionReviewOption> getOptions();

   @Override
   public abstract String toString();

}