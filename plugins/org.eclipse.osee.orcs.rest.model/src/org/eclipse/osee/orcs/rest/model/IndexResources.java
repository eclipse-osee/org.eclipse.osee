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
package org.eclipse.osee.orcs.rest.model;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

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