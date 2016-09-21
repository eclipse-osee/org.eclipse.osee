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
package org.eclipse.osee.framework.skynet.core.mocks;

import java.util.Random;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public final class DataFactory {
   private final static Random randomGenerator = new Random();

   private DataFactory() {
      // Utility Class
   }

   public static ArtifactType fromToken(IArtifactType artifactType) {
      String name = artifactType.getName();
      Long guid = artifactType.getGuid();
      return new ArtifactType(guid, name, true);
   }

   public static IArtifact createArtifact(String name, String guid) {
      int uniqueId = randomGenerator.nextInt();
      return createArtifact(uniqueId, name, guid, null, fromToken(CoreArtifactTypes.Artifact));
   }

   private static IArtifact createArtifact(int uniqueId, String name, String guid, BranchId branch, ArtifactType artifactType) {
      return new MockIArtifact(uniqueId, name, guid, branch, artifactType);
   }
}