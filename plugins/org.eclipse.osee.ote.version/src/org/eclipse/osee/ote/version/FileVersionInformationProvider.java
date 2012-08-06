package org.eclipse.osee.ote.version;

import java.io.File;

public interface FileVersionInformationProvider {
	FileVersion getFileVersion(File file);
}
