/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile.program;

import org.eclipse.osee.ats.api.agile.JaxAgileProgramBacklogItem;

/**
 * @author Donald G. Dunne
 */
public class JaxProgramBacklogItemUpdate extends JaxProgramBaseItem {

   JaxAgileProgramBacklogItem item;

   public JaxAgileProgramBacklogItem getItem() {
      return item;
   }

   public void setItem(JaxAgileProgramBacklogItem item) {
      this.item = item;
   }

}