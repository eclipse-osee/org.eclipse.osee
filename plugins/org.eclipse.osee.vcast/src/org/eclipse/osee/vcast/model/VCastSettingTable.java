/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vcast.model;

import java.util.Collection;
import org.eclipse.osee.vcast.VCastDataStore;

/**
 * @author Roberto E. Escobar
 */
public class VCastSettingTable implements VCastTableData<VCastSetting> {

   @Override
   public String getName() {
      return "settings";
   }

   @Override
   public String[] getColumns() {
      return new String[] {"setting", "value"};
   }

   @Override
   public Collection<VCastSetting> getRows(VCastDataStore dataStore)  {
      return dataStore.getAllSettings();
   }

   @Override
   public Object[] toRow(VCastSetting data) {
      String settingValue = data.getSetting();
      String value = data.getValue();
      return new Object[] {settingValue, value};
   }
}
