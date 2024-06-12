/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.applicability;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;

public class ProductLineConfig {
   private List<FeatureValue> table = new LinkedList<FeatureValue>();
   private final List<ConfigurationValue> headers = new LinkedList<ConfigurationValue>();
   private final List<Integer> headerLengths = new LinkedList<Integer>();

   public ProductLineConfig(List<FeatureValue> table) {
      this.setTable(table);
      List<ConfigurationValue> internalHeaders =
         this.table.stream().flatMap(x -> x.getConfigurationValues().stream()).distinct().sorted(
            new NamedComparator(SortOrder.ASCENDING)).collect(Collectors.toList());
      // create list of all configs that belong to any group
      List<ArtifactId> configsInGroups =
         internalHeaders.stream().flatMap(x -> x.getRelated().stream()).collect(Collectors.toList());
      List<ConfigurationValue> groups =
         internalHeaders.stream().filter(x -> x.getTypeId().equals(CoreArtifactTypes.GroupArtifact)).collect(
            Collectors.toList());
      //initialize the group map with a 'No Group' that has all configurationValues that have no related group and aren't a group themselves
      List<ConfigurationValue> noConfig =
         Stream.concat(Stream.of(ConfigurationValue.NO_GROUP), internalHeaders.stream().filter(x -> {
            return !configsInGroups.contains(ArtifactId.valueOf(x.getId())) && !x.getTypeId().equals(
               CoreArtifactTypes.GroupArtifact);
         })).sorted((a, b) -> a.getApplicability().getName().compareTo(b.getApplicability().getName())).collect(
            Collectors.toList());
      if (noConfig.size() > 1) {
         headerLengths.add(noConfig.size() - 1);
         headers.addAll(noConfig);

      }
      //we are going to act like having no group is a group with a -1 Id
      for (ConfigurationValue group : groups) {
         List<ConfigurationValue> groupHeaders = internalHeaders.stream().filter(
            x -> x.getId().equals(group.getId()) || group.getRelated().contains(ArtifactId.valueOf(x.getId()))).sorted(
               (a, b) -> Long.valueOf(a.getTypeId().getId() - b.getTypeId().getId()).intValue()).sorted(
                  (a, b) -> a.getApplicability().getName().compareTo(b.getApplicability().getName())).collect(
                     Collectors.toList());
         headers.addAll(groupHeaders);
         headerLengths.add(groupHeaders.size());
      }
   }

   public ProductLineConfig() {
   }

   /**
    * @return the table
    */
   public List<FeatureValue> getTable() {
      return table;
   }

   /**
    * @param table the table to set
    */
   public void setTable(List<FeatureValue> table) {
      this.table = table;
   }

   /**
    * @return the headers
    */
   public List<ConfigurationValue> getHeaders() {
      return headers;
   }

   /**
    * @return the headerLengths
    */
   public List<Integer> getHeaderLengths() {
      return headerLengths;
   }
}
