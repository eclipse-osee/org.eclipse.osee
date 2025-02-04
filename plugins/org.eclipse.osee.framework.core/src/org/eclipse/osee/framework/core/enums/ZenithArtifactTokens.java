/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.ScriptConfiguration;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Ryan T. Baldwin
 */
public final class ZenithArtifactTokens {

   // @formatter:off
   public static final ArtifactToken ZenithConfigFolder  = ArtifactToken.valueOf(805625192, "Zenith", Folder);

   public static final ArtifactToken ZenithConfiguration = ArtifactToken.valueOf(356555247, "Zenith Configuration", ScriptConfiguration);
   // @formatter:on

   private ZenithArtifactTokens() {
      // Constants
   }

}