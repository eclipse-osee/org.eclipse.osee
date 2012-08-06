package org.eclipse.osee.ote.version.internal;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformation;
import org.eclipse.osee.ote.version.FileVersionInformationProvider;

public class FileVersionInformationImpl implements FileVersionInformation {

	private CopyOnWriteArrayList<FileVersionInformationProvider> providers;

	public FileVersionInformationImpl(){
		providers = new CopyOnWriteArrayList<FileVersionInformationProvider>();
	}
	
	public void addFileVersionInformationProvider(FileVersionInformationProvider versionProvider){
		providers.add(versionProvider);
	}
	
	public void removeFileVersionInformationProvider(FileVersionInformationProvider versionProvider){
		providers.remove(versionProvider);
	}

	@Override
	public FileVersion getFileVersion(File file) {
		FileVersion version = null;
		for(FileVersionInformationProvider provider:providers){
			version = provider.getFileVersion(file);
			if(version != null){
				return version;
			}
		}
		return new DefaultFileVersion(file);
	}
}
