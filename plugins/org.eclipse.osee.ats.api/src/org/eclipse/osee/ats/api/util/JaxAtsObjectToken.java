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
package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsObjectToken implements Named {

   private String name;
   private ArtifactId id;

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public ArtifactId getId() {
      return id;
   }

   public void setId(ArtifactId id) {
      this.id = id;
   }

   public static JaxAtsObjectToken construct(ArtifactId id, String name) {
      JaxAtsObjectToken token = new JaxAtsObjectToken();
      token.setId(id);
      token.setName(name);
      return token;
   }

   public static JaxAtsObjectToken construct(ArtifactToken storeObject) {
      return construct(storeObject, storeObject.getName());
   }
}
