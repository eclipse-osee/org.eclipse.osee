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
package org.eclipse.osee.framework.ui.skynet.artifact.snapshot;

import java.io.UnsupportedEncodingException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
class KeyGenerator {

   /**
    * Creates namespace/key pair object from an artifact and branch;
    * 
    * @param artifact source
    * @param branch source
    * @return key pair
    * @throws UnsupportedEncodingException
    */
   public Pair<String, String> getKeyPair(Artifact artifact, Branch branch) throws UnsupportedEncodingException {
      String namespace = getNamespace(artifact, branch);
      String key = Integer.toString(artifact.getGammaId());
      return new Pair<String, String>(namespace, key);
   }

   /**
    * Creates namespace/key pair object from an artifact
    * 
    * @param artifact source
    * @return key pair
    * @throws UnsupportedEncodingException
    */
   public Pair<String, String> getKeyPair(Artifact artifact) throws UnsupportedEncodingException {
      return getKeyPair(artifact, artifact.getBranch());
   }

   /**
    * Generates a namespace key for an artifact
    * 
    * @param artifact artifact to use when generating the namespace
    * @param branch branch to use when generating the namespace
    * @return namespace
    * @throws UnsupportedEncodingException
    */
   private String getNamespace(Artifact artifact, Branch branch) throws UnsupportedEncodingException {
      StringBuffer namespace = new StringBuffer();
      namespace.append(artifact.getGuid());
      namespace.append("BRANCH");
      namespace.append(branch.getBranchId());
      return namespace.toString();
   }

   /**
    * Convert key pair into a local cache key
    * 
    * @param key Pair containing namespace and key information
    * @return local cache key
    */
   public String toLocalCacheKey(Pair<String, String> key) {
      return key.getKey() + "&" + key.getValue();
   }

   /**
    * Convert key pair into a local cache key
    * 
    * @param namespace to use
    * @param key to use
    * @return local cache key
    */
   public String toLocalCacheKey(String namespace, String key) {
      return namespace + "&" + key;
   }
}
