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
package org.eclipse.osee.ats.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.change.ViewWordChangeReportHandler;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ExportChangeReportsAction extends Action {
   private final WorldEditor worldEditor;
   private final List<TeamWorkFlowArtifact> workflows = new ArrayList<TeamWorkFlowArtifact>();
   private final boolean reverse = false;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      setText("Export Change Report(s)");
      setImageDescriptor(getImageDescriptor());
      this.worldEditor = worldEditor;
      workflows.clear();
   }

   public List<TeamWorkFlowArtifact> getWorkflows() throws OseeCoreException {
      Collection<String> legacyIds =
            Arrays.asList(new String[] {"10014", "10015", "10016", "10018", "10019", "10021", "10023", "10026",
                  "10033", "10035", "10042", "10043", "10048", "10050", "10052", "10053", "10054", "10059", "10062",
                  "10063", "10065", "10066", "10070", "10074", "10076", "10077", "10081", "10082", "10083", "10084",
                  "10085", "10089", "10092", "10093", "10094", "10095", "10100", "10101", "10104", "10106", "10107",
                  "10108", "10109", "10111", "10112", "10113", "10115", "10119", "10123", "10125", "10126", "10127",
                  "10128", "10129", "10130", "10133", "10137", "10144", "10145", "10146", "10147", "10149", "10150",
                  "10151", "10152", "10153", "10173", "10174", "10175", "10176", "10177", "10178", "10179", "10180",
                  "10186", "10188", "10190", "10203", "10205", "10207", "10214", "10216", "10217", "10222", "10225",
                  "10226", "10227", "10228", "10229", "10230", "10231", "10232", "10233", "10234", "10235", "10236",
                  "10239", "10240", "10241", "10247", "10248", "10249", "10250", "10251", "10252", "10253", "10254",
                  "10255", "10261", "10262", "10263", "10266", "10267", "10268", "10269", "10271", "10273", "10276",
                  "10277", "10279", "10285", "10286", "10287", "10288", "10289", "10291", "10293", "10294", "10295",
                  "10296", "10298", "10301", "10305", "10308", "10309", "10310", "10311", "10312", "10313", "10316",
                  "10319", "10322", "10325", "10328", "10330", "10334", "10339", "10341", "10344", "10347", "10348",
                  "10353", "10354", "10355", "10356", "10357", "10358", "10363", "10364", "10367", "10368", "10370",
                  "10371", "10374", "10376", "10384", "10385", "10388", "10389", "10393", "10394", "10400", "10401",
                  "10402", "10403", "10404", "10405", "10407", "10408", "10410", "10411", "10413", "10417", "10419",
                  "10425", "10426", "10431", "10433", "10435", "10436", "10440", "10441", "10445", "10450", "10452",
                  "10453", "10454", "10455", "10456", "10457", "10458", "10460", "10461", "10462", "10466", "10467",
                  "10469", "10470", "10472", "10475", "10476", "10479", "10480", "10481", "10482", "10483", "10489",
                  "10491", "10492", "10493", "10495", "10497", "10500", "10508", "10517", "10519", "10563", "10564",
                  "10566", "10567", "10580", "10581", "10587", "10588", "10591", "10595", "10596", "10597", "10598",
                  "10624", "10630", "10632", "10637", "10648", "10649", "10650", "10651", "10652", "10653", "10656",
                  "10657", "10658", "10668", "10680", "10690", "10698", "10700", "10701", "10702", "10703", "10705",
                  "10706", "10720", "10737", "10738", "10739", "10741", "10748", "10754", "10759", "10762", "10764",
                  "10765", "10767", "10770", "10771", "10773", "10774", "10775", "10777", "10781", "10783", "10792",
                  "10794", "10796", "10800", "10801", "10802", "10803", "10804", "10842", "10847", "10854", "10865",
                  "10868", "10871", "10872", "10873", "10899", "10900", "10901", "10902", "10903", "10904", "10905",
                  "10907", "10908", "10916", "10917", "10918", "10921", "10923", "10924", "10927", "10930", "10943",
                  "10945", "10946", "10949", "10953", "10954", "10972", "10973", "10974", "10975", "10976", "10978",
                  "10979", "10980", "10982", "10983", "10984", "10985", "10986", "10990", "11004", "11006", "11008",
                  "11009", "11010", "11011", "11012", "11016", "11017", "11019", "11020", "11024", "11025", "11026",
                  "11034", "11035", "11041", "11048", "11052", "11053", "11057", "11063", "11064", "11067", "11076",
                  "11077", "11080", "11082", "11090", "11093", "11094", "11095", "11096", "11097", "11098", "11100",
                  "11101", "11102", "11103", "11104", "11105", "11106", "11107", "11112", "11113", "11119", "11120",
                  "11121", "11122", "11123", "11124", "11125", "11126", "11127", "11128", "11135", "11137", "11138",
                  "11139", "11140", "11141", "11142", "11144", "11145", "11148", "11149", "11150", "11153", "11161",
                  "11163", "11172", "11188", "11191", "11201", "11203", "11206", "11207", "11210", "11212", "11219",
                  "11220", "11225", "11226", "11228", "11229", "11241", "11245", "11246", "11248", "11249", "11250",
                  "11251", "11253", "11256", "11260", "11262", "11268", "11269", "11270", "11272", "11273", "11277",
                  "11278", "11281", "11286", "11288", "11290", "11296", "11297", "11298", "11306", "11307", "11313",
                  "11315", "11317", "11320", "11322", "11324", "11330", "11331", "11333", "11340", "11341", "11342",
                  "11344", "11346", "11348", "11350", "11351", "11354", "11355", "11358", "11359", "11360", "11361",
                  "11366", "11367", "11368", "11370", "11371", "11377", "11378", "11379", "11381", "11384", "11385",
                  "11388", "11389", "11390", "11391", "11392", "11393", "11394", "11395", "11396", "11397", "11398",
                  "11399", "11400", "11404", "11405", "11406", "11410", "11411", "11413", "11417", "11421", "11422",
                  "11424", "11426", "11427", "11428", "11429", "11430", "11431", "11432", "11433", "11434", "11438",
                  "11439", "11440", "11441", "11442", "11443", "11444", "11445", "11446", "11447", "11448", "11449",
                  "11450", "11453", "11454", "11457", "11459", "11460", "11463", "11465", "11466", "11467", "11470",
                  "11471", "11472", "11473", "11474", "11480", "11481", "11482", "11483", "11489", "11490", "11491",
                  "11492", "11494", "11497", "11503", "11506", "11507", "11512", "11513", "11516", "11520", "11522",
                  "11523", "11531", "11533", "11534", "11543", "11544", "11545", "11549", "11561", "11567", "11569",
                  "11571", "11574", "11577", "11578", "11579", "11586", "11587", "11588", "11591", "11592", "11593",
                  "11594", "11602", "11603", "11605", "11609", "11617", "11647", "11649", "11684", "11707", "11713",
                  "11715", "11717", "11721", "11722", "11726", "11729", "11730", "11731", "11732", "11733", "11734",
                  "11735", "11736", "11739", "11740", "11747", "11748", "11749", "11750", "11770", "11775", "11784",
                  "11785", "11788", "11789", "11790", "11791", "11792", "11793", "11794", "11795", "11796", "11797",
                  "11798", "11802", "11803", "11804", "11805", "11806", "11807", "11808", "11812", "11813", "11814",
                  "11819", "11822", "11825", "11826", "11827", "11838", "11840", "11841", "11842", "11843", "11844",
                  "11845", "11848", "11849", "11852", "11855", "11858", "11859", "11860", "11861", "11862", "11866",
                  "11868", "11870", "11871", "11876", "11877", "11884", "11886", "11889", "11892", "11893", "11895",
                  "11902", "11903", "11904", "11906", "11913", "11925", "11929", "11931", "11940", "11944", "11945",
                  "11946", "11951", "11960", "11963", "11964", "11967", "11969", "11978", "11983", "11986", "11990",
                  "11991", "11995", "11997", "12001", "12002", "12003", "12006", "12008", "12014", "12015", "12024",
                  "12025", "12027", "12029", "12030", "12031", "12035", "12036", "12037", "12043", "12045", "12050",
                  "12053", "12055", "12057", "12058", "12062", "12064", "12066", "12067", "12070", "12072", "12083",
                  "12090", "12093", "12095", "12096", "12099", "12105", "12107", "12108", "12109", "12110", "12116",
                  "12127", "12130", "12131", "12138", "12139", "12140", "12144", "12146", "12152", "12163", "12174",
                  "12183", "12189", "12194", "8684", "8685", "8686", "8687", "8688", "8689", "8694", "8798", "8800",
                  "8804", "8806", "8807", "8808", "8810", "8816", "8822", "8992", "9020", "9112", "9123", "9125",
                  "9130", "9131", "9132", "9176", "9203", "9204", "9205", "9215", "9216", "9227", "9229", "9230",
                  "9231", "9232", "9233", "9234", "9270", "9298", "9301", "9302", "9311", "9312", "9313", "9331",
                  "9336", "9340", "9341", "9342", "9343", "9344", "9345", "9346", "9347", "9348", "9349", "9350",
                  "9351", "9352", "9353", "9354", "9355", "9356", "9357", "9358", "9359", "9360", "9361", "9362",
                  "9363", "9364", "9365", "9366", "9367", "9368", "9369", "9370", "9371", "9372", "9373", "9375",
                  "9376", "9377", "9378", "9379", "9380", "9381", "9382", "9383", "9384", "9386", "9387", "9388",
                  "9390", "9393", "9394", "9395", "9396", "9397", "9398", "9399", "9402", "9403", "9404", "9405",
                  "9406", "9407", "9408", "9409", "9410", "9411", "9417", "9418", "9419", "9421", "9422", "9423",
                  "9424", "9425", "9427", "9429", "9430", "9434", "9435", "9441", "9445", "9448", "9449", "9453",
                  "9457", "9458", "9459", "9460", "9462", "9463", "9464", "9465", "9466", "9467", "9468", "9469",
                  "9470", "9471", "9472", "9473", "9474", "9475", "9476", "9477", "9478", "9479", "9480", "9481",
                  "9484", "9487", "9489", "9490", "9491", "9492", "9493", "9501", "9502", "9503", "9505", "9506",
                  "9507", "9508", "9509", "9510", "9511", "9512", "9513", "9514", "9515", "9516", "9517", "9519",
                  "9520", "9521", "9523", "9524", "9525", "9534", "9538", "9539", "9543", "9557", "9565", "9568",
                  "9569", "9571", "9583", "9584", "9589", "9592", "9595", "9597", "9602", "9611", "9612", "9620",
                  "9626", "9629", "9630", "9631", "9633", "9637", "9638", "9639", "9640", "9646", "9649", "9651",
                  "9653", "9655", "9656", "9662", "9663", "9676", "9679", "9695", "9707", "9722", "9724", "9741",
                  "9744", "9748", "9749", "9760", "9761", "9762", "9764", "9766", "9767", "9768", "9769", "9770",
                  "9771", "9772", "9775", "9776", "9777", "9778", "9779", "9782", "9783", "9791", "9793", "9803",
                  "9812", "9813", "9816", "9819", "9823", "9831", "9834", "9837", "9838", "9839", "9840", "9841",
                  "9842", "9847", "9848", "9849", "9850", "9851", "9858", "9865", "9866", "9867", "9868", "9869",
                  "9906", "9907", "9908", "9912", "9914", "9921", "9922", "9924", "9927", "9928", "9933", "9934",
                  "9935", "9936", "9937", "9939", "9941", "9942", "9943", "9944", "9950", "9951", "9954", "9963",
                  "9966", "9967", "9969", "9973", "9976", "9986", "9987", "9989", "9990", "9997"

            });

      if (workflows.isEmpty()) {
         List<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.LegacyPCRId, legacyIds,
                     CoreBranches.COMMON, legacyIds.size());
         for (Artifact artifact : artifacts) {
            if (artifact.getArtifactType().getGuid().equals("AAMFDjZ1UVAQTXHk2GgA")) {
               workflows.add((TeamWorkFlowArtifact) artifact);
            }
         }
         Collections.sort(workflows);
         if (reverse) {
            Collections.reverse(workflows);
         }
      }
      return workflows;
      //return worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts();
   }

   @Override
   public void run() {

      try {
         export();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private TransactionRecord pickTransaction(IArtifact workflow) throws OseeCoreException {
      int minTransactionId = -1;
      for (TransactionRecord transaction : TransactionManager.getCommittedArtifactTransactionIds(workflow)) {
         if (minTransactionId < transaction.getId()) {
            minTransactionId = transaction.getId();
         }
      }
      if (minTransactionId == -1) {
         throw new OseeStateException("no transaction records found for " + workflow);
      }
      return TransactionManager.getTransactionId(minTransactionId);
   }

   private void export() throws OseeCoreException {
      ViewWordChangeReportHandler handler = new ViewWordChangeReportHandler();

      for (TeamWorkFlowArtifact workflow : getWorkflows()) {

         AtsBranchManager atsBranchMgr = workflow.getBranchMgr();
         IProgressMonitor monitor = new NullProgressMonitor();
         Collection<Change> changes = null;
         if (atsBranchMgr.isCommittedBranchExists()) {
            changes = ChangeManager.getChangesPerTransaction(pickTransaction(workflow), monitor);
         } else {
            Branch branch = atsBranchMgr.getWorkingBranch();
            if (atsBranchMgr.isWorkingBranchInWork()) {
               changes = ChangeManager.getChangesPerBranch(branch, monitor);
            }
         }
         if (changes != null) {
            handler.viewWordChangeReport(changes, true, workflow.getSoleAttributeValueAsString(
                  AtsAttributeTypes.LegacyPCRId, null));
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA);
   }

   public void updateEnablement() throws OseeCoreException {
      setEnabled(getWorkflows().size() > 0);
   }
}
