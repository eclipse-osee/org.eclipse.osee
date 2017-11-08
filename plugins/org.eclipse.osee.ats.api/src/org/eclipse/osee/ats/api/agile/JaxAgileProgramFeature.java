/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class JaxAgileProgramFeature extends JaxAtsObject {

   private long programBacklogItemId;

   public static JaxAgileProgramFeature construct(JaxAgileProgramBacklogItem item1, ArtifactToken programFeature) {
      JaxAgileProgramFeature feature = new JaxAgileProgramFeature();
      feature.setName(programFeature.getName());
      feature.setId(programFeature.getId());
      feature.setProgramBacklogItemId(item1.getId());
      return feature;
   }

   public long getProgramBacklogItemId() {
      return programBacklogItemId;
   }

   public void setProgramBacklogItemId(long programBacklogItemId) {
      this.programBacklogItemId = programBacklogItemId;
   }

}
