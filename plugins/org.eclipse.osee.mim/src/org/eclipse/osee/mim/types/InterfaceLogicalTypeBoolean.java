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
public class InterfaceLogicalTypeBoolean extends InterfaceLogicalTypeGeneric {
   public static String name = "boolean";

   public InterfaceLogicalTypeBoolean() {
      super(1L, name);
      ArrayList<InterfaceLogicalTypeField> fields = new ArrayList<InterfaceLogicalTypeField>();
      fields.add(new InterfaceLogicalTypeField("Name", "Name", true, true, "Name", CoreAttributeTypes.Name, "name"));
      fields.add(new InterfaceLogicalTypeField("Bit Size", "InterfacePlatformTypeBitSize", true, true, "8",
         CoreAttributeTypes.InterfacePlatformTypeBitSize, "interfacePlatformTypeBitSize"));
      fields.add(new InterfaceLogicalTypeField("2s Complement", "InterfacePlatformType2sComplement", true, false,
         "true", CoreAttributeTypes.InterfacePlatformType2sComplement, "interfacePlatformType2sComplement"));
      fields.add(new InterfaceLogicalTypeField("Description", "Description", false, true,
         CoreAttributeTypes.Description, "description"));
      fields.add(new InterfaceLogicalTypeField("Minval", "InterfacePlatformTypeMinval", false, true,
         CoreAttributeTypes.InterfacePlatformTypeMinval, "interfacePlatformTypeMinval"));
      fields.add(new InterfaceLogicalTypeField("Maxval", "InterfacePlatformTypeMaxval", false, true,
         CoreAttributeTypes.InterfacePlatformTypeMaxval, "interfacePlatformTypeMaxval"));
      fields.add(new InterfaceLogicalTypeField("Default Value", "InterfaceDefaultValue", false, true,
         CoreAttributeTypes.InterfaceDefaultValue, "interfaceDefaultValue"));
      this.setFields(fields);
   }

}