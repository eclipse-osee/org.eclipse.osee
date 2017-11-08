/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAgileProgramBacklog extends IAgileObject {

   String getName();

   Long getProgramId();

   List<Long> getBacklogItemIds();

   Long getId();

}
