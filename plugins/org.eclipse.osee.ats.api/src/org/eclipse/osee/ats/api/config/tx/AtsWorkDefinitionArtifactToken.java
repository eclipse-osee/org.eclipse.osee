/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config.tx;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken.ArtifactTokenImpl;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionArtifactToken extends ArtifactTokenImpl implements IAtsWorkDefinitionArtifactToken {

   private final String filename;

   public AtsWorkDefinitionArtifactToken(Long id, String name) {
      this(id, name, null);
   }

   public AtsWorkDefinitionArtifactToken(Long id, String name, String filename) {
      super(id, GUID.create(), name, CoreBranches.COMMON, AtsArtifactTypes.WorkDefinition);
      this.filename = filename;
   }

   public static IAtsWorkDefinitionArtifactToken valueOf(Long id, String name) {
      return valueOf(id, name, null);
   }

   public static IAtsWorkDefinitionArtifactToken valueOf(Long id, String name, String filename) {
      return new AtsWorkDefinitionArtifactToken(id, name, filename);
   }

   @Override
   public String getFilename() {
      return filename;
   }

}
