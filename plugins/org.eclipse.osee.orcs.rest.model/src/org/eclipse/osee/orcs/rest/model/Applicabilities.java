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
public class Applicabilities {

   private List<Applicability> applicabilities;

   public List<Applicability> getApplicabilities() {
      if (applicabilities == null) {
         applicabilities = new LinkedList<>();
      }
      return applicabilities;
   }

   public void setApplicabilities(List<Applicability> applicabilities) {
      this.applicabilities = applicabilities;
   }

}
