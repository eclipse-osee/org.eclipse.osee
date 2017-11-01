/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

/**
 * Simple named id for serialization until ArtifactToken serializes with Name
 *
 * @author Donald G. Dunne
 */
public class JaxNamedId {

   private String name;
   private String id;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public static JaxNamedId construct(Long id, String name) {
      JaxNamedId token = new JaxNamedId();
      token.setName(name);
      token.setId(id.toString());
      return token;
   }
}
