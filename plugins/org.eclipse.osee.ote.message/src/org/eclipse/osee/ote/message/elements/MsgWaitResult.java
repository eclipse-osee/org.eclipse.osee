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
package org.eclipse.osee.ote.message.elements;

public class MsgWaitResult {
    private final long time;
    private final int xmitCount;
    private final boolean passed;
    
    public MsgWaitResult(long time, int xmitCount, boolean passed) {
	this.time = time;
	this.xmitCount = xmitCount;
	this.passed = passed;
    }
    
    public long getElapsedTime() {
        return time;
    }
    public int getXmitCount() {
        return xmitCount;
    }
    public boolean isPassed() {
        return passed;
    }
    
    
}
