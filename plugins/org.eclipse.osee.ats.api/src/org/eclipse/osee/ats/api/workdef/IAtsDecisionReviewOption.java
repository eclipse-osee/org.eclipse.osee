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

/**
 * @author Donald G. Dunne
 */
public interface IAtsDecisionReviewOption {

   public abstract String getName();

   public abstract void setName(String name);

   public abstract List<String> getUserIds();

   public abstract void setUserIds(List<String> userIds);

   public abstract boolean isFollowupRequired();

   public abstract void setFollowupRequired(boolean followupRequired);

   public abstract List<String> getUserNames();

   public abstract void setUserNames(List<String> userNames);

   @Override
   public abstract String toString();

}