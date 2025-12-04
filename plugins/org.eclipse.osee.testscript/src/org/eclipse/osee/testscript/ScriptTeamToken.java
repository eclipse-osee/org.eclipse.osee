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

package org.eclipse.osee.testscript;

import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.framework.core.data.AttributePojo;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan T. Baldwin
 */
public class ScriptTeamToken extends ArtifactAccessorResultWithGammas {

   public static final ScriptTeamToken SENTINEL = new ScriptTeamToken();

   public ScriptTeamToken() {
      super();
   }

   public ScriptTeamToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public ScriptTeamToken(ArtifactReadable art) {
      super(art);
   }

   public ScriptTeamToken(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
   }

}