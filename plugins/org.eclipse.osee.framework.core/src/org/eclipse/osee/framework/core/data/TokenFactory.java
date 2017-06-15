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
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;

public final class TokenFactory {

   public static final Pattern nameIdPattern = Pattern.compile("\\[(.*)\\]-\\[(.*)\\]");

   private TokenFactory() {
      // Utility Class
   }

   public static IArtifactType createArtifactType(long guid, String name) {
      return new ArtifactTypeToken(guid, name);
   }

   /**
    * @param token as [name]-[uuid]
    */
   public static IArtifactType createArtifactTypeFromToken(String token) {
      Matcher matcher = nameIdPattern.matcher(token);
      if (matcher.find()) {
         Long uuid = Long.valueOf(matcher.group(2));
         String name = matcher.group(1);
         return new ArtifactTypeToken(uuid, name);
      }
      return null;
   }

   public static RelationTypeToken createRelationType(long id, String name) {
      return RelationTypeToken.create(id, name);
   }

   public static IAccessContextId createAccessContextId(String guid, String name) {
      return new AccessContextIdToken(guid, name);
   }

   public static ArtifactToken createArtifactToken(long id, String guid, String name, IArtifactType artifactType) {
      return ArtifactToken.valueOf(id, guid, name, CoreBranches.COMMON, artifactType);
   }

   private final static class ArtifactTypeToken extends NamedIdBase implements IArtifactType {
      public ArtifactTypeToken(Long id, String name) {
         super(id, name);
      }

      @Override
      public Long getGuid() {
         return getId();
      }
   }

   private final static class AccessContextIdToken extends NamedIdentity<String> implements IAccessContextId {
      public AccessContextIdToken(String guid, String name) {
         super(guid, name);
      }

      @Override
      public String toString() {
         return String.format("%s - %s", getName(), getGuid());
      }
   }

   public static String createArtifactTypeTokenString(IArtifactType artifactType) {
      return String.format("[%s]-[%d]", artifactType.getName(), artifactType.getGuid());
   }

}
