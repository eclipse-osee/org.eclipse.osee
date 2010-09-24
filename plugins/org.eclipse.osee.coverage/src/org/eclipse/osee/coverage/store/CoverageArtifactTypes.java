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
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Don Dunne
 */
public final class CoverageArtifactTypes extends NamedIdentity implements IArtifactType {

   // @formatter:off
   public static final IArtifactType CoveragePackage = new CoverageArtifactTypes("AARFcyA9zyD3uEK8akgA", "Coverage Package");
   public static final IArtifactType CoverageUnit = new CoverageArtifactTypes("ALZS3MQdCCIUvEYlZeAA", "Coverage Unit");
   public static final IArtifactType CoverageFolder = new CoverageArtifactTypes("ALZR_AbpJTTf6QQn2iAA", "Coverage Folder");
   // @formatter:on

   private CoverageArtifactTypes(String guid, String name) {
      super(guid, name);
   }
}
