/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.importer.coverage;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Angel Avila
 */
public class CoverageUtil {

   private CoverageUtil() {

   }
   // @formatter:off
   public static final IArtifactType CoveragePackage = TokenFactory.createArtifactType(0x000000000000004B, "Coverage Package");
   public static final IArtifactType CoverageUnit = TokenFactory.createArtifactType(0x000000000000004E, "Coverage Unit");
   public static final IArtifactType CoverageFolder = TokenFactory.createArtifactType(0x000000000000004D, "Coverage Folder");

   // Attributes
   public static final IAttributeType Item = TokenFactory.createAttributeType(1152921504606847236L, "coverage.Coverage Item");

   //@formatter:on

}
