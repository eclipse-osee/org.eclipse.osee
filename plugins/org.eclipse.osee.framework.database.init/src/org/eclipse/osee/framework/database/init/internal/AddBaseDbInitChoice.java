/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.database.init.internal;

import org.eclipse.osee.framework.database.init.DatabaseInitConfiguration;
import org.eclipse.osee.framework.database.init.DefaultOseeTypeDefinitions;
import org.eclipse.osee.framework.database.init.IAddDbInitChoice;
import org.eclipse.osee.framework.database.init.IGroupSelector;

/**
 * @author Ryan D. Brooks
 */
public class AddBaseDbInitChoice implements IAddDbInitChoice {
   @Override
   public void addDbInitChoice(IGroupSelector groupSelection) {
      DatabaseInitConfiguration config = new DatabaseInitConfiguration();
      for (DefaultOseeTypeDefinitions types : DefaultOseeTypeDefinitions.values()) {
         config.addOseeType(types);
      }
      groupSelection.addChoice("Base - for importing branches", config);
   }
}
