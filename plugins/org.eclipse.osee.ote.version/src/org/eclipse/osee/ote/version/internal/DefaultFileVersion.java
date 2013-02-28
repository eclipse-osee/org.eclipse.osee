/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.version.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;

import org.eclipse.osee.ote.version.FileVersion;

public class DefaultFileVersion implements FileVersion {

	private String url;
	private String lastModified;

	public DefaultFileVersion(File file){
		if(file.exists()){
			try {
				this.url = file.toURI().toURL().toString();
			} catch (MalformedURLException e) {
				this.url = e.getMessage();
			}
			this.lastModified = new Date(file.lastModified()).toString();
		} else {
			this.url = "N/A";
		}
	}
	
	@Override
	public String getLastChangedRevision() {
		return "N/A";
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public String getVersionControlSystem() {
		return "NONE";
	}

	@Override
	public String getModifiedFlag() {
		return "N/A";
	}

	@Override
	public String getLastModificationDate() {
		return lastModified;
	}

	@Override
	public String getLastAuthor() {
		return System.getProperty("user.name");
	}

}
