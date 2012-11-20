package org.eclipse.osee.ote.version.svn;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformationProvider;
import org.eclipse.team.svn.core.connector.SVNEntryInfo;
import org.eclipse.team.svn.core.utility.SVNUtility;

public class SvnVersionProvider implements FileVersionInformationProvider {


	@Override
	public FileVersion getFileVersion(File file) {
		if (isSvn(file)) {
			SVNEntryInfo entry = SVNUtility.getSVNInfo(file);
			if(entry != null){
				return new SvnFileVersion(entry);
			}
		}
		return null;
	}

	protected boolean isSvn(File file) {
		File svn = new File(file.getParentFile(), SVNUtility.getSVNFolderName());
		return svn.exists();
	}

	@Override
	public void getFileVersions(List<File> files, Map<File, FileVersion> versions) {
		for(File file:files){
			FileVersion version = getFileVersion(file);
			if(version != null){
				versions.put(file, version);
			}
		}
	}

}
