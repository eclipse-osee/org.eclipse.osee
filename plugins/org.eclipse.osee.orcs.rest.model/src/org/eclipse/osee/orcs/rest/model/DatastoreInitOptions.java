/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class DatastoreInitOptions {

   private String tableDataSpace;
   private String indexDataSpace;
   private boolean useFileSpecifiedSchemas;

   public String getTableDataSpace() {
      return tableDataSpace;
   }

   public String getIndexDataSpace() {
      return indexDataSpace;
   }

   public boolean isUseFileSpecifiedSchemas() {
      return useFileSpecifiedSchemas;
   }

   public void setTableDataSpace(String tableDataSpace) {
      this.tableDataSpace = tableDataSpace;
   }

   public void setIndexDataSpace(String indexDataSpace) {
      this.indexDataSpace = indexDataSpace;
   }

   public void setUseFileSpecifiedSchemas(boolean useFileSpecifiedSchemas) {
      this.useFileSpecifiedSchemas = useFileSpecifiedSchemas;
   }

}
