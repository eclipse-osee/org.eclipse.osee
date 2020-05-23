/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.mbse.cameo.browser;

/**
 * @author David W. Miller
 */
public class ParentBranch {
   private long id;
   private int viewId;

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public int getViewId() {
      return viewId;
   }

   public void setViewId(int viewId) {
      this.viewId = viewId;
   }
}
