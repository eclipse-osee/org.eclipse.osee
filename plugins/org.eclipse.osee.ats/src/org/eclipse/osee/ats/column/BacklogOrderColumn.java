/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.osee.ats.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class BacklogOrderColumn extends GoalOrderColumn {

   public static final String COLUMN_ID = WorldXViewerFactory.COLUMN_NAMESPACE + ".backlogOrder";

   static BacklogOrderColumn instance = new BacklogOrderColumn();

   public static BacklogOrderColumn getInstance() {
      return instance;
   }

   public BacklogOrderColumn() {
      super(true, COLUMN_ID, "Backlog Order");
   }

}
