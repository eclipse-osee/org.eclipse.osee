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
package org.eclipse.osee.ote.version.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitVersions extends GitVersionBase {

	private List<File> files;

	public GitVersions(List<File> files) {
		this.files = files;
	}
	
	
	public  Map<File, RevCommit> getLastCommits() {
		Map<File, RevCommit> commits = new HashMap<>();
		Map<File, List<File>> gitToFiles = new HashMap<>();
		for(File file:files){
			if(!file.exists()){
				continue;
			}
			File gitFolder = findGitDirUp(file);
			if(gitFolder == null){
				continue;
			}	
			List<File> gitfiles = gitToFiles.get(gitFolder);
			if(gitfiles == null){
				gitfiles = new ArrayList<>();
				gitToFiles.put(gitFolder, gitfiles);
			}
			gitfiles.add(file);
		}
		for(Entry<File, List<File>> entry:gitToFiles.entrySet()){
			try{
				Repository repository = buildRepository(entry.getKey());
				Git git = new Git(repository);

				for(File gitfile:entry.getValue()){
				    LogCommand log = git.log();
				    log.setMaxCount(1);
					String pathFilter = getPathFilterFromFullPathAndGitFolder(gitfile, entry.getKey());
					log.addPath(pathFilter);
					Iterable<RevCommit> iterable = log.call();
					Iterator<RevCommit> it = iterable.iterator();
					if(it.hasNext()){
						RevCommit commit = it.next();
						commits.put(gitfile, commit);
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			} catch (NoHeadException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
		}
		return commits;
	}
}
