package org.eclipse.osee.ote.version;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface FileVersionInformationProvider {
	FileVersion getFileVersion(File file);
	void getFileVersions(List<File> files, Map<File, FileVersion> versions);
}
