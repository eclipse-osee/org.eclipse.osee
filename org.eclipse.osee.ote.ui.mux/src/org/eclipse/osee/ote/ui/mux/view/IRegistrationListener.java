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
package org.eclipse.osee.ote.ui.mux.view;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;

/**
 * @author Ken J. Aguilar
 *
 */
public interface IRegistrationListener extends Remote, IInstrumentationRegistrationListener {

	void onRegistered(String name, IOInstrumentation instrumentation) throws RemoteException;

	void onDeregistered(String name) throws RemoteException;
}
