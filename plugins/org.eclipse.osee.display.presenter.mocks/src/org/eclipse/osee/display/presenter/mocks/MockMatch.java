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
package org.eclipse.osee.display.presenter.mocks;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author John R. Misinco
 */
public class MockMatch implements Match<ArtifactReadable, AttributeReadable<String>> {

   private final ArtifactReadable artifact;
   private final List<AttributeReadable<String>> attributes = new LinkedList<AttributeReadable<String>>();

   public MockMatch(ArtifactReadable artifact) {
      this.artifact = artifact;
   }

   public MockMatch(ArtifactReadable artifact, AttributeReadable<String> attribute) {
      this(artifact);
      attributes.add(attribute);
   }

   public MockMatch(ArtifactReadable artifact, List<AttributeReadable<String>> attributes) {
      this(artifact);
      attributes.addAll(attributes);
   }

   @Override
   public boolean hasLocationData() {
      return false;
   }

   @Override
   public ArtifactReadable getItem() {
      return artifact;
   }

   @Override
   public List<AttributeReadable<String>> getElements() {
      return attributes;
   }

   @Override
   public List<MatchLocation> getLocation(AttributeReadable<String> element) {
      List<MatchLocation> toReturn = new LinkedList<MatchLocation>();
      toReturn.add(new MatchLocation());
      return toReturn;
   }
}