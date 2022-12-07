/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim.types;

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Luciano T. Vaglienti
 */
public class MimUserGlobalPreferences extends PLGenericDBObject {
   public static final MimUserGlobalPreferences SENTINEL = new MimUserGlobalPreferences();
   private boolean wordWrap = false;

   public MimUserGlobalPreferences(ArtifactReadable artifact) {
      super(artifact);
      if (artifact.isValid()) {
         this.setWordWrap(artifact.getSoleAttributeValue(CoreAttributeTypes.MimSettingWordWrap, false));
      }
   }

   public MimUserGlobalPreferences() {
      super();
   }

   public boolean isWordWrap() {
      return wordWrap;
   }

   public void setWordWrap(boolean wordWrap) {
      this.wordWrap = wordWrap;
   }

}
