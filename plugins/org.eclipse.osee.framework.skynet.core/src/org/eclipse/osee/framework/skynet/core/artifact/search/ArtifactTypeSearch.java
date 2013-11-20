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

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Robert A. Fisher
 */
public class ArtifactTypeSearch implements ISearchPrimitive {
   private final Identity<Long> artifactType;
   private final DeprecatedOperator operation;
   private static final String tables = "osee_artifact";
   private final static String TOKEN = ";";

   public ArtifactTypeSearch(Identity<Long> type, DeprecatedOperator operation) {
      super();
      this.artifactType = type;
      this.operation = operation;
   }

   @Override
   public String getArtIdColName() {
      return "art_id";
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, IOseeBranch branch) throws OseeCoreException {
      String sql = "osee_artifact.art_type_id = ?";
      dataList.add(artifactType.getGuid());
      return sql;
   }

   @Override
   public String getTableSql(List<Object> dataList, IOseeBranch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return "Artifact type: " + artifactType;
   }

   @Override
   public String getStorageString() {
      return artifactType.getGuid().toString() + TOKEN + operation.name();
   }

   public static ArtifactTypeSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) {
         throw new IllegalStateException("Value for " + ArtifactTypeSearch.class.getSimpleName() + " not parsable");
      }

      Identity<Long> identity = new BaseIdentity<Long>(Long.valueOf(values[0]));
      return new ArtifactTypeSearch(identity, DeprecatedOperator.valueOf(values[1]));
   }

}
