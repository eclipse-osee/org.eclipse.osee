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

package org.eclipse.osee.orcs.rest.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class IndexResources {

   private List<Long> gammaIds;
   private boolean waitForIndexerToComplete;

   public List<Long> getGammaIds() {
      return gammaIds != null ? gammaIds : Collections.<Long> emptyList();
   }

   public void setGammaIds(List<Long> gammaIds) {
      this.gammaIds = gammaIds;
   }

   public boolean isWaitForIndexerToComplete() {
      return waitForIndexerToComplete;
   }

   public void setWaitForIndexerToComplete(boolean waitForIndexerToComplete) {
      this.waitForIndexerToComplete = waitForIndexerToComplete;
   }
}