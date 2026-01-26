/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.world.search.pr;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ProblemReportCollector {

   private String name;
   private String description;
   private List<Artifact> collection = new ArrayList<>();

   public ProblemReportCollector() {
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<Artifact> getCollection() {
      return collection;
   }

   public void setCollection(List<Artifact> collection) {
      this.collection = collection;
   }

   public void add(Artifact art) {
      collection.add(art);
   }

}
