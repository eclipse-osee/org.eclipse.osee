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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.display.presenter.ArtifactProvider;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author John Misinco
 */
public class MockArtifactProvider implements ArtifactProvider {

   private final Map<String, ReadableArtifact> artifacts = new HashMap<String, ReadableArtifact>();
   private List<ReadableArtifact> artList;

   public void addArtifact(ReadableArtifact artifact) {
      artifacts.put(artifact.getGuid(), artifact);
   }

   public void setArtifactList(List<ReadableArtifact> artList) {
      this.artList = artList;
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) {
      return artifacts.get(token.getGuid());
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) {
      return artifacts.get(guid);
   }

   @Override
   public List<ReadableArtifact> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) {
      return artList;
   }

}