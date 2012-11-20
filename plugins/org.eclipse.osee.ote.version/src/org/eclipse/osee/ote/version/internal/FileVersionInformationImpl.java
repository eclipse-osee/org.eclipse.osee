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

	@Override
	public Map<File, FileVersion> getFileVersions(List<File> files) {
		Map<File, FileVersion> versions = new HashMap<File, FileVersion>(files.size());
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
