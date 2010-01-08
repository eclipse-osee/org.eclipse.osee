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

/**
 * @author Don Dunne
 */
public enum CoverageArtifactTypes implements IArtifactType {
   CoveragePackage("Coverage Package", "AARFcyA9zyD3uEK8akgA"),
   CoverageUnit("Coverage Unit", "ALZS3MQdCCIUvEYlZeAA"),
   CoverageFolder("Coverage Folder", "ALZR_AbpJTTf6QQn2iAA");

   private final String name;
   private final String guid;

   private CoverageArtifactTypes(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }

}
