/*********************************************************************
 * Copyright (c) 2010 Boeing
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
package org.eclipse.osee.framework.core.access;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Roberto E. Escobar
 */
public final class FrameworkOseeAccessProvider implements IOseeAccessProvider {

   public static Collection<ArtifactCheck> artChecks = null;

   @Override
   public Collection<ArtifactCheck> getArtifactChecks() {
      if (artChecks == null) {
         artChecks = Arrays.asList(new UserArtifactCheck());
      }
      return artChecks;
   }

}
