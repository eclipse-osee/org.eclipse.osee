/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;

public final class CoreArtifactTokens {

   // @formatter:off
   public static IArtifactToken DefaultHierarchyRoot = TokenFactory.createArtifactToken(197818, "AEslkN+d4hWXjQvnZ1gA", "Default Hierarchy Root", CoreArtifactTypes.RootArtifact);
   public static IArtifactToken UniversalGroupRoot = TokenFactory.createArtifactToken(60807, "AExdLMeOTGhhPY4CyvQA", "Root Artifact", CoreArtifactTypes.UniversalGroup);
   public static IArtifactToken Everyone = TokenFactory.createArtifactToken(48656, "AAABEbn4DKoAaR82FZsL3A", "Everyone", CoreArtifactTypes.UserGroup);
   public static IArtifactToken OseeAdmin = TokenFactory.createArtifactToken(52247, "AAABHaItmnUAG6ZAYlFKag", "OseeAdmin", CoreArtifactTypes.UserGroup);
   public static IArtifactToken UserGroups = TokenFactory.createArtifactToken(80920, "AAABGTAGNY8BauB5GajiIQ", "User Groups", CoreArtifactTypes.Folder);
   // @formatter:on

   private CoreArtifactTokens() {
      // Constants
   }
}
