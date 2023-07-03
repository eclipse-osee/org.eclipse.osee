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
package org.eclipse.osee.mim.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.mim.EnumEndpoint;

public class EnumEndpointImpl implements EnumEndpoint {

   public EnumEndpointImpl() {
   }

   @Override
   public Collection<String> getPeriodicity() {
      List<String> periodicities = new ArrayList<String>();
      periodicities.addAll(CoreAttributeTypes.InterfaceMessagePeriodicity.getEnumStrValues());
      periodicities.sort(Comparator.comparing(String::toString));
      return periodicities;
   }

   @Override
   public Collection<String> getMessageRates() {
      List<String> rates = new ArrayList<String>();
      rates.addAll(CoreAttributeTypes.InterfaceMessageRate.getEnumStrValues());
      rates.sort(new Comparator<String>() {

         @Override
         public int compare(String o1, String o2) {
            return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
         }

      });
      return rates;
   }

   @Override
   public Collection<String> getMessageTypes() {
      List<String> types = new ArrayList<String>();
      types.addAll(CoreAttributeTypes.InterfaceMessageType.getEnumStrValues());
      types.sort(Comparator.comparing(String::toString));
      return types;
   }

   @Override
   public Collection<String> getStructureCategories() {
      List<String> categories = new ArrayList<String>();
      categories.addAll(CoreAttributeTypes.InterfaceStructureCategory.getEnumStrValues());
      categories.sort(Comparator.comparing(String::toString));
      return categories;
   }

   @Override
   public Collection<String> getPossibleUnits() {
      List<String> units = new ArrayList<String>();
      units.addAll(CoreAttributeTypes.InterfacePlatformTypeUnits.getEnumStrValues());
      units.sort(Comparator.comparing(String::toString));
      return units;
   }

}
