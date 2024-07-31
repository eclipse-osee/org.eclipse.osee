/*********************************************************************
 * Copyright (c) 2011 Boeing
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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.MimImport;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.MimReport;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Ryan T. Baldwin
 */
public final class MimArtifactTokens {

   // @formatter:off
   public static final ArtifactToken InterfaceMessagesFolder                     = ArtifactToken.valueOf(8255184, "Interface Messages", Folder);
   public static final ArtifactToken InterfacePlatformTypesFolder                = ArtifactToken.valueOf(8255185, "Interface Platform Types", Folder);
   public static final ArtifactToken MimConfigFolder                             = ArtifactToken.valueOf(168451920, "MIM", Folder);

   public static final ArtifactToken MimIcdImport                                = ArtifactToken.valueOf(365842840, "MIM ICD", MimImport);
   public static final ArtifactToken MimIcdReport                                = ArtifactToken.valueOf(365842841, "MIM ICD", MimReport);
   // @formatter:on

   private MimArtifactTokens() {
      // Constants
   }

}