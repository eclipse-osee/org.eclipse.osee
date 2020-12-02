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
import org.eclipse.osee.framework.core.enums.token.InterfaceElementUnitsAttribute.InterfaceElementUnitsEnum;

/**
 * @author Audrey Denk
 */
public class InterfaceElementUnitsAttribute extends AttributeTypeEnum<InterfaceElementUnitsEnum> {

   public final InterfaceElementUnitsEnum Unitless = new InterfaceElementUnitsEnum(0, "n/a", "Unitless");
   public final InterfaceElementUnitsEnum Feet = new InterfaceElementUnitsEnum(1, "feet", "distance / altitude");
   public final InterfaceElementUnitsEnum Degrees = new InterfaceElementUnitsEnum(2, "degrees", "heading / bearing");
   public final InterfaceElementUnitsEnum DirectionCosine =
      new InterfaceElementUnitsEnum(3, "direction cosine", "LOS angle");
   public final InterfaceElementUnitsEnum DirectionCosinePerSecond =
      new InterfaceElementUnitsEnum(4, "direction cosine/second", "LOS rate");
   public final InterfaceElementUnitsEnum RadiansPerSec = new InterfaceElementUnitsEnum(5, "radians/sec", "LOS rate");
   public final InterfaceElementUnitsEnum FeetPerSecond = new InterfaceElementUnitsEnum(6, "feet/second", "velocity");
   public final InterfaceElementUnitsEnum FeetPerSecondSquared =
      new InterfaceElementUnitsEnum(7, "feet/second²", "acceleration");
   public final InterfaceElementUnitsEnum Radians = new InterfaceElementUnitsEnum(8, "radians", "latitude / longitude");
   public final InterfaceElementUnitsEnum UnitSquared = new InterfaceElementUnitsEnum(9, "unit²", "variance");
   public final InterfaceElementUnitsEnum Hertz = new InterfaceElementUnitsEnum(10, "hertz", "frequency");
   public final InterfaceElementUnitsEnum dBm = new InterfaceElementUnitsEnum(11, "dBm", "power");
   public final InterfaceElementUnitsEnum kW = new InterfaceElementUnitsEnum(12, "kW", "power");
   public final InterfaceElementUnitsEnum Nm = new InterfaceElementUnitsEnum(13, "Nm", "distance");
   public final InterfaceElementUnitsEnum DegreesPerSecond =
      new InterfaceElementUnitsEnum(14, "degrees/second", "attitude rate");
   public final InterfaceElementUnitsEnum FeetSquaredPerSecondSquared =
      new InterfaceElementUnitsEnum(15, "feet²/second²", "velocity variance");
   public final InterfaceElementUnitsEnum Volts = new InterfaceElementUnitsEnum(16, "volts", "voltage");
   public final InterfaceElementUnitsEnum DegreesCelcius = new InterfaceElementUnitsEnum(17, "°C", "temperature-C");
   public final InterfaceElementUnitsEnum DegreesFahrenheit = new InterfaceElementUnitsEnum(18, "°F", "temperature-F");
   public final InterfaceElementUnitsEnum DI = new InterfaceElementUnitsEnum(19, "DI", "display units");
   public final InterfaceElementUnitsEnum Bd = new InterfaceElementUnitsEnum(20, "Bd", "Baud");
   public final InterfaceElementUnitsEnum dB = new InterfaceElementUnitsEnum(21, "dB", "Decibel");
   public final InterfaceElementUnitsEnum DegreesRankine = new InterfaceElementUnitsEnum(22, "°R", "Temperature");
   public final InterfaceElementUnitsEnum InHG = new InterfaceElementUnitsEnum(23, "in HG", "Pressure");
   public final InterfaceElementUnitsEnum Nanoseconds = new InterfaceElementUnitsEnum(24, "nanoseconds", "time");
   public final InterfaceElementUnitsEnum Seconds = new InterfaceElementUnitsEnum(25, "seconds", "time");
   public final InterfaceElementUnitsEnum Minutes = new InterfaceElementUnitsEnum(26, "minutes", "time");
   public final InterfaceElementUnitsEnum Hours = new InterfaceElementUnitsEnum(27, "hours", "time");
   public final InterfaceElementUnitsEnum Days = new InterfaceElementUnitsEnum(28, "days", "time");
   public final InterfaceElementUnitsEnum Years = new InterfaceElementUnitsEnum(29, "years", "time");
   public final InterfaceElementUnitsEnum FeetSquared = new InterfaceElementUnitsEnum(30, "Feet^2", "Position");

   public InterfaceElementUnitsAttribute(NamespaceToken namespace, int enumCount) {
      super(4026643196432874344L, namespace, "Interface Element Units", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public InterfaceElementUnitsAttribute() {
      this(NamespaceToken.OSEE, 31);
   }

   public class InterfaceElementUnitsEnum extends EnumToken {
      private String description;

      public InterfaceElementUnitsEnum(int ordinal, String name, String desc) {
         super(ordinal, name);
         setDescription(desc);
         addEnum(this);
      }

      public String getDescription() {
         return description;
      }

      public void setDescription(String description) {
         this.description = description;
      }
   }
}