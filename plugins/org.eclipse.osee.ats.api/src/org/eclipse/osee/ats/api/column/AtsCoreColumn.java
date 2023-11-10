/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.column;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsCoreColumn {

   public static final String CELL_ERROR_PREFIX = "!Error";
   protected final AtsApi atsApi;
   protected AtsCoreColumnToken columnToken;

   public AtsCoreColumn() {
      this(null, null);
      // for jax-rs
   }

   public AtsCoreColumn(AtsCoreColumnToken columnToken, AtsApi atsApi) {
      this.columnToken = columnToken;
      this.atsApi = atsApi;
   }

   @JsonIgnore
   public abstract String getColumnText(IAtsObject atsObject);

   @JsonIgnore
   public String getColumnType() {
      return getClass().getSimpleName();
   }

   @JsonIgnore
   public String getId() {
      return columnToken.getId();
   }

   public String getSource() {
      return getClass().getSimpleName();
   }

   public AtsCoreColumnToken getColumnToken() {
      return columnToken;
   }

   public void setColumnToken(AtsCoreColumnToken columnToken) {
      this.columnToken = columnToken;
   }

}
