/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.cpa;

import java.net.URI;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsCpaService {

   String getId();

   List<IAtsCpaProgram> getPrograms();

   String getConfigJson() throws Exception;

   URI getLocation(URI uri, String uuid);

   String getProgramName(String pcrId);
}
