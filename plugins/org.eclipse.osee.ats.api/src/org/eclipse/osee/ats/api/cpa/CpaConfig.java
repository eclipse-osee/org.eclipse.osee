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
package org.eclipse.osee.ats.api.cpa;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class CpaConfig {

   private final List<String> applicabilityOptions = new ArrayList<String>();

   private final List<CpaConfigTool> tools = new ArrayList<CpaConfigTool>();

   public List<String> getApplicabilityOptions() {
      return applicabilityOptions;
   }

   public List<CpaConfigTool> getTools() {
      return tools;
   }
}
