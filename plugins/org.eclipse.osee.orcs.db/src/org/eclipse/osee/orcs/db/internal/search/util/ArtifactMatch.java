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
package org.eclipse.osee.orcs.db.internal.search.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatch implements Match<ReadableArtifact, ReadableAttribute<?>> {

   private final Map<ReadableAttribute<?>, List<MatchLocation>> matchedAttributes;
   private final ReadableArtifact item;

   public ArtifactMatch(ReadableArtifact item, Map<ReadableAttribute<?>, List<MatchLocation>> matchedAttributes) {
      super();
      this.item = item;
      this.matchedAttributes = matchedAttributes;
   }

   @Override
   public boolean hasLocationData() {
      return !matchedAttributes.isEmpty();
   }

   @Override
   public ReadableArtifact getItem() {
      return item;
   }

   @Override
   public Collection<ReadableAttribute<?>> getElements() {
      return matchedAttributes.keySet();
   }

   @Override
   public List<MatchLocation> getLocation(ReadableAttribute<?> element) {
      List<MatchLocation> toReturn = matchedAttributes.get(element);
      return toReturn != null ? toReturn : Collections.<MatchLocation> emptyList();
   }

   @Override
   public String toString() {
      return "ArtifactMatch [item=" + item + ", matchedAttributes=" + matchedAttributes + "]";
   }

}
