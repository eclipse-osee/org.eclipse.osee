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
package org.eclipse.osee.ats.api.agile;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author Donald G. Dunne
 */
public class JaxNewAgileProgramFeature extends JaxAtsObject {

   @JsonSerialize(using = ToStringSerializer.class)
   private Long programBacklogItemId;

   public Long getProgramBacklogItemId() {
      return programBacklogItemId;
   }

   public void setProgramBacklogItemId(Long programBacklogItemId) {
      this.programBacklogItemId = programBacklogItemId;
   }

}
