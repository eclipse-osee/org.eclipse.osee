/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.FaceOseeTypes;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProviderBase;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Ryan D. Brooks
 */
public final class CoreTypeTokenProvider extends OrcsTypeTokenProviderBase {
   public static final OrcsTypeTokens osee = new OrcsTypeTokens(NamespaceToken.OSEE);
   public static final NamespaceToken FACE =
      NamespaceToken.valueOf(108, "face", "Namespace for Future Airborne Capability Environment Consortium");
   public static final OrcsTypeTokens face = new OrcsTypeTokens(FACE);
   public static final NamespaceToken OTE =
      NamespaceToken.valueOf(3, "ote", "Namespace for ote system and content management types");
   public static final OrcsTypeTokens ote = new OrcsTypeTokens(OTE);

   public CoreTypeTokenProvider() {
      super(osee, face, ote);

      loadClasses(CoreArtifactTypes.Artifact, CoreAttributeTypes.Name, CoreRelationTypes.Allocation,
         CoreOperationTypes.CreateChildArtifact, FaceOseeTypes.UnitOfConformance, OteArtifactTypes.TestRun,
         OteAttributeTypes.BuildId, OteRelationTypes.TestCaseToRunRelation);
      registerTokenClasses(CoreArtifactTypes.class, CoreAttributeTypes.class, CoreRelationTypes.class,
         CoreOperationTypes.class, CoreTupleTypes.class, FaceOseeTypes.class, OteArtifactTypes.class,
         OteAttributeTypes.class, OteRelationTypes.class);
   }
}