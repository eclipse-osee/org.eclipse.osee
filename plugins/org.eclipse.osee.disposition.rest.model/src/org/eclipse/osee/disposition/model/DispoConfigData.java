/*********************************************************************
 * Copyright (c) 2015 Boeing
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
