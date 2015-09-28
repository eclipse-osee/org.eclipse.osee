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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformation;
import org.eclipse.osee.ote.version.FileVersionInformationProvider;

public class FileVersionInformationImpl implements FileVersionInformation {

	private CopyOnWriteArrayList<FileVersionInformationProvider> providers;

	public FileVersionInformationImpl(){
		providers = new CopyOnWriteArrayList<>();
	}
	
	public void addFileVersionInformationProvider(FileVersionInformationProvider versionProvider){
		providers.add(versionProvider);
	}
	
	public void removeFileVersionInformationProvider(FileVersionInformationProvider versionProvider){
		providers.remove(versionProvider);
	}

	@Override
	public Map<File, FileVersion> getFileVersions(List<File> files) {
		Map<File, FileVersion> versions = new HashMap<>(files.size());
		for(File file:files){
			versions.put(file, null);
		}
		for(FileVersionInformationProvider provider:providers){
			provider.getFileVersions(files, versions);
		}
		for(Entry<File, FileVersion> entry:versions.entrySet()){
			if(entry.getValue() == null){
				entry.setValue(new DefaultFileVersion(entry.getKey()));
			}
		}
		return versions;
	}
}
