/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.transaction;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.DataProxy;

/**
 * @author Roberto E. Escobar
 */
public class DaoToSql {
   private final long gammaId;
   private final DataProxy proxy;
   private final boolean isNewGammaId;

   public DaoToSql(long gammaId, DataProxy proxy, boolean isNewGammaId) {
      super();
      this.gammaId = gammaId;
      this.proxy = proxy;
      this.isNewGammaId = isNewGammaId;
   }

   public boolean hasNewGammaId() {
      return isNewGammaId;
   }

   public long getGammaId() {
      return gammaId;
   }

   private DataProxy getProxy() {
      return proxy;
   }

   public String getUri() throws OseeCoreException {
      String uri = getItemAt(1, getProxy().getData());
      return uri != null ? uri : "";
   }

   public String getValue() throws OseeCoreException {
      String value = getItemAt(0, getProxy().getData());
      return value != null ? value : "";
   }

   public void persist() throws OseeCoreException {
      if (isNewGammaId) {
         proxy.persist(gammaId);
      }
   }

   public void rollBack() throws OseeCoreException {
      if (isNewGammaId) {
         getProxy().purge();
      }
   }

   private String getItemAt(int index, Object... data) {
      String toReturn = null;
      if (data != null && data.length > index) {
         Object obj = data[index];
         if (obj != null) {
            toReturn = obj.toString();
         }
      }
      return toReturn;
   }

   @Override
   public String toString() {
      return "DAOToSQL [gammaId=" + gammaId + ", isNewGammaId=" + isNewGammaId + ", proxy=" + proxy + "]";
   }

}