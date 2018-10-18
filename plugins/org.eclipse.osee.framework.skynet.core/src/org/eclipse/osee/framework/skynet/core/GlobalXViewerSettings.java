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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
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
            return loadConfigArtifact();
         }
      };
   }

   private static Artifact loadConfigArtifact() {
      Artifact configArt = ArtifactQuery.getArtifactFromTokenOrNull(CoreArtifactTokens.XViewerGlobalCustomization,
         CoreBranches.COMMON, DeletionFlag.EXCLUDE_DELETED);
      if (configArt == null) {
         configArt = OseeSystemArtifacts.getCachedArtifact(CoreArtifactTypes.XViewerGlobalCustomization,
            CoreArtifactTypes.XViewerGlobalCustomization.getName(), COMMON);
      }
      return configArt;
   }

   public static Artifact getCustomArtifact() throws OseeCoreException {
      return configurationsCache.get();
   }

   public static Artifact createCustomArtifact() throws OseeCoreException {
      return ArtifactTypeManager.addArtifact(CoreArtifactTokens.XViewerGlobalCustomization, COMMON);
   }
}