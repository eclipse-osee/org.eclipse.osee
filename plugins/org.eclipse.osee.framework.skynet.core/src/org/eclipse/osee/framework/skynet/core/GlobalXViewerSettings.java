/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.XViewerCustomization;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * This singleton artifact stores the default customizations for XViewers
 *
 * @author Donald G. Dunne
 */
public final class GlobalXViewerSettings {

   private static final Supplier<Artifact> configurationsCache =
      Suppliers.memoizeWithExpiration(getConfigArtifactSupplier(), 10, TimeUnit.MINUTES);

   private static Supplier<Artifact> getConfigArtifactSupplier() {
      return new Supplier<Artifact>() {
         @Override
         public Artifact get() {
            return ArtifactQuery.getArtifactFromToken(XViewerCustomization);
         }
      };
   }

   public static Artifact getCustomArtifact() {
      return configurationsCache.get();
   }
}