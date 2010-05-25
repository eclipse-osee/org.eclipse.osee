/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch.action;

import org.eclipse.jface.action.Action;

/**
 * @author Ken J. Aguilar
 *
 */
public class DisabledAction extends Action {

	public DisabledAction(String name) {
		super(name, Action.AS_PUSH_BUTTON);
		setEnabled(false);
	}
}
