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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * Model in support of Agile Program's ui_grid. Don't refactor without manually testing Agile Program page
 * 
 * @author Donald G. Dunne
 */
public class UiGridProgram extends JaxAtsObject {

   List<UiGridProgItem> items = new ArrayList<UiGridProgItem>();

   public UiGridProgram() {
      // for jax-rs
   }

   public List<UiGridProgItem> getItems() {
      return items;
   }

   public void setItems(List<UiGridProgItem> items) {
      this.items = items;
   }

}
