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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.accessor.types.AttributePojo;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Stephen J. Molaro
 */
public class ScriptSetToken extends ArtifactAccessorResultWithGammas {

   public static final ScriptSetToken SENTINEL = new ScriptSetToken();

   private AttributePojo<Boolean> active =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Active, GammaId.SENTINEL, false, "");;

   public ScriptSetToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptSetToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Name, "")));
      this.setActive(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Active, false)));
   }

   public ScriptSetToken(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
      this.setActive(false);
   }

   public ScriptSetToken() {
      super();
   }

   /**
    * @return the active
    */
   public AttributePojo<Boolean> getActive() {
      return active;
   }

   /**
    * @param active the active to set
    */
   public void setActive(boolean active) {
      this.active = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Active, GammaId.SENTINEL, active, "");
   }

   @JsonProperty
   public void setActive(AttributePojo<Boolean> active) {
      this.active = active;
   }
}