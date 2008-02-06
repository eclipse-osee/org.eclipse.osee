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
package org.eclipse.osee.framework.jini.discovery;

import java.net.InetAddress;
import java.security.Permission;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelaxedSecurity extends SecurityManager {

	/**
	 * 
	 */
	public RelaxedSecurity() {
		super();
	}

	public void checkPermission(Permission perm) {
		return;
	}

	public void checkPermission(Permission perm, Object context) {
		return;
	}

	@Override
	public void checkAccept(String host, int port) {
		return;
	}

	@Override
	public void checkConnect(String host, int port, Object context) {
	}

	@Override
	public void checkMulticast(InetAddress maddr, byte ttl) {
	}

	@Override
	public void checkMulticast(InetAddress maddr) {
	}

	@Override
	public void checkConnect(String host, int port) {
	}

	@Override
	public void checkPackageAccess(String pkg) {

	}

	


}
