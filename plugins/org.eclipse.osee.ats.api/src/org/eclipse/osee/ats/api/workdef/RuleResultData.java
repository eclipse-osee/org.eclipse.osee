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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mark Joy
 */
@XmlRootElement
public class RuleResultData {

   Long uuid;
   List<RuleResultsEnum> resultList = new ArrayList<>();

   public Long getUuid() {
      return uuid;
   }

   public void setUuid(Long uuid) {
      this.uuid = uuid;
   }

   public List<RuleResultsEnum> getResultList() {
      return resultList;
   }

   public void setResultList(List<RuleResultsEnum> resultList) {
      this.resultList = resultList;
   }
}
