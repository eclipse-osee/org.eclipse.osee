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

package org.eclipse.osee.framework.core.enums.token;

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.InterfacePlatformTypeUnitsAttribute.InterfacePlatformTypeUnitsEnum;

/**
 * @author Audrey Denk
 */
public class InterfacePlatformTypeUnitsAttribute extends AttributeTypeEnum<InterfacePlatformTypeUnitsEnum> {

   public final InterfacePlatformTypeUnitsEnum Unitless = new InterfacePlatformTypeUnitsEnum(0, "n/a", "Unitless");
   public final InterfacePlatformTypeUnitsEnum Feet =
      new InterfacePlatformTypeUnitsEnum(1, "feet", "distance / altitude");
   public final InterfacePlatformTypeUnitsEnum Degrees =
      new InterfacePlatformTypeUnitsEnum(2, "degrees", "heading / bearing");
   public final InterfacePlatformTypeUnitsEnum DirectionCosine =
      new InterfacePlatformTypeUnitsEnum(3, "direction cosine", "LOS angle");
   public final InterfacePlatformTypeUnitsEnum DirectionCosinePerSecond =
      new InterfacePlatformTypeUnitsEnum(4, "direction cosine/second", "LOS rate");
   public final InterfacePlatformTypeUnitsEnum RadiansPerSec =
      new InterfacePlatformTypeUnitsEnum(5, "radians/sec", "LOS rate");
   public final InterfacePlatformTypeUnitsEnum FeetPerSecond =
      new InterfacePlatformTypeUnitsEnum(6, "feet/second", "velocity");
   public final InterfacePlatformTypeUnitsEnum FeetPerSecondSquared =
      new InterfacePlatformTypeUnitsEnum(7, "feet/second²", "acceleration");
   public final InterfacePlatformTypeUnitsEnum Radians =
      new InterfacePlatformTypeUnitsEnum(8, "radians", "latitude / longitude");
   public final InterfacePlatformTypeUnitsEnum UnitSquared = new InterfacePlatformTypeUnitsEnum(9, "unit²", "variance");
   public final InterfacePlatformTypeUnitsEnum Hertz = new InterfacePlatformTypeUnitsEnum(10, "hertz", "frequency");
   public final InterfacePlatformTypeUnitsEnum dBm = new InterfacePlatformTypeUnitsEnum(11, "dBm", "power");
   public final InterfacePlatformTypeUnitsEnum kW = new InterfacePlatformTypeUnitsEnum(12, "kW", "power");
   public final InterfacePlatformTypeUnitsEnum Nm = new InterfacePlatformTypeUnitsEnum(13, "Nm", "distance");
   public final InterfacePlatformTypeUnitsEnum DegreesPerSecond =
      new InterfacePlatformTypeUnitsEnum(14, "degrees/second", "attitude rate");
   public final InterfacePlatformTypeUnitsEnum FeetSquaredPerSecondSquared =
      new InterfacePlatformTypeUnitsEnum(15, "feet²/second²", "velocity variance");
   public final InterfacePlatformTypeUnitsEnum Volts = new InterfacePlatformTypeUnitsEnum(16, "volts", "voltage");
   public final InterfacePlatformTypeUnitsEnum DegreesCelcius =
      new InterfacePlatformTypeUnitsEnum(17, "°C", "temperature-C");
   public final InterfacePlatformTypeUnitsEnum DegreesFahrenheit =
      new InterfacePlatformTypeUnitsEnum(18, "°F", "temperature-F");
   public final InterfacePlatformTypeUnitsEnum DI = new InterfacePlatformTypeUnitsEnum(19, "DI", "display units");
   public final InterfacePlatformTypeUnitsEnum Bd = new InterfacePlatformTypeUnitsEnum(20, "Bd", "Baud");
   public final InterfacePlatformTypeUnitsEnum dB = new InterfacePlatformTypeUnitsEnum(21, "dB", "Decibel");
   public final InterfacePlatformTypeUnitsEnum DegreesRankine =
      new InterfacePlatformTypeUnitsEnum(22, "°R", "Temperature");
   public final InterfacePlatformTypeUnitsEnum InHG = new InterfacePlatformTypeUnitsEnum(23, "in HG", "Pressure");
   public final InterfacePlatformTypeUnitsEnum Nanoseconds =
      new InterfacePlatformTypeUnitsEnum(24, "nanoseconds", "time");
   public final InterfacePlatformTypeUnitsEnum Seconds = new InterfacePlatformTypeUnitsEnum(25, "seconds", "time");
   public final InterfacePlatformTypeUnitsEnum Minutes = new InterfacePlatformTypeUnitsEnum(26, "minutes", "time");
   public final InterfacePlatformTypeUnitsEnum Hours = new InterfacePlatformTypeUnitsEnum(27, "hours", "time");
   public final InterfacePlatformTypeUnitsEnum Days = new InterfacePlatformTypeUnitsEnum(28, "days", "time");
   public final InterfacePlatformTypeUnitsEnum Years = new InterfacePlatformTypeUnitsEnum(29, "years", "time");
   public final InterfacePlatformTypeUnitsEnum FeetSquared =
      new InterfacePlatformTypeUnitsEnum(30, "Feet^2", "Position");

   public InterfacePlatformTypeUnitsAttribute(NamespaceToken namespace, int enumCount) {
      super(4026643196432874344L, namespace, "Interface Platform Type Units", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfacePlatformTypeUnitsAttribute() {
      this(NamespaceToken.OSEE, 31);
   }

   public class InterfacePlatformTypeUnitsEnum extends EnumToken {

      public InterfacePlatformTypeUnitsEnum(int ordinal, String name, String desc) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}