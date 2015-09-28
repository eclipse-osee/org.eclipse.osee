package org.eclipse.osee.ote.ui.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.osgi.service.prefs.Preferences;

public class OTEPackagingBuilder extends IncrementalProjectBuilder {

   public static final String BUILDER_ID = "org.eclipse.osee.ote.ui.builder.OTEPackagingBuilder";

   private static final String ROOT_FOLDER_NAME = "OTE";
   private static final String JAR_FOLDER = "workspacejars";
   
   public static boolean isOTEBuilderActive(){
      if(Activator.getDefault() != null && Activator.getDefault().getPreferenceStore() != null){
         Preferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
         return prefs.getBoolean(OTEBuilderPreferencePage.DO_JAR_PACKAGING, false);
      } else {
         return false;
      }
   }
   
   public static File getWorkspaceArchiveFolder(){
      File oteFolder = OseeData.getFile(ROOT_FOLDER_NAME);
      File workspaceArchives = new File(oteFolder, JAR_FOLDER);
      workspaceArchives.mkdirs();
      return workspaceArchives;
   }
   
   public OTEPackagingBuilder(){
   }
   
	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		
	   
	   private ArchiveBuilder builder;

      public SampleDeltaVisitor(ArchiveBuilder builder) {
         this.builder = builder;
      }

      /*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		@Override
      public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkResource(resource, builder);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkResource(resource, builder);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
	   
	   private ArchiveBuilder builder;
	   
		public SampleResourceVisitor(ArchiveBuilder builder) {
         this.builder = builder;
      }

      @Override
      public boolean visit(IResource resource) {
			checkResource(resource, builder);
			//return true to continue visiting children.
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
   @SuppressWarnings("rawtypes")
   protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	   try{
	      if(!isOTEBuilderActive()){
	         return null;
	      }
	      long time = System.currentTimeMillis();
	      IJavaProject javaProject = JavaCore.create(getProject());
	      if(javaProject != null){
	         File workspaceArchiveHome = getWorkspaceArchiveFolder();
	         List<IPath> outputLocations = getOutputLocations(javaProject.getOutputLocation(), javaProject.getRawClasspath());

	         ArchiveBuilder builder = new ArchiveBuilder(workspaceArchiveHome, outputLocations);
	        
	         if (kind == FULL_BUILD) {
	            fullBuild(monitor, builder);
	         } else {
	            IResourceDelta delta = getDelta(getProject());
	            if (delta == null) {
	               fullBuild(monitor, builder);
	            } else {
	               incrementalBuild(delta, monitor, builder);
	            }
	         } 
	         builder.archive();
	      }
	      long elapsed = System.currentTimeMillis() - time;
	      System.out.printf("%s,%d\n", getProject().getName(), elapsed);
	   } catch (Throwable th){
	      OseeLog.log(getClass(), Level.SEVERE, "Failed to run the OTE Packager", th);
	      th.printStackTrace();
	   }
	   return null;
	}

   private List<IPath> getOutputLocations(IPath outputLocation, IClasspathEntry[] rawClasspath) {
      List<IPath> outputLocations = new ArrayList<>();
      outputLocations.add(outputLocation);
      for(IClasspathEntry classpathEntry:rawClasspath){
         if(classpathEntry.getOutputLocation() != null){
            outputLocations.add(classpathEntry.getOutputLocation());
         }
      }
      return outputLocations;
   }

   void checkResource(IResource resource, ArchiveBuilder builder) {
		if (resource instanceof IFile && resource.getName().endsWith(".class")) {
			builder.addFile(resource);
		}
	}

	protected void fullBuild(final IProgressMonitor monitor, ArchiveBuilder builder) throws CoreException {
		try {
			getProject().accept(new SampleResourceVisitor(builder));
		} catch (CoreException e) {
		}
	}

	protected void incrementalBuild(IResourceDelta delta,	IProgressMonitor monitor, ArchiveBuilder builder) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor(builder));
	}
}
