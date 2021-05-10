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
public class InterfaceLogicalTypeUnsignedLong extends InterfaceLogicalTypeGeneric {
   public static String name = "unsigned long";

   public InterfaceLogicalTypeUnsignedLong() {
      super(14L, name);
      ArrayList<InterfaceLogicalTypeField> fields = new ArrayList<InterfaceLogicalTypeField>();
      fields.add(new InterfaceLogicalTypeField(CoreAttributeTypes.Name, true));
      fields.add(new InterfaceLogicalTypeField(CoreAttributeTypes.InterfacePlatformTypeByteSize, true));
      fields.add(new InterfaceLogicalTypeField(CoreAttributeTypes.Description, false));
      fields.add(new InterfaceLogicalTypeField(CoreAttributeTypes.InterfacePlatformTypeMinval, true));
      fields.add(new InterfaceLogicalTypeField(CoreAttributeTypes.InterfacePlatformTypeMaxval, true));
      fields.add(new InterfaceLogicalTypeField(CoreAttributeTypes.InterfacePlatformTypeUnits, false));
      fields.add(new InterfaceLogicalTypeField(CoreAttributeTypes.InterfacePlatformTypeDefaultValue, false));
      this.setFields(fields);
   }

}