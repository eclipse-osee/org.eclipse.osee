/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.revision;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * Caches names and descriptors to an artifact id. This cache assumes that the user is already taking care of how this
 * data is appropriate for versions and branches.
 * 
 * @author Robert A. Fisher
 */
public class ArtifactNameDescriptorCache extends ArtifactNameDescriptorResolver {
   private Map<Integer, String> nameMap;
   private Map<Integer, ArtifactSubtypeDescriptor> descriptorMap;
   private boolean backupAvailable;

   public ArtifactNameDescriptorCache() {
      this(null);
   }

   public ArtifactNameDescriptorCache(Branch branch) {
      super(branch);

      this.nameMap = new HashMap<Integer, String>();
      this.descriptorMap = new HashMap<Integer, ArtifactSubtypeDescriptor>();
      //      this.backupAvailable = branch != null;
      this.backupAvailable = false;
   }

   public void cache(Integer artId, String name, ArtifactSubtypeDescriptor descriptor) {
      if (artId == null) throw new IllegalArgumentException("artId must not be null");

      nameMap.put(artId, name);
      descriptorMap.put(artId, descriptor);
   }

   @Override
   public Pair<String, ArtifactSubtypeDescriptor> get(Integer artId) {
      if (artId == null) throw new IllegalArgumentException("artId must not be null");

      if (nameMap.containsKey(artId) || !backupAvailable) {
         return new Pair<String, ArtifactSubtypeDescriptor>(nameMap.get(artId), descriptorMap.get(artId));
      } else {
         return super.get(artId);
      }
   }
}
