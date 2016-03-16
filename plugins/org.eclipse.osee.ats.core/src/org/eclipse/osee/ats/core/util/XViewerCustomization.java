/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;

/**
 * @author Morgan Cook
 */
public class XViewerCustomization {

   String name;
   String namespace;
   String guid;
   String sorterId;
   ArrayList<AtsAttributeValueColumn> columns;

   public XViewerCustomization() {
      name = "";
      namespace = "";
      guid = "";
      sorterId = "";
      columns = new ArrayList<AtsAttributeValueColumn>();

   }

   public String getName() {
      return name;
   }

   public String getNamespace() {
      return namespace;
   }

   public String getGuid() {
      return guid;
   }

   public ArrayList<AtsAttributeValueColumn> getColumns() {
      return columns;
   }

   public String getSorterId() {
      return sorterId;
   }

   public void setSorterId(String sorterId) {
      this.sorterId = sorterId;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setColumns(ArrayList<AtsAttributeValueColumn> columns) {
      this.columns = columns;
   }

   @Override
   public String toString() {
      return "XViewerCustomization [name=" + name + ", namespace=" + namespace + ", guid=" + guid + ", sorterId=" + sorterId + ", columns=" + columns + "]";
   }
}
