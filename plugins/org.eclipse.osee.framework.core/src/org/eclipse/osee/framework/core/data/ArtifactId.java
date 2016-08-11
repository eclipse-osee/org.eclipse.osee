/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.codehaus.jackson.annotate.JsonCreator;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;

/**
 * @author Megumi Telles
 */
public interface ArtifactId extends Identifiable<String>, Id {

   public static final ArtifactId SENTINEL = ArtifactId.valueOf(Id.SENTINEL);

   public Long getUuid();

   public static ArtifactId valueOf(String id) {

      return valueOf(Long.valueOf(id));
   }

   @JsonCreator
   public static ArtifactId valueOf(long id) {
      final class ArtifactIdImpl extends BaseId implements ArtifactId {
         public ArtifactIdImpl(Long artId) {
            super(artId);
         }

         @Override
         public String getGuid() {
            return null;
         }

         @Override
         public Long getUuid() {
            return getId();
         }

         @Override
         public String getName() {
            return null;
         }
      }
      return new ArtifactIdImpl(id);
   }

   default String toStringWithId() {
      return String.format("[%s][%s]", getName(), getUuid());
   }

   @Override
   default Long getId() {
      return getUuid();
   }
}