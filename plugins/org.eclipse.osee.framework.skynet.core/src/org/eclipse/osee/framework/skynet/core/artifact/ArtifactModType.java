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
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Donald G. Dunne
 */
public enum ArtifactModType {

   Deleted, Added, Changed,

   // Dirtied artifact was reloaded to last saved state (not propagated remotely)
   // This is a Local modification type only and will not get transmitted as part of the transaction event
   Reverted

}
