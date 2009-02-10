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
package org.eclipse.osee.framework.jdk.core.util;

import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class SingletonApplicationInstance {

    private static DatagramChannel lockChannel;
    
    public static void verifySingleton(int port) throws Exception {
	if (findOther(port)) {
	    System.out.println("found another instance");
	} else {
	    System.out.println("no other instance found");
	}
    }


    private static boolean findOther(int port) throws Exception {
	InetAddress host = InetAddress.getLocalHost();
	try {
	    DatagramChannel channel = DatagramChannel.open();
	    channel.configureBlocking(true);
	    InetSocketAddress address = new InetSocketAddress(host, port);
	    channel.socket().bind(address);
	    lockChannel = channel;
	    return false;
	} catch (BindException e) {
	    return true;
	}
    }

    public static void main(String[] args) {
	try {
	    verifySingleton(32900);
	    System.in.read();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
}
