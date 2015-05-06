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
package org.eclipse.osee.disposition.model;

import java.util.List;

/**
 * @author Angel Avila
 */
public class DispoConfigData implements DispoConfig {

   List<ResolutionMethod> validResolutions;

   public DispoConfigData() {

   }

   public void setValidResolutions(List<ResolutionMethod> validResolutions) {
      this.validResolutions = validResolutions;
   }

   @Override
   public List<ResolutionMethod> getValidResolutions() {
      return validResolutions;
   }

}
