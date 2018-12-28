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
package org.eclipse.osee.ats.api.workdef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Joy
 */
public class RuleResultData {

   Long id;
   List<RuleResultsEnum> resultList = new ArrayList<>();

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public List<RuleResultsEnum> getResultList() {
      return resultList;
   }

   public void setResultList(List<RuleResultsEnum> resultList) {
      this.resultList = resultList;
   }
}
