/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.data.enums.token;

import org.eclipse.osee.ats.api.data.enums.token.ColorTeamAttributeType.ColorTeamEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;

/**
 * @author Stephen J. Molaro
 */
public class ColorTeamAttributeType extends AttributeTypeEnum<ColorTeamEnum> {

   // @formatter:off
	public final ColorTeamEnum BloodRedTeam = new ColorTeamEnum(0, "Blood Red Team");
	public final ColorTeamEnum BlueCrewTeam = new ColorTeamEnum(1, "Blue Crew Team");
	public final ColorTeamEnum MeanGreenTeam = new ColorTeamEnum(2, "Mean Green Team");
	public final ColorTeamEnum PurpleTeam = new ColorTeamEnum(3, "Purple Team");
	public final ColorTeamEnum BurntOrangeTeam = new ColorTeamEnum(4, "Burnt Orange Team");
	public final ColorTeamEnum BronzeTeam = new ColorTeamEnum(5, "Bronze Team");
	public final ColorTeamEnum SilverTeam = new ColorTeamEnum(6, "Silver Team");
	public final ColorTeamEnum PirateBlackTeam = new ColorTeamEnum(7, "Pirate Black Team");
	public final ColorTeamEnum GoldTeam = new ColorTeamEnum(8, "Gold Team");
	public final ColorTeamEnum PlaidTeam = new ColorTeamEnum(9, "Plaid Team");
	public final ColorTeamEnum Unspecified = new ColorTeamEnum(10, "Unspecified");
	// @formatter:on

   public ColorTeamAttributeType(TaggerTypeToken taggerType, String mediaType, NamespaceToken namespace) {
      super(1364016837443371647L, namespace, "ats.Color Team", mediaType, "", taggerType);
   }

   public class ColorTeamEnum extends EnumToken {
      public ColorTeamEnum(int ordinal, String name) {
         super(ordinal, name);
      }
   }
}
