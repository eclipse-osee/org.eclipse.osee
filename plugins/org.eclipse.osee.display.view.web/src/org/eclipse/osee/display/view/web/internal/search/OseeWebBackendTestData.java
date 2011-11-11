/*
 * Created on Sep 26, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.view.web.internal.search;


public class OseeWebBackendTestData {
   //   private final static OseeWebBackendTestData singleInstance = new OseeWebBackendTestData();
   //   private final static ProgramsAndBuilds builds = new ProgramsAndBuilds();
   //   private final static Map<String, Artifact> artifacts = new HashMap<String, Artifact>();
   //   private static boolean hasBeenInitialized = false;
   //
   //   private static void initProgramsAndBuilds() {
   //      Program blk3 = new Program("Blk 3", "blk3_guid");
   //      Program v131 = new Program("V13.1", "v131_guid");
   //      Program taiwan = new Program("Taiwan", "taiwan_guid");
   //
   //      builds.addBuilds(blk3, Arrays.asList(new Build("Baseline", "baseline_guid"), new Build("Bld_1", "bld_1_guid"),
   //         new Build("FTP0", "ftb0_guid")));
   //      builds.addBuilds(v131,
   //         Arrays.asList(new Build("FTB1", "FTB1_guid"), new Build("FTB2", "FTB2_guid"), new Build("FTB3", "FTB3_guid")));
   //      builds.addBuilds(taiwan,
   //         Arrays.asList(new Build("EB0", "EB0_guid"), new Build("EB1", "EB1_guid"), new Build("PIDS", "PIDS_guid")));
   //   }
   //
   //   private static void initArtifacts() {
   //      Artifact defaultroot = new Artifact("defaultHierarchRoot_GUID", "Default Hierarchy Root", "Root Artifact", null);
   //
   //      Map<RelationType, Collection<Artifact>> swreqsRelations = new HashMap<RelationType, Collection<Artifact>>();
   //      swreqsRelations.put(RelationType.PARENT, Arrays.asList(defaultroot));
   //      Artifact swreqs = new Artifact("SWReq_GUID", "Software Requirements", "Folder", swreqsRelations);
   //
   //      Map<RelationType, Collection<Artifact>> crewIntRelations = new HashMap<RelationType, Collection<Artifact>>();
   //      crewIntRelations.put(RelationType.PARENT, Arrays.asList(swreqs));
   //      Artifact crewIntreqs = new Artifact("CrewInt_GUID", "Crew Interface", "Folder", crewIntRelations);
   //
   //      Map<RelationType, Collection<Artifact>> commSubSysCrewIntRelations =
   //         new HashMap<RelationType, Collection<Artifact>>();
   //      commSubSysCrewIntRelations.put(RelationType.PARENT, Arrays.asList(crewIntreqs));
   //      Artifact commSubSysCrewIntreqs =
   //         new Artifact("commSubSysCrewInt_GUID", "Communication Subsystem Crew Interface", "Heading",
   //            commSubSysCrewIntRelations);
   //
   //      Map<RelationType, Collection<Artifact>> comm_page_Relations = new HashMap<RelationType, Collection<Artifact>>();
   //      comm_page_Relations.put(RelationType.PARENT, Arrays.asList(commSubSysCrewIntreqs));
   //      Artifact comm_page_Intreqs =
   //         new Artifact("com_page_GUID", "{COM_PAGE}", "Software Requirement", comm_page_Relations);
   //
   //      artifacts.put(defaultroot.getGuid(), defaultroot);
   //      artifacts.put(swreqs.getGuid(), swreqs);
   //      artifacts.put(crewIntreqs.getGuid(), crewIntreqs);
   //      artifacts.put(commSubSysCrewIntreqs.getGuid(), commSubSysCrewIntreqs);
   //      artifacts.put(comm_page_Intreqs.getGuid(), comm_page_Intreqs);
   //   }
   //
   //   public OseeWebBackendTestData() {
   //   }
   //
   //   public static OseeWebBackendTestData getSingleinstance() {
   //      if (!hasBeenInitialized) {
   //         initProgramsAndBuilds();
   //         initArtifacts();
   //         hasBeenInitialized = true;
   //      }
   //      return singleInstance;
   //   }
   //
   //   public static ProgramsAndBuilds getBuilds() {
   //      return builds;
   //   }
   //
   //   public Collection<Build> getBuildsWithProgram(Program program) {
   //      return builds.getBuilds(program);
   //   }
   //
   //   public Collection<Program> getPrograms() {
   //      return builds.getPrograms();
   //   }
   //
   //   public Map<String, Artifact> getArtifacts() {
   //      return artifacts;
   //   }
   //
   //   public Artifact getArtifactWithGuid(String artifactGuid) {
   //      if (artifacts == null || artifacts.size() <= 0) {
   //         return null;
   //      }
   //      return artifacts.get(artifactGuid);
   //   }
   //
   //   public Program getProgramForArtUuid(String artifactUuid) {
   //      Collection<Program> programs = builds.getPrograms();
   //      Program[] programsArray = (Program[]) programs.toArray();
   //      if (programsArray.length > 0) {
   //         return programsArray[1];
   //      } else {
   //         return null;
   //      }
   //   }
   //
   //   public Build getBuildForArtUuid(String artifactUuid) {
   //      Program program = getProgramForArtUuid(artifactUuid);
   //      if (program != null) {
   //         Collection<Build> local_builds = builds.getBuilds(program);
   //         Build[] buildsArray = (Build[]) local_builds.toArray();
   //         if (buildsArray.length > 0) {
   //            return buildsArray[1];
   //         } else {
   //            return null;
   //         }
   //      } else {
   //         return null;
   //      }
   //   }
}
