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
package org.eclipse.osee.ote.core.environment;

import java.io.Serializable;
import java.util.List;

public class BundleConfigurationReport implements Serializable {
	private static final long serialVersionUID = 2948282371713776849L;
	private List<BundleDescription> missing;
	private List<BundleDescription> versionMismatch;
	private List<BundleDescription> partOfInstallation; 
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<BundleDescription> getMissing() {
		return missing;
	}

	public List<BundleDescription> getVersionMismatch() {
		return versionMismatch;
	}

	public List<BundleDescription> getPartOfInstallation() {
		return partOfInstallation;
	}

	public BundleConfigurationReport(List<BundleDescription> missing, List<BundleDescription> versionMismatch, List<BundleDescription> partOfInstallation){
		this.missing = missing;
		this.partOfInstallation = partOfInstallation;
		this.versionMismatch = versionMismatch;
	}
}
