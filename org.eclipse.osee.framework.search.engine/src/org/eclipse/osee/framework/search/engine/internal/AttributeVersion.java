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
package org.eclipse.osee.framework.search.engine.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public class AttributeVersion {
   private int attrId;
   private long gamma_id;

   public AttributeVersion(int attrId, long gamma_id) {
      super();
      this.attrId = attrId;
      this.gamma_id = gamma_id;
   }

   public AttributeVersion(ResultSet resultSet) throws SQLException {
      this(resultSet.getInt("attr_id"), resultSet.getLong("gamma_id"));
   }

   public int getAttrId() {
      return attrId;
   }

   public long getGamma_id() {
      return gamma_id;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object object) {
      if (this == object) return true;
      if (!(object instanceof AttributeVersion)) return false;
      AttributeVersion other = (AttributeVersion) object;
      return other.attrId == this.attrId && other.gamma_id == this.gamma_id;
   }

   public String toString() {
      return String.format("attrId: [%s] gammaId: [%d]", getAttrId(), getGamma_id());
   }

   public Object[] toArray() {
      return new Object[] {SQL3DataType.INTEGER, getAttrId(), SQL3DataType.BIGINT, getGamma_id()};
   }
}
