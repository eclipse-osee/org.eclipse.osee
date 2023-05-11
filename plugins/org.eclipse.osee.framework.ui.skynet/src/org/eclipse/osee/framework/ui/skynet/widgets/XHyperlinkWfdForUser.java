/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForUser extends XHyperlinkWithFilteredDialog<User> {

   ISelectableValueProvider valueProvider;

   public XHyperlinkWfdForUser() {
      super("User");
   }

   @Override
   public Collection<User> getSelectable() {
      List<User> usersAll = new ArrayList<>();
      for (Artifact art : ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.User, CoreBranches.COMMON)) {
         if (art.getSoleAttributeValue(CoreAttributeTypes.Active, false)) {
            usersAll.add((User) art);
         }
      }
      return usersAll;
   }

}
