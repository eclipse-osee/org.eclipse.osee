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
package org.eclipse.osee.framework.core.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan D. Brooks
 */
public final class TokenFactory {

   public static final Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");

   private TokenFactory() {
      // Utility Class
   }

   public static ArtifactTypeToken createArtifactType(long id, String name) {
      return ArtifactTypeToken.valueOf(id, name);
   }

   /**
    * @param token as [name]-[uuid]
    */
   public static ArtifactTypeToken createArtifactTypeFromToken(String token) {
      Matcher matcher = nameIdPattern.matcher(token);
      if (matcher.find()) {
         long id = Long.valueOf(matcher.group(2));
         String name = matcher.group(1);
         return ArtifactTypeToken.valueOf(id, name);
      }
      return null;
   }

   public static RelationTypeToken createRelationType(long id, String name) {
      return RelationTypeToken.create(id, name);
   }

   public static ArtifactToken createArtifactToken(long id, String guid, String name, BranchId branch, ArtifactTypeToken artifactType) {
      return ArtifactToken.valueOf(id, guid, name, branch, artifactType);
   }
}