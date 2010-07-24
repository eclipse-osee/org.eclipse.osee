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
public class CoverageArtifactTypes extends NamedIdentity implements IArtifactType {
   public static final CoverageArtifactTypes CoveragePackage = new CoverageArtifactTypes("AARFcyA9zyD3uEK8akgA",
      "Coverage Package");
   public static final CoverageArtifactTypes CoverageUnit = new CoverageArtifactTypes("ALZS3MQdCCIUvEYlZeAA",
      "Coverage Unit");
   public static final CoverageArtifactTypes CoverageFolder = new CoverageArtifactTypes("ALZR_AbpJTTf6QQn2iAA",
      "Coverage Folder");

   private CoverageArtifactTypes(String guid, String name) {
      super(guid, name);
   }
}
