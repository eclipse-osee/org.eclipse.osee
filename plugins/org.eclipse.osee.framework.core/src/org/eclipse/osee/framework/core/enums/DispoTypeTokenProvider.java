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

import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProviderBase;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Ryan D. Brooks
 */
public final class DispoTypeTokenProvider extends OrcsTypeTokenProviderBase {
   public static final NamespaceToken DISPO =
      NamespaceToken.valueOf(4, "dispo", "Namespace for dispo system and content management types");

   public static final OrcsTypeTokens dispo = new OrcsTypeTokens(DISPO);

   public DispoTypeTokenProvider() {
      super(dispo);

      loadClasses(DispoOseeTypes.DispoCiSet, CoverageOseeTypes.Assignees);
      registerTokenClasses(DispoOseeTypes.class, CoverageOseeTypes.class);
   }
}