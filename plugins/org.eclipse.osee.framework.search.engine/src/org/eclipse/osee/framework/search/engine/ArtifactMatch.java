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
package org.eclipse.osee.framework.search.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactMatch {
   private final int artId;
   private final int branchId;
   private boolean hasMatchLocations;
   private final HashCollection<Long, MatchLocation> attributes;

   public ArtifactMatch(int branchId, int artId) {
      this.artId = artId;
      this.branchId = branchId;
      attributes = new HashCollection<Long, MatchLocation>();
      hasMatchLocations = false;
   }

   void addAttribute(long gammaId, Collection<MatchLocation> matches) {
      if (matches != null && !matches.isEmpty()) {
         hasMatchLocations = true;
      }
      if (matches == null) {
         matches = Collections.emptyList();
      }
      attributes.put(gammaId, matches);
   }

   public Set<Long> getAttributes() {
      return attributes.keySet();
   }

   public Collection<MatchLocation> getMatchLocations(long gammaId) {
      return attributes.getValues(gammaId);
   }

   public boolean hasMatchLocations() {
      return hasMatchLocations;
   }

   public int getArtId() {
      return artId;
   }

   public int getId() {
      return branchId;
   }
}