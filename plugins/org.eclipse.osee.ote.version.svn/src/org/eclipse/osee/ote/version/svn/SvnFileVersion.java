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
package org.eclipse.osee.ote.version.svn;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.team.svn.core.connector.SVNEntryInfo;


public class SvnFileVersion implements FileVersion {

	private SVNEntryInfo entry;
	private DateFormat dateFormat;

	public SvnFileVersion(SVNEntryInfo svnEntryInfo) {
       this.entry = svnEntryInfo;
       dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
	}
	  
	@Override
	public String getLastChangedRevision() {
		return Long.toString(entry.lastChangedRevision);
	}

	@Override
	public String getURL() {
		return entry.url;
	}

	@Override
	public String getVersionControlSystem() {
		return "svn";
	}

	@Override
	public String getModifiedFlag() {
		/*
		 *  IFile iFile = AIFile.constructIFile(file.getAbsolutePath());
	      ILocalResource local = SVNRemoteStorage.instance().asLocalResource(iFile);
	      entry.setModifiedFlag(SVNUtility.getStatusText(local.getStatus()));
		 */
		return "N/A";
	}

	@Override
	public String getLastModificationDate() {
		return dateFormat.format(new Date(entry.lastChangedDate));
	}

	@Override
	public String getLastAuthor() {
		return entry.lastChangedAuthor;
	}

}
