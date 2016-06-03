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
package org.eclipse.osee.orcs.rest.model;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class ApplicabilityIds {

   private List<ApplicabilityId> applicabilityIds;

   public List<ApplicabilityId> getApplicabilityIds() {
      if (applicabilityIds == null) {
         applicabilityIds = new LinkedList<>();
      }
      return applicabilityIds;
   }

   public void setApplicabilityIds(List<ApplicabilityId> applicabilityIds) {
      this.applicabilityIds = applicabilityIds;
   }

}
