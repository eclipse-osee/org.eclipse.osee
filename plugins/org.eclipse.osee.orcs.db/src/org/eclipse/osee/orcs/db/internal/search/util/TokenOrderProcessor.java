/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
