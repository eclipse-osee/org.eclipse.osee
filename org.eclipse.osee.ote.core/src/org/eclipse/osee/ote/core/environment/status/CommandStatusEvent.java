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
package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;



/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class CommandStatusEvent implements Serializable{

    /**
    * 
    */
   private static final long serialVersionUID = -567005567921815848L;
   private CommandDescription description;

	/**
     * CommandStatusEvent Constructor.
     * 
	 * @param description The command description.
	 */
	public CommandStatusEvent(CommandDescription description) {
		super();
		this.description = description;
	}

	/**
	 * @return Returns the description.
	 */
	public CommandDescription getDescription() {
		return description;
	}
}