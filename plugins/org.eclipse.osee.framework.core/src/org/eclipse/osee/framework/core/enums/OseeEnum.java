/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.core.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Simple Enum class that provides few things over enum class.</br>
 * </br>
 * 1) It searilizes/desearilizes automatically</br>
 * 2) Provides a default get(String) method that doesn't exception if no match</br>
 * 3) Other parameters can be easily added while named/id is automatic</br>
 *
 * @author Donald G. Dunne
 */
public abstract class OseeEnum extends NamedIdBase {
   protected static final HashCollection<Long, OseeEnum> idToEnums = new HashCollection<Long, OseeEnum>(15);

   /**
    * Add enum with next ordinal
    */
   public OseeEnum(Long typeId, String name) {
      super(-1L, name);
      Long id = Long.valueOf(values().size());
      validateId(typeId, id, name);
      setId(Long.valueOf(values().size()));
      idToEnums.put(typeId, this);
   }

   public OseeEnum(Long typeId, Long ordinal, String name) {
      this(typeId, ordinal, name, true);
   }

   public OseeEnum(Long typeId, Long ordinal, String name, boolean ensureUnique) {
      super(ordinal, name);
      if (ensureUnique) {
         validateId(typeId, ordinal, name);
      }
      idToEnums.put(typeId, this);
   }

   private void validateId(Long typeId, Long id, String name) {
      List<OseeEnum> values = idToEnums.getValues(typeId);
      if (values != null) {
         for (OseeEnum oEnum : values) {
            if (oEnum.getId().equals(id)) {
               String str = String.format("Duplicate Ids %s for Same Enum Type %s - [%s]", id, typeId, name);
               System.err.println(str);
               throw new OseeArgumentException(str);
            }
         }
      }
   }

   abstract public Long getTypeId();

   @JsonIgnore
   abstract public OseeEnum getDefault();

   /**
    * @return OseeEnum matching name or sentinel
    */
   public OseeEnum get(String name) {
      for (OseeEnum oEnum : idToEnums.getValues(getTypeId())) {
         if (oEnum.name().equals(name)) {
            return oEnum;
         }
      }
      return getDefault();
   }

   /**
    * @return OseeEnum matching id or sentinel
    */
   public OseeEnum get(Long id) {
      for (OseeEnum oEnum : idToEnums.getValues(getTypeId())) {
         if (oEnum.getId().equals(id)) {
            return oEnum;
         }
      }
      return getDefault();
   }

   public String name() {
      return getName();
   }

   public Collection<OseeEnum> values() {
      Collection<OseeEnum> values = idToEnums.getValues(getTypeId());
      if (values == null) {
         return Collections.emptyList();
      }
      return values;
   }

}