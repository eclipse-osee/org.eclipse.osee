/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Donald G. Dunne
 */
public final class CoverageArtifactTypes {

   // @formatter:off
   public static final IArtifactType CoveragePackage = TokenFactory.createArtifactType(0x000000000000004B, "Coverage Package");
   public static final IArtifactType CoverageUnit = TokenFactory.createArtifactType(0x000000000000004E, "Coverage Unit");
   public static final IArtifactType CoverageFolder = TokenFactory.createArtifactType(0x000000000000004D, "Coverage Folder");
   // @formatter:on

   private CoverageArtifactTypes() {
      // Constants
   }
}
