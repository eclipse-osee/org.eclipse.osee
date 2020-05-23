/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.util;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;

/**
 * @author John Misinco
 */
public interface TokenOrderProcessor {

   int getTotalTokensToMatch();

   void acceptTokenToMatch(String token);

   /**
    * Returns true when all match criteria have been met
    */
   boolean processToken(String token, MatchLocation match);

   List<MatchLocation> getLocations();

   void clearAllLocations();
}
