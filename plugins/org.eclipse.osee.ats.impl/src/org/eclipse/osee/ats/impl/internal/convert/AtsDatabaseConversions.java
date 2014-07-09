/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.convert;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Return available database conversions
 * 
 * @author Donald G Dunne
 */
public class AtsDatabaseConversions {

   private static List<IAtsDatabaseConversion> externalConversions = new ArrayList<IAtsDatabaseConversion>();

   public static List<IAtsDatabaseConversion> getConversions(OrcsApi orcsApi, IOseeDatabaseService dbService) {
      List<IAtsDatabaseConversion> conversions = new ArrayList<IAtsDatabaseConversion>();
      conversions.add(new ConvertBaselineGuidToBaselineUuid(dbService, orcsApi));
      conversions.add(new ConvertFavoriteBranchGuidToUuid(dbService, orcsApi));
      conversions.addAll(externalConversions);
      return conversions;
   }

   public void addDatabaseConversion(IAtsDatabaseConversion conversion) {
      externalConversions.add(conversion);
   }
}
