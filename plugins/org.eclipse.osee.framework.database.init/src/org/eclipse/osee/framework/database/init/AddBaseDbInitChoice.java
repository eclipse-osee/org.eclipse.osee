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

package org.eclipse.osee.framework.database.init;

/**
 * @author Ryan D. Brooks
 */
public class AddBaseDbInitChoice implements IAddDbInitChoice {
   public void addDbInitChoice(GroupSelection groupSelection) {
      DbInitConfiguration config = new DbInitConfiguration(true);
      groupSelection.addChoice("Base - for importing branches", config);
   }
}
