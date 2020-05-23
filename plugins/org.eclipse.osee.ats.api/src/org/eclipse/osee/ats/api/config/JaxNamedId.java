/*********************************************************************
 * Copyright (c) 2017 Boeing
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
