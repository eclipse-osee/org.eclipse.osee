/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.SubsystemAttributeType.SubsystemEnum;

/**
 * @author Stephen J. Molaro
 */
public class SubsystemAttributeType extends AttributeTypeEnum<SubsystemEnum> {

   public final SubsystemEnum Robot_API = new SubsystemEnum(0, "Robot API", NamespaceToken.OSEE);
   public final SubsystemEnum Robot_Survivability_Equipment =
      new SubsystemEnum(1, "Robot Survivability Equipment", NamespaceToken.OSEE);
   public final SubsystemEnum Robot_Systems_Management =
      new SubsystemEnum(2, "Robot Systems Management", NamespaceToken.OSEE);
   public final SubsystemEnum Chassis = new SubsystemEnum(3, "Chassis", NamespaceToken.OSEE);
   public final SubsystemEnum Communications = new SubsystemEnum(4, "Communications", NamespaceToken.OSEE);
   public final SubsystemEnum Data_Management = new SubsystemEnum(5, "Data Management", NamespaceToken.OSEE);
   public final SubsystemEnum Electrical = new SubsystemEnum(6, "Electrical", NamespaceToken.OSEE);
   public final SubsystemEnum Controls = new SubsystemEnum(7, "Controls", NamespaceToken.OSEE);
   public final SubsystemEnum Hydraulics = new SubsystemEnum(8, "Hydraulics", NamespaceToken.OSEE);
   public final SubsystemEnum Lighting = new SubsystemEnum(9, "Lighting", NamespaceToken.OSEE);
   public final SubsystemEnum Navigation = new SubsystemEnum(10, "Navigation", NamespaceToken.OSEE);
   public final SubsystemEnum Propulsion = new SubsystemEnum(11, "Propulsion", NamespaceToken.OSEE);
   public final SubsystemEnum Unknown = new SubsystemEnum(12, "Unknown", NamespaceToken.OSEE);
   public final SubsystemEnum Unspecified = new SubsystemEnum(13, "Unspecified", NamespaceToken.OSEE);

   public SubsystemAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847112L, namespace, "Subsystem", MediaType.TEXT_PLAIN, "", TaggerTypeToken.PlainTextTagger,
         enumCount);
   }

   public SubsystemAttributeType() {
      this(NamespaceToken.OSEE, 14);
   }

   public class SubsystemEnum extends EnumToken {
      public SubsystemEnum(int ordinal, String name, NamespaceToken... namespaces) {
         super(ordinal, name, namespaces);
         addEnum(this);
      }
   }
}