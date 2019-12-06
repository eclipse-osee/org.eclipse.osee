/*******************************************************************************
* Copyright (c) 2019 Boeing.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Boeing - initial API and implementation
*******************************************************************************/
package org.eclipse.osee.ote.define;

import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProviderBase;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Ryan D. Brooks
 */
public final class OteTypeTokenProvider extends OrcsTypeTokenProviderBase {
   private static final NamespaceToken OTE =
      NamespaceToken.valueOf(3, "ote", "Namespace for ote system and content management types");
   public static final OrcsTypeTokens ote = new OrcsTypeTokens(OTE);

   public OteTypeTokenProvider() {
      super(ote);
      loadClasses(OteArtifactTypes.TestRun, OteAttributeTypes.BuildId, OteRelationTypes.TestCaseToRunRelation);
   }
}