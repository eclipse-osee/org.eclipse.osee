/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.message.test.mocks;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

public final class MockRequestFactory {

   private MockRequestFactory() {
      // Utility class
   }

   public static ChangeItem createArtifactChangeItem() throws OseeArgumentException {
      int artId = (int) Math.random();
      Long gammaIdNumber = Long.valueOf((int) Math.random());
      int artTypeId = artId * 10;
      ChangeItem changeItem =
         ChangeItemUtil.newArtifactChange(artId, artTypeId, gammaIdNumber, ModificationType.getMod(1));
      populateChangeVersion(changeItem.getDestinationVersion(), 22);
      populateChangeVersion(changeItem.getCurrentVersion(), 15);
      return changeItem;
   }

   public static ChangeVersion createChangeVersion(int index) {
      ModificationType modType = ModificationType.values()[index % ModificationType.values().length];
      return new ChangeVersion("change_version_value_" + index, (long) (index * Integer.MAX_VALUE), modType);
   }

   public static void populateChangeVersion(ChangeVersion changeVersion, int index) {
      ModificationType modType = ModificationType.values()[index % ModificationType.values().length];
      changeVersion.setGammaId((long) (index * Integer.MAX_VALUE));
      changeVersion.setModType(modType);
      changeVersion.setValue("change_version_value_" + index);
   }

}
