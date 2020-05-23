/*********************************************************************
 * Copyright (c) 2017 Boeing
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

   List<UiGridProgItem> items = new ArrayList<>();

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
