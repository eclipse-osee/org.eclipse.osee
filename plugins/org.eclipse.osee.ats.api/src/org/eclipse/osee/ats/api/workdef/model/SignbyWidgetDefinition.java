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
import org.eclipse.osee.framework.core.enums.OseeImage;

/**
 * @author Donald G. Dunne
 */
public class SignbyWidgetDefinition extends WidgetDefinition {

   public SignbyWidgetDefinition(String name, AttributeTypeToken signbyAttrType, AttributeTypeToken signbyDateAttrType) {
      super(name, signbyAttrType, "XSignbyWidget");
      setAttributeType2(signbyDateAttrType);
   }

   public SignbyWidgetDefinition andRequired() {
      set(WidgetOption.REQUIRED_FOR_TRANSITION);
      return this;
   }

   public SignbyWidgetDefinition andImage(OseeImage oseeImage) {
      setOseeImage(oseeImage);
      return this;
   }

}
