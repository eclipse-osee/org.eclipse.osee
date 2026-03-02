/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.ArrayList;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Donald G. Dunne
 */
@Component(service = BlamService.class, immediate = true)
public class BlamService {

   private static final List<AbstractBlam> blams = new ArrayList<AbstractBlam>();

   @Reference(service = //
   AbstractBlam.class, //
      cardinality = ReferenceCardinality.MULTIPLE, //
      policy = ReferencePolicy.DYNAMIC, //
      policyOption = ReferencePolicyOption.GREEDY, //
      bind = "addBlam" //
   )

   public void addBlam(AbstractBlam blam) {
      blams.add(blam);
   }

   public BlamService() {
      // for osgi
   }

   public static List<AbstractBlam> getBlams() {
      return blams;
   }

}
