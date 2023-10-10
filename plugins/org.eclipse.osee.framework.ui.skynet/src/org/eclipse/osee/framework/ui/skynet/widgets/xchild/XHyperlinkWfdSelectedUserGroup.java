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
package org.eclipse.osee.framework.ui.skynet.widgets.xchild;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkWfdSelectedUserGroup extends AbstractXHyperlinkWfdSelectedChild {

   public XHyperlinkWfdSelectedUserGroup(String label, ArtifactToken parentArt) {
      super(label, parentArt);
   }

   @Override
   protected boolean isSelectable(Artifact art) {
      return art.isOfType(CoreArtifactTypes.UserGroup);
   }

}
