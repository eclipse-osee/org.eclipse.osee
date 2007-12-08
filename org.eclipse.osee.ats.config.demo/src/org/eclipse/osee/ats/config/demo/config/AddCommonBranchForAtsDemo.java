/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.config.demo.config;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.dbinit.AddCommonBranch;

/**
 * @author Donald G. Dunne
 */
public class AddCommonBranchForAtsDemo extends AddCommonBranch {

   @Override
   public List<String> getSkynetDbTypeExtensionIds() {
      List<String> skynetTypeImport = super.getSkynetDbTypeExtensionIds();
      skynetTypeImport.add("org.eclipse.osee.ats.config.demo.Demo_Common_Skynet_Types");
      skynetTypeImport.add("org.eclipse.osee.ats.ATS_Skynet_Types");
      return skynetTypeImport;
   }

}
