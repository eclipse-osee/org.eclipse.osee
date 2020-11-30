/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ValidateOseeTypes extends XNavigateItemAction {

   public ValidateOseeTypes(XNavigateItem parent) {
      super(parent, "Validate OSEE Types", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData rd = new XResultData();

      List<AttributeTypeGeneric<?>> attrTypes = new ArrayList<>();
      attrTypes.addAll(AtsApiService.get().tokenService().getAttributeTypes());

      for (ArtifactTypeToken artType : AtsApiService.get().tokenService().getArtifactTypes()) {
         for (AttributeTypeToken attrType : artType.getValidAttributeTypes()) {
            attrTypes.remove(attrType);
         }
      }

      rd.log(getName() + "\n");
      rd.log("Attribute Types without Artifact reference: " + "\n");
      for (AttributeTypeToken attrType : attrTypes) {
         rd.log(attrType.toStringWithId());
      }

      XResultDataUI.report(rd, getName());
   }
}
