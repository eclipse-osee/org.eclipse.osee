/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.api.workdef.model;

import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXSignByAndDateArtWidget;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.widget.XOption;

/**
 * @author Donald G. Dunne
 */
public class SignDateWidgetDef extends AttrWidgetDef {

   public SignDateWidgetDef(String name, AttributeTypeToken signbyAttrType, AttributeTypeToken signbyDateAttrType) {
      super(name, signbyAttrType, XXSignByAndDateArtWidget);
      setAttributeType2(signbyDateAttrType);
   }

   public SignDateWidgetDef andRequiredByTeamLead() {
      set(XOption.LRFT); // Adds RFT
      return this;
   }

   @Override
   public SignDateWidgetDef andUserGroup(IUserGroupArtifactToken userGroup) {
      super.andUserGroup(userGroup);
      return this;
   }

}
