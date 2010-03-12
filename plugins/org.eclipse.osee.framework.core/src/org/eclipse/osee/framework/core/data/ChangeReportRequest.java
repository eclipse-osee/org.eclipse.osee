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
package org.eclipse.osee.framework.core.data;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportRequest {
	private final int srcTx;
	private final int destTx;
	private final boolean isHistorical;

	public ChangeReportRequest(int srcTx, int destTx, boolean isHistorical) {
		super();
		this.srcTx = srcTx;
		this.destTx = destTx;
		this.isHistorical = isHistorical;
	}

	public int getSourceTx() {
		return srcTx;
	}

	public int getDestinationTx() {
		return destTx;
	}

	public boolean isHistorical() {
		return isHistorical;
	}
}
