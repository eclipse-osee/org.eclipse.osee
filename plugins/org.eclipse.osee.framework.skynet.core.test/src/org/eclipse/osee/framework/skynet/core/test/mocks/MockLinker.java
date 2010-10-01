/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *public static final CoreAttributeTypes   Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.mocks;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink.ArtifactLinker;

/**
 * @author Roberto E. Escobar
 */
public class MockLinker implements ArtifactLinker {

   private final String name;

   public MockLinker(String name) {
      super();
      this.name = name;
   }

   public MockLinker() {
      this(null);
   }

   @Override
   public void updateCachedArtifact(int artId, Branch branch) {
      //
   }

   @Override
   public Artifact getArtifact(int ArtId, Branch branch) {
      return null;
   }

   @Override
   public String getLazyArtifactName(int aArtifactId, Branch branch) {
      return name;
   }

   @Override
   public void deleteFromRelationOrder(Artifact aArtifact, Artifact bArtifact, RelationType relationType) throws OseeCoreException {
      //
   }
}