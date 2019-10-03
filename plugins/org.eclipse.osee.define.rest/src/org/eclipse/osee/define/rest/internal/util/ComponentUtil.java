/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal.util;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation_Component;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan D. Brooks
 */
public class ComponentUtil {
   private final BranchId branchId;
   private final OrcsApi orcsApi;
   private ArtifactReadable mpCsci;
   private Collection<ArtifactReadable> mpComponents;
   private boolean wasLoaded;

   public ComponentUtil(BranchId branchId, OrcsApi providedOrcs) {
      super();
      this.branchId = branchId;
      this.mpComponents = null;
      this.wasLoaded = false;
      this.mpCsci = null;
      this.orcsApi = providedOrcs;
   }

   private synchronized void load() {
      wasLoaded = true;
      mpCsci = orcsApi.getQueryFactory().fromBranch(branchId).andTypeEquals(CoreArtifactTypes.Component).andNameEquals(
         "MP CSCI").getResults().getExactlyOne();
      mpComponents = mpCsci.getDescendants();
   }

   private void ensureLoaded() {
      if (!wasLoaded) {
         load();
      }
   }

   public String getQualifiedComponentName(ArtifactReadable component) {
      ensureLoaded();
      if (component.getParent().equals(mpCsci)) {
         return component.getName();
      }
      return component.getParent().getName() + "." + component.getName();
   }

   public String getQualifiedComponentNames(ArtifactReadable requirement) {
      ensureLoaded();
      ResultSet<ArtifactReadable> components = requirement.getRelated(Allocation_Component);

      StringBuilder strB = new StringBuilder(20);

      for (ArtifactReadable component : components) {
         if (mpComponents.contains(component)) {
            strB.append(getQualifiedComponentName(component));
            strB.append(", ");
         }
      }
      return strB.length() == 0 ? null : strB.substring(0, strB.length() - 2);
   }

   public Collection<ArtifactReadable> getComponents() {
      ensureLoaded();
      return mpComponents;
   }
}
