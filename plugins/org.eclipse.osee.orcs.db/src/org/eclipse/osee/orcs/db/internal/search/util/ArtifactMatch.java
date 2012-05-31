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
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatch implements Match<ArtifactReadable, AttributeReadable<?>> {

   private final Map<AttributeReadable<?>, List<MatchLocation>> matchedAttributes;
   private final ArtifactReadable item;

   public ArtifactMatch(ArtifactReadable item, Map<AttributeReadable<?>, List<MatchLocation>> matchedAttributes) {
      super();
      this.item = item;
      this.matchedAttributes = matchedAttributes;
   }

   @Override
   public boolean hasLocationData() {
      return !matchedAttributes.isEmpty();
   }

   @Override
   public ArtifactReadable getItem() {
      return item;
   }

   @Override
   public Collection<AttributeReadable<?>> getElements() {
      return matchedAttributes.keySet();
   }

   @Override
   public List<MatchLocation> getLocation(AttributeReadable<?> element) {
      List<MatchLocation> toReturn = matchedAttributes.get(element);
      return toReturn != null ? toReturn : Collections.<MatchLocation> emptyList();
   }

   @Override
   public String toString() {
      return "ArtifactMatch [item=" + item + ", matchedAttributes=" + matchedAttributes + "]";
   }

}
