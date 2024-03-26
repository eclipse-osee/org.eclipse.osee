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

import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.jdk.core.util.WidgetHint;

/**
 * @author Donald G. Dunne
 */
public class SignByAndDateWidgetDefinition extends WidgetDefinition {

   public SignByAndDateWidgetDefinition(String name, AttributeTypeToken signbyAttrType, AttributeTypeToken signbyDateAttrType) {
      super(name, signbyAttrType, "XSignByAndDateWidget");
      setAttributeType2(signbyDateAttrType);
   }

   public SignByAndDateWidgetDefinition andRequired() {
      set(WidgetOption.REQUIRED_FOR_TRANSITION);
      return this;
   }

   public SignByAndDateWidgetDefinition andImage(OseeImage oseeImage) {
      setOseeImage(oseeImage);
      return this;
   }

   public LayoutItem andRequiredByTeamLead() {
      andRequired();
      getWidgetHints().add(WidgetHint.LeadRequired);
      return this;
   }

   public LayoutItem andRequiredByUserGroup(IUserGroupArtifactToken userGroup) {
      andRequired();
      setUserGroup(userGroup);
      return this;
   }

}
