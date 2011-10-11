/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.api.data;

/**
 * @author Shawn F. Cook
 */
public class SearchResultMatch {
   private final String attributeType;
   private final String matchHint;
   private final int manyMatches;

   public SearchResultMatch(String attributeType, String matchHint, int manyMatches) {
      this.attributeType = attributeType;
      this.matchHint = matchHint;
      this.manyMatches = manyMatches;
   }

   public String getAttributeType() {
      return attributeType;
   }

   public String getMatchHint() {
      return matchHint;
   }

   public int getManyMatches() {
      return manyMatches;
   }

}