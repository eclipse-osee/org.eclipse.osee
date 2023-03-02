/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import java.util.ArrayList;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Audrey E Denk
 */
public class InterfaceLogicalTypeCharacter extends InterfaceLogicalTypeGeneric {
   public static String name = "character";

   public InterfaceLogicalTypeCharacter() {
      super(2L, name);
      ArrayList<InterfaceLogicalTypeField> fields = new ArrayList<InterfaceLogicalTypeField>();
      fields.add(new InterfaceLogicalTypeField("Name", "Name", true, true, "Name", CoreAttributeTypes.Name));
      fields.add(new InterfaceLogicalTypeField("Bit Size", "InterfacePlatformTypeBitSize", true, true, "8",
         CoreAttributeTypes.InterfacePlatformTypeBitSize));
      fields.add(new InterfaceLogicalTypeField("2s Complement", "InterfacePlatformType2sComplement", true, false,
         "true", CoreAttributeTypes.InterfacePlatformType2sComplement));
      fields.add(
         new InterfaceLogicalTypeField("Description", "Description", false, true, CoreAttributeTypes.Description));
      fields.add(new InterfaceLogicalTypeField("Minval", "InterfacePlatformTypeMinval", false, true,
         CoreAttributeTypes.InterfacePlatformTypeMinval));
      fields.add(new InterfaceLogicalTypeField("Maxval", "InterfacePlatformTypeMaxval", false, true,
         CoreAttributeTypes.InterfacePlatformTypeMaxval));
      fields.add(new InterfaceLogicalTypeField("Default Value", "InterfaceDefaultValue", false, true,
         CoreAttributeTypes.InterfaceDefaultValue));

      this.setFields(fields);
   }

}