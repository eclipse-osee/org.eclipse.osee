/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Jaden Puckett
 */
public class ApplicabilityTokenWithConstraints extends NamedIdBase {

   public static final ApplicabilityTokenWithConstraints BASE =
      new ApplicabilityTokenWithConstraints(ApplicabilityId.BASE.getId(), "Base", new ArrayList<>());
   public static final ApplicabilityTokenWithConstraints SENTINEL =
      new ApplicabilityTokenWithConstraints(ApplicabilityId.SENTINEL.getId(), "Sentinel", new ArrayList<>());

   private List<ApplicabilityTokenWithConstraints> constraints = new ArrayList<>();

   public ApplicabilityTokenWithConstraints(long applId, String name, List<ApplicabilityTokenWithConstraints> constraints) {
      super(applId, name);
      this.constraints = constraints;
   }

   public ApplicabilityTokenWithConstraints(Long applId, String name, List<ApplicabilityTokenWithConstraints> constraints) {
      super(applId, name);
      this.constraints = constraints;
   }

   public ApplicabilityTokenWithConstraints(long applId, String name) {
      super(applId, name);
   }

   public ApplicabilityTokenWithConstraints(Long applId, String name) {
      super(applId, name);
   }

   public List<ApplicabilityTokenWithConstraints> getConstraints() {
      return this.constraints;
   }

   public void addConstraint(ApplicabilityTokenWithConstraints constraint) {
      this.constraints.add(constraint);
   }

   public void clearConstraints() {
      this.constraints.clear();
   }

}
