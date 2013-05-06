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
package org.eclipse.osee.display.presenter;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Filter;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author John R. Misinco
 */
public class ArtifactFilter implements Filter<ArtifactReadable> {

   private static final IArtifactType[] notAllowedTypes = {CoreArtifactTypes.TestRun};

   private static final IArtifactType[] allowedTypes = {
      CoreArtifactTypes.SoftwareRequirement,
      CoreArtifactTypes.SubsystemRequirementMSWord,
      CoreArtifactTypes.SystemRequirementMSWord,
      CoreArtifactTypes.IndirectSoftwareRequirement,
      CoreArtifactTypes.TestUnit};

   private boolean allTypesAllowed = false;

   private static final List<String> allowed = Arrays.asList("System Requirements", "Subsystem Requirements",
      "Software Requirements");

   private final ArtifactProvider provider;

   public ArtifactFilter(ArtifactProvider provider) {
      this.provider = provider;
   }

   public void setAllTypesAllowed(boolean allTypesAllowed) {
      this.allTypesAllowed = allTypesAllowed;
   }

   @Override
   public boolean accept(ArtifactReadable item) throws Exception {
      boolean isAllowed = false;
      if (allTypesAllowed) {
         isAllowed = true;
      } else if (item != null) {
         if (item.isOfType(allowedTypes) || item.getBranch().equals(CoreBranches.COMMON)) {
            isAllowed = true;
         } else if (!item.isOfType(notAllowedTypes)) {
            ArtifactReadable current = item;
            while (current != null) {
               if (allowed.contains(current.getName())) {
                  isAllowed = true;
                  break;
               }
               current = provider.getParent(current);
            }
         }
      }
      return isAllowed;
   }
}
