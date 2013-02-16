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
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitVersion extends GitVersionBase {

	private final File file;
	
	public GitVersion(File file){
		this.file = file;
	}
	
	public String getURL() throws MalformedURLException{
		return this.file.toURI().toURL().toString();
	}
	
	public boolean getModified() throws IOException, NoWorkTreeException, GitAPIException{
		if(!file.exists()){
			return false;
		}
		File gitFolder = findGitDirUp(file);
		if(gitFolder == null){
			return false;
		}
		Repository repository = buildRepository(gitFolder);
		Git git = new Git(repository);
		StatusCommand status = git.status();
		String pathFilter = getPathFilterFromFullPathAndGitFolder(file, gitFolder);
		Status result = status.call();
		Set<String> modified = result.getModified();
		if(modified.contains(pathFilter)){
			return true;
		} else {
			return false;
		}
	}
	
	public  RevCommit getLastCommit() throws IOException, NoHeadException, GitAPIException{
		if(!file.exists()){
			return null;
		}
		File gitFolder = findGitDirUp(file);
		if(gitFolder == null){
			return null;
		}
		Repository repository = buildRepository(gitFolder);
		Git git = new Git(repository);
		LogCommand log = git.log();
		String pathFilter = getPathFilterFromFullPathAndGitFolder(file, gitFolder);
		log.addPath(pathFilter);
		Iterable<RevCommit> iterable = log.call();
		Iterator<RevCommit> it = iterable.iterator();
		if(it.hasNext()){
			return it.next(); 
		} else {
			return null;
		}
	}
}
