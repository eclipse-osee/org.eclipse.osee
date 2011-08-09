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
package org.eclipse.osee.cluster.admin.rest.internal;

import javax.ws.rs.Path;

/**
 * @author Roberto E. Escobar
 */
@Path("jobs")
public class JobsResource {
   //
   // GET /jobs/ gets list of jobs
   //
   // One user methods:
   //
   // GET /jobs/[jobId] gets job details
   // DELETE /jobs/[jobId] deletes a job - stops/cancels ?
   //
}
