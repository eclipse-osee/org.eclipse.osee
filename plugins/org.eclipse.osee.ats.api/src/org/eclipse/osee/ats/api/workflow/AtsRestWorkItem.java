/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AtsRestWorkItem {

   private final String name;
   private final long id;
   private final String atsId;

   public AtsRestWorkItem(String name, long id, String atsId) {
      this.name = name;
      this.id = id;
      this.atsId = atsId;
   }

   public String getName() {
      return name;
   }

   public long getId() {
      return id;
   }

   public String getAtsId() {
      return atsId;
   }

}
