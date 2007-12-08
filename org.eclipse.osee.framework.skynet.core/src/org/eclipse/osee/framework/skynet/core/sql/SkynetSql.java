/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.sql;

public class SkynetSql {

   private static SkynetSql instance = null;
   private SkynetMetaData skynetMetaData;
   private SkynetData skynetData;
   private SkynetRelational skynetRelational;
   private SkynetRevisionControl skynetRevisionControl;

   private SkynetSql() {
      skynetMetaData = SkynetMetaData.getInstance();
      skynetData = SkynetData.getInstance();
      skynetRelational = SkynetRelational.getInstance();
      skynetRevisionControl = SkynetRevisionControl.getInstance();
   }

   public static SkynetSql getInstance() {
      if (instance == null) {
         instance = new SkynetSql();
      }
      return instance;
   }

   public SkynetData getDataSql() {
      return skynetData;
   }

   public SkynetMetaData getMetaDataSql() {
      return skynetMetaData;
   }

   public SkynetRelational getRelationalSql() {
      return skynetRelational;
   }

   public SkynetRevisionControl getRevisionControlSql() {
      return skynetRevisionControl;
   }
}
