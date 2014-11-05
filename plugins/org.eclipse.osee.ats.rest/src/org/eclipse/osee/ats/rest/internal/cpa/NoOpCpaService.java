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
package org.eclipse.osee.ats.rest.internal.cpa;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.cpa.IAtsCpaProgram;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.cpa.ICpaPcr;

/**
 * @author Roberto E. Escobar
 */
public class NoOpCpaService implements IAtsCpaService {

   private static final String CPA_SERVER_ID = "no-op-service";

   @Override
   public String getId() {
      return CPA_SERVER_ID;
   }

   @Override
   public List<IAtsCpaProgram> getPrograms() {
      return Collections.emptyList();
   }

   @Override
   public String getConfigJson() throws Exception {
      return "{}";
   }

   @Override
   public URI getLocation(URI uri, String uuid) {
      return null;
   }

   @Override
   public ICpaPcr getPcr(String pcrId) {
      return null;
   }

   @Override
   public Map<String, ICpaPcr> getPcrsByIds(Collection<String> issueIds) {
      return Collections.emptyMap();
   }

}
