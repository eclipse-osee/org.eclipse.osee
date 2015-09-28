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
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class GitVersionTest {

	private static final String GIT_REPO_FILTER = "";
	private static final String GIT_REPO_FOLDER = "";
	
	private static final String validGitFile = "";
	private static final String validModifiedGitFile = "";
	private static final String invalidGitFile = "";
	private static final String invalidNoGitFile = "";
	
	private long start;
	private long end;

	@Ignore
	@Test
	public void testGetLastCommit() throws NoHeadException, IOException, GitAPIException {
		GitVersion gitVersion = new GitVersion(new File(validGitFile));

		RevCommit commit = gitVersion.getLastCommit();
		Assert.assertNotNull(commit);
		System.out.println(commit.getCommitterIdent().getName());
		System.out.println(commit.getCommitterIdent().getWhen());
		System.out.println(commit.getId().getName());
	}

	@Ignore
	@Test
	public void testGetLastCommitFailFile() throws NoHeadException, IOException, GitAPIException {
		GitVersion gitVersion = new GitVersion(new File(invalidGitFile));
		RevCommit commit = gitVersion.getLastCommit();
		Assert.assertNull(commit);
	}

	@Ignore
	@Test
	public void testGetLastCommitFailRepo() throws NoHeadException, IOException, GitAPIException {
		GitVersion gitVersion = new GitVersion(new File(invalidNoGitFile));
		RevCommit commit = gitVersion.getLastCommit();
		Assert.assertNull(commit);
	}

	@Ignore
	@Test
	public void testModified() throws NoHeadException, IOException, GitAPIException{
		GitVersion gitVersion = new GitVersion(new File(validGitFile));
		Assert.assertFalse(gitVersion.getModified());

		gitVersion = new GitVersion(new File(invalidNoGitFile));
		Assert.assertFalse(gitVersion.getModified());

		gitVersion = new GitVersion(new File(invalidGitFile));
		Assert.assertFalse(gitVersion.getModified());

		gitVersion = new GitVersion(new File(validModifiedGitFile));
		Assert.assertTrue(gitVersion.getModified());

	}
	
	private List<File> getFiles(){
		File repo = new File(GIT_REPO_FOLDER);
		File[] wpsfolders = repo.listFiles(new FileFilter(){
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() && arg0.getName().contains(GIT_REPO_FILTER);
			}
			
		});
		List<File> files = new ArrayList<>(200);
		for(File folder:wpsfolders){
			getJavaFiles(folder, files);
		}
		return files;
	}
	
	private void getJavaFiles(File folder, List<File> files){
		if(folder.isDirectory()){
			for(File file:folder.listFiles()){
				if(file.isDirectory()){
					getJavaFiles(file, files);
				} else if(file.getAbsolutePath().endsWith(".java")){
					files.add(file);
				}
			}
		}
	}

	@Test
	public void testTime() throws NoHeadException, IOException, GitAPIException{
		List<File> files = getFiles();
		System.out.println(files.size());
		startTimer();
		for(File timedGitFile: files){
			GitVersion gitVersion = new GitVersion(timedGitFile);
			RevCommit commit = gitVersion.getLastCommit();
//			System.out.println(commit.getId());
		}
		endTimer();
		System.out.println(getElapsedTimeInSec());

		GitVersions gitVersions = new GitVersions(files);
		startTimer();
		Map<File, RevCommit> commits = gitVersions.getLastCommits();
//		for(File timedGitFile: files){
//			System.out.println(commits.get(timedGitFile).getId());
//		}
		endTimer();
		System.out.println(getElapsedTimeInSec());

	}

	private void startTimer(){
		start = System.nanoTime();
	}

	private void endTimer(){
		end = System.nanoTime();
	}

	private long getElapsedTime(){
		return end - start;
	}

	private String getElapsedTimeInSec(){
		return String.format("%f03", (((float)getElapsedTime())/1000000000f));
	}

}
