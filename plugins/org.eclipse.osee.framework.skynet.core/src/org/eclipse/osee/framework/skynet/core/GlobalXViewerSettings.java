/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.XViewerGlobalCustomization;
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
            return ArtifactQuery.getArtifactFromToken(XViewerGlobalCustomization);
         }
      };
   }

   public static Artifact getCustomArtifact() {
      return configurationsCache.get();
   }
}