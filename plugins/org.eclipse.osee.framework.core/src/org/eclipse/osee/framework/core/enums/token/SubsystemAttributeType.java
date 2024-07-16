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

   public final SubsystemEnum Robot_API = new SubsystemEnum(0, "Robot API");
   public final SubsystemEnum Robot_Survivability_Equipment = new SubsystemEnum(1, "Robot Survivability Equipment");
   public final SubsystemEnum Robot_Systems_Management = new SubsystemEnum(2, "Robot Systems Management");
   public final SubsystemEnum Chassis = new SubsystemEnum(3, "Chassis");
   public final SubsystemEnum Communications = new SubsystemEnum(4, "Communications");
   public final SubsystemEnum Data_Management = new SubsystemEnum(5, "Data Management");
   public final SubsystemEnum Electrical = new SubsystemEnum(6, "Electrical");
   public final SubsystemEnum Controls = new SubsystemEnum(7, "Controls");
   public final SubsystemEnum Hydraulics = new SubsystemEnum(8, "Hydraulics");
   public final SubsystemEnum Lighting = new SubsystemEnum(9, "Lighting");
   public final SubsystemEnum Navigation = new SubsystemEnum(10, "Navigation");
   public final SubsystemEnum Propulsion = new SubsystemEnum(11, "Propulsion");
   public final SubsystemEnum Unknown = new SubsystemEnum(12, "Unknown");
   public final SubsystemEnum Unspecified = new SubsystemEnum(13, "Unspecified");

   public SubsystemAttributeType(NamespaceToken namespace, int enumCount) {
      super(1152921504606847112L, namespace, "Subsystem", MediaType.TEXT_PLAIN, "", TaggerTypeToken.PlainTextTagger,
         enumCount);
   }

   public SubsystemAttributeType() {
      this(NamespaceToken.OSEE, 14);
   }

   public class SubsystemEnum extends EnumToken {
      public SubsystemEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}