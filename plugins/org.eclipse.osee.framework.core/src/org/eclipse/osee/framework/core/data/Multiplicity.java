/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Ryan D. Brooks
 */
public class Multiplicity extends BaseId {
   public static final Multiplicity SENTINEL = Multiplicity.valueOf("SENTINEL", Id.SENTINEL);
   public static final Multiplicity ANY = Multiplicity.valueOf("ANY", 1);
   public static final Multiplicity EXACTLY_ONE = Multiplicity.valueOf("EXACTLY_ONE", 2);
   public static final Multiplicity ZERO_OR_ONE = Multiplicity.valueOf("ZERO_OR_ONE", 3);
   public static final Multiplicity AT_LEAST_ONE = Multiplicity.valueOf("AT_LEAST_ONE", 4);
   private final String name;
   private MultiplicityToken token;

   public Multiplicity(String name, Long id) {
      super(id);
      this.name = name;
   }

   public static Multiplicity valueOf(String name, long id) {
      return new Multiplicity(name, id);
   }

   public String getName() {
      return name;
   }

   public MultiplicityToken getToken() {
      if (token == null) {
         token = new MultiplicityToken(name, id);
      }
      return token;
   }

   public static class MultiplicityToken extends NamedIdBase {

      public MultiplicityToken() {
         // for jax-rs
      }

      public MultiplicityToken(String name, Long id) {
         super(id, name);
      }

      @JsonIgnore
      @Override
      public int getIdIntValue() {
         return super.getIdIntValue();
      }

      @JsonIgnore
      @Override
      public String getIdString() {
         return super.getIdString();
      }

   }

}