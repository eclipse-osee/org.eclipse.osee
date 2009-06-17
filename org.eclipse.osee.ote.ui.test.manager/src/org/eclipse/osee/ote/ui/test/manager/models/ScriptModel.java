/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.models;

import java.io.File;
import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.svn.VersionControl;
import org.eclipse.osee.framework.svn.entry.IRepositoryEntry;
import org.eclipse.osee.framework.ui.plugin.util.AJavaProject;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.config.ScriptVersionConfig;

public class ScriptModel extends FileModel {


	public enum ScriptInteractionEnum {
		BATCH, MANUAL, UNKNOWN
	}

	public class TestFileData {
		public String absoluteFilePath = null;
		public String classPath = null; 
		public String error = "";
		public String name = null;
		public String outFile = null;
		public String projectPath = null;
		public String rawFileName;

		public ScriptVersionConfig getVersionInfo() {
			ScriptVersionConfig scriptVersion = new ScriptVersionConfig();
			File javaFile = new File(rawFileName);
			if (javaFile != null && javaFile.exists() && javaFile.canRead()) {
				IRepositoryEntry entry = VersionControl.getInstance()
						.getRepositoryEntry(javaFile);
				if (entry != null) {
					scriptVersion.setRevision(entry.getVersion());
					scriptVersion.setLocation(entry.getURL());
					scriptVersion.setRepositoryType(entry
							.getVersionControlSystem());
					scriptVersion.setLastAuthor(entry.getLastAuthor());
					scriptVersion.setLastModificationDate(entry
							.getLastModificationDate());
					scriptVersion.setModifiedFlag(entry.getModifiedFlag());
				}
			} 
			return scriptVersion;
		}
	}

	private TestFileData javaFileData;
	private OutputModel outputModel;
	private TestScript testScript;
	

	/**
	 * @param rawFilename
	 * @param outputDir
	 *            alternate output directory for tmo output files null will
	 *            default to script directory
	 */
	public ScriptModel(String rawFilename, String outputDir) {
		super(rawFilename);
		javaFileData = new TestFileData();
		javaFileData = getSunData(outputDir);
		javaFileData.rawFileName = rawFilename;
		outputModel = new OutputModel(javaFileData.outFile);
	}

	public ScriptInteractionEnum getInteraction() {
		if (testScript == null)
			return ScriptInteractionEnum.UNKNOWN;
		if (testScript.isBatchable())
			return ScriptInteractionEnum.BATCH;
		else
			return ScriptInteractionEnum.MANUAL;
	}

	/**
	 * @return Returns the outputModel.
	 */
	public OutputModel getOutputModel() {
		outputModel.setRawFilename(javaFileData.outFile);
		return outputModel;

	}

	/**
	 * @param alternateOutputDir
	 *            place output files here instead of at location of the script
	 * @return Returns sun data.
	 */
	private TestFileData getSunData(String alternateOutputDir) {
		javaFileData.absoluteFilePath = getRawFilename();
		String temp = null;
		if (javaFileData.absoluteFilePath.endsWith(".java")) {
		   temp = AJavaProject.getClassName(this.getRawFilename());
		} 
		javaFileData.name = ((temp == null) ? new File(getRawFilename()).getName() : temp);
		javaFileData.classPath = "";
		alternateOutputDir = alternateOutputDir.trim();
		if (alternateOutputDir == null || alternateOutputDir.equals("")) {
			javaFileData.outFile = javaFileData.absoluteFilePath.replaceFirst(".java$", ".tmo");
			if (!javaFileData.outFile.endsWith(".tmo")) {
				javaFileData.outFile += ".tmo";
			}
		} else {
			try{
   			File dir = new File(alternateOutputDir);
   			if (dir.exists() && dir.isDirectory()) {
   				javaFileData.outFile = alternateOutputDir;
   			} else {
   			   if(getIFile() != null){
      				IProject project = getIFile().getProject();
      				IFolder folder = project.getFolder(alternateOutputDir);
   					if(!folder.exists()){
   						ContainerCreator containerCreator = new ContainerCreator(folder.getWorkspace(), folder.getFullPath());
   						containerCreator.createContainer(new NullProgressMonitor());
   					}
   					javaFileData.outFile = folder.getLocation().toFile().getAbsolutePath();
   			   }
   			}
   
   			javaFileData.outFile += File.separator;
   			javaFileData.outFile += getName();
   			javaFileData.outFile = javaFileData.outFile.replaceFirst(".java$",".tmo");
   			if (!javaFileData.outFile.endsWith(".tmo")) {
   				javaFileData.outFile += ".tmo";
   			}
			} catch (CoreException ex){
				ex.printStackTrace();
				javaFileData.outFile = javaFileData.absoluteFilePath.replaceFirst(".java$", ".tmo");
				if (!javaFileData.outFile.endsWith(".tmo")) {
					javaFileData.outFile += ".tmo";
				}
			}
		}
//		outputModel = new OutputModel(javaFileData.outFile);
		
		
//		OseeLog.log(TestManagerPlugin.class, Level.FINE, "javaFileData.absoluteJavaPath *"+ javaFileData.absoluteFilePath + "*");
//	   OseeLog.log(TestManagerPlugin.class, Level.FINE, "javaFileData.className *" + javaFileData.name + "*");
//	   OseeLog.log(TestManagerPlugin.class, Level.FINE, "javaFileData.classPath *" + javaFileData.classPath + "*");
//		OseeLog.log(TestManagerPlugin.class, Level.FINE, "javaFileData.outFile *" + javaFileData.outFile+ "*");

		return javaFileData;
	}

	public TestFileData updateScriptModelInfo(String alternateOutputDir){
	   TestFileData javaFileData = getSunData(alternateOutputDir);
	   outputModel = new OutputModel(javaFileData.outFile);
	   return javaFileData;
	}
	
	public String getTestClass(){
		return javaFileData.name;
	}
	
	/**
	 * @param outputModel
	 *            The outputModel to set.
	 */
	public void setOutputModel(OutputModel outputModel) {
		this.outputModel = outputModel;
	}

}