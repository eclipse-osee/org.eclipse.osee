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
package org.eclipse.osee.disposition.model;

/**
 * @author Donald G. Dunne
 */
public class DispoProgramImpl implements DispoProgram {

   private String name;
   private String guid;
   private Long uuid;

   public DispoProgramImpl(String name, String guid, Long uuid) {
      this.name = name;
      this.guid = guid;
      this.uuid = uuid;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   @Override
   public Long getUuid() {
      return uuid;
   }

   public void setUuid(Long uuid) {
      this.uuid = uuid;
   }
}
