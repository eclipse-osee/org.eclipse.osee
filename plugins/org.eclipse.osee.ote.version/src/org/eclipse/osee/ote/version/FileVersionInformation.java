package org.eclipse.osee.ote.version;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FileVersionInformation {
	
	FileVersion getFileVersion(File file);
	
	Map<File, FileVersion> getFileVersions(List<File> files);
	
}
