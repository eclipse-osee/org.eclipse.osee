/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Stephen J. Molaro
 */
public class ScriptSetToken extends ArtifactAccessorResult {

   public static final ScriptSetToken SENTINEL = new ScriptSetToken();

   private boolean active;

   public ScriptSetToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptSetToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setActive(art.getSoleAttributeValue(CoreAttributeTypes.Active, false));
   }

   public ScriptSetToken(Long id, String name) {
      super(id, name);
      this.setActive(false);
   }

   public ScriptSetToken() {
      super();
   }

   /**
    * @return the active
    */
   public boolean getActive() {
      return active;
   }

   /**
    * @param active the active to set
    */
   public void setActive(boolean active) {
      this.active = active;
   }
}