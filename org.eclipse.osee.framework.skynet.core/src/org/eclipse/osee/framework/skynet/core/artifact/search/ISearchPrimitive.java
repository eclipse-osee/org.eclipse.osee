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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

public interface ISearchPrimitive {

   /**
    * The sql operators that will provide a set of art_id's for the given search.
    * 
    * @param branch TODO
    * @return Return SQL string
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) throws SQLException;

   /**
    * The name of the column to use as the art_id column.
    * 
    * @return Return artifact column name string
    */
   public String getArtIdColName();

   /**
    * Returns a list of the tables, comma separated, that are necessary for the sql statement returned from getSql().
    * 
    * @param branch TODO
    * @return tables string
    * @see ISearchPrimitive#getCriteriaSql(List, Branch)
    */
   public String getTableSql(List<Object> dataList, Branch branch);

   /**
    * Returns a string which can be used to later re-acquire the primitive in full
    * 
    * @return Return storage string
    */
   public String getStorageString();
}
