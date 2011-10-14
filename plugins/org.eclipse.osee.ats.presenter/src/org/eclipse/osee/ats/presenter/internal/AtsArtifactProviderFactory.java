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
package org.eclipse.osee.ats.presenter.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.search.AtsArtifactProvider;
import org.eclipse.osee.ats.mocks.MockAtsArtifactProvider;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.mock.MockArtifact;
import org.eclipse.osee.orcs.mock.MockAttribute;
import org.eclipse.osee.orcs.mock.MockMatch;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author John Misinco
 */
public class AtsArtifactProviderFactory {

   public static AtsArtifactProvider createAtsArtifactProvider(QueryFactory factory) {
      if (factory != null) {
         return new AtsArtifactProviderImpl(factory);
      } else {
         MockAtsArtifactProvider provider = new MockAtsArtifactProvider();
         List<Match<ReadableArtifact, ReadableAttribute<?>>> searchMatches =
            new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();

         MockArtifact art = new MockArtifact("AE92c1fShgbFbCTPbCgA", "matchArt");
         MockArtifact parent = new MockArtifact("AE92c1fShgbFbCTPbCgZ", "parentArt");
         MockArtifact grandParent = new MockArtifact("AE92c1fShgbFbCTPbCg4", "grandParentArt");
         provider.addArtifact(art);
         provider.addArtifact(parent);
         provider.addArtifact(grandParent);

         parent.setParent(grandParent);
         art.setParent(parent);
         MockArtifact related = new MockArtifact("AE92c1ibUQjf8oJxPPwA", "related1");
         art.addRelation(CoreRelationTypes.Allocation__Component, related);
         provider.addArtifact(art);
         MockAttribute attr = new MockAttribute(CoreAttributeTypes.Name, "matchArt");

         Match match = new MockMatch(art, attr);
         searchMatches.add(match);
         provider.setResultList(searchMatches);
         return provider;
      }
   }
}
