/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.config.AtsViews;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import com.google.gson.Gson;

public class ConvertColumnToJson {

   public static Gson gson = new Gson();

   public static void main(String[] args) {

      AtsViews views = new AtsViews();
      add(views, OriginatingPcrIdColumn.getInstance());
      add(views, DuplicatedPcrIdColumn.getInstance());
      add(views, ApplicabilityColumn.getInstance());
      add(views, RationaleColumn.getInstance());
      add(views, PcrToolIdColumn.getInstance());

      System.out.println(gson.toJson(views));
   }

   public static void add(AtsViews views, XViewerAtsAttributeValueColumn inCol) {
      AtsAttributeValueColumn column = AtsAttributeValueColumnFactory.get(WorldXViewerFactory.NAMESPACE, inCol);
      views.getAttrColumns().add(column);
   }
}
