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
package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItem extends XNavigateItemAction {

   /**
    * @param parent
    */
   public DoesNotWorkItem(XNavigateItem parent) {
      super(parent, "Does Not Work - convertAtsLogUserIds");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;

      //      for (Artifact art : ArtifactQuery.getArtifactsFromAttributeType("ats.Branch Id", AtsPlugin.getAtsBranch())) {
      //         int branchId = art.getSoleAttributeValue("ats.Branch Id");
      //         Branch branch = null;
      //         try {
      //            branch = BranchManager.getBranch(branchId);
      //         } catch (BranchDoesNotExist ex) {
      //            System.out.println("Branch does not exist for art " + art.getHumanReadableId() + " - " + art);
      //         } catch (Exception ex) {
      //            System.err.println("Exception getting branch for art " + art.getHumanReadableId() + " - " + art);
      //         }
      //         if (branch != null) {
      //            System.err.println("Branch DOES exist for art " + art.getHumanReadableId() + " - " + art);
      //         }
      //      }

      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      //      convertAtsLogUserIds(transaction);
      transaction.execute();

      //      deleteUnAssignedUserRelations();
      //      relateDonDunne();

      //      testDeleteAttribute();
      //      deleteNullUserAttributes();
      //      XNavigateItem item = AtsNavigateViewItems.getInstance().getSearchNavigateItems().get(1);
      //      System.out.println("Item " + item.getName());
      //      NavigateView.getNavigateView().handleDoubleClick(item);

      //      XResultData.runExample();

      // fixOseePeerReviews();

      AWorkbench.popup("Completed", "Complete");
   }

   private void convertAtsLogUserIds(SkynetTransaction transaction) throws OseeCoreException {
      List<String> hrids =
            Arrays.asList("NKYBF", "J1Z48", "ZY4W5", "U9H58", "9713S", "83XVW", "59B9X", "TQD1J", "UVM7U", "HZT73",
                  "C49Q5", "RHCPY", "MBCXV", "YJFKC", "2F461", "AGW15", "K6ZGD", "9W45V", "GG43L", "G2VTQ", "CVWFC",
                  "EXMT0", "W1TS8", "JM3RD", "7Q0W3", "P9DKR", "BR2RN", "Z6B0Z", "6KT6U", "HPQJX", "QN2K3", "W0VTD",
                  "LDJMH", "6PWYH", "T8B4K", "YTNLC", "9557A", "SQQ6T", "D82X9", "2P5GC", "YK58N", "LWVT1", "KCGSQ",
                  "5X2WL", "C8HWW");
      for (Artifact art : ArtifactQuery.getArtifactsFromIds(hrids, AtsPlugin.getAtsBranch())) {
         String str = art.getSoleAttributeValue(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), null);
         str = str.replaceAll("rj236c", "1779483");
         art.setSoleAttributeFromString(ATSAttributes.LOG_ATTRIBUTE.getStoreName(), str);
         art.persistAttributes(transaction);
      }
   }

   private void importTaskEstimatedHours() throws OseeCoreException {
      System.out.println("Processing " + hrids.size() + " hrids.");
      Map<String, String> hridHourMap = new HashMap<String, String>();
      List<String> justHrids = new ArrayList<String>();
      for (String str : hrids) {
         String strs[] = str.split(",");
         hridHourMap.put(strs[0], strs[1]);
         justHrids.add(strs[0]);
      }
      // bulk load
      List<Artifact> arts = ArtifactQuery.getArtifactsFromIds(justHrids, AtsPlugin.getAtsBranch());
      // set
      int x = 1;
      for (String hrid : hridHourMap.keySet()) {
         System.out.println("Processing " + x++ + "/" + hridHourMap.size() + "...");
         boolean found = false;
         for (Artifact art : arts) {
            if (hrid.equals(art.getHumanReadableId())) {
               System.out.println("setting value " + hridHourMap.get(hrid) + " on " + hrid + " for " + art);
               if (art instanceof TaskArtifact) {
                  art.setSoleAttributeFromString(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(),
                        hridHourMap.get(hrid));
                  art.persistAttributes();
               }
               found = true;
            }
         }
         if (!found) {
            System.err.println("can't find hrid " + hrid);
         }
      }
   }

   private final List<String> hrids =
         Arrays.asList("399T4,35", "6P2T3,35", "RHXXY,35", "V4248,35", "QWFR2,35", "PP650,35", "HJXNY,35", "5LTLK,35",
               "20FVD,14", "7RTPQ,14", "77693,14", "KN3S7,13", "KSC49,13", "GVDPM,13", "JYH60,13", "JX1S3,13",
               "8D73E,13", "MDFYU,13", "XHLV3,13", "2MB4S,13", "B9T99,13", "03QZB,151", "3RC9K,151", "Q114C,213",
               "0WRPY,213", "ANZTF,30", "XX2J0,30", "8WRM5,30", "BDZVF,30", "DPZ2Q,30", "GFM5W,30", "4TP2N,30",
               "K4NMA,30", "EMCVC,30", "T984F,30", "AL8W5,30", "2L071,30", "YDRJT,30", "E0F06,30", "X9GGG,30",
               "Q4QCG,30", "KVSSJ,30", "4NW5K,30", "19WW1,30", "MNC8N,30", "J1Z48,17", "U6W8U,17", "PJDYM,17",
               "VCR3P,17", "RC9QS,17", "WWPGB,17", "W2VQL,17", "ZYYQ6,17", "6KF0K,17", "PPH4Y,17", "0QS8W,17",
               "31T23,17", "SJVNJ,17", "U3R18,17", "1FGX1,17", "37S6D,17", "G2VTQ,17", "0KC60,17", "0G7QZ,17",
               "ES0KP,17", "G83T8,17", "FX1BN,17", "TGHQB,17", "TDWTL,17", "R97GY,17", "YTWDP,17", "FJ34Z,17",
               "F6SS2,17", "BXK88,17", "YLFBX,17", "P9DKR,17", "MJKJ8,17", "ZZV3W,17", "AFR90,17", "70GKC,17",
               "ZY4W5,22", "U9H58,22", "9713S,22", "83XVW,22", "59B9X,22", "TQD1J,22", "UVM7U,22", "HZT73,22",
               "C49Q5,22", "DJ0TL,22", "BZQW2,22", "CGV1R,22", "W0VTD,22", "T8B4K,22", "YTNLC,22", "D82X9,22",
               "TYDDH,22", "77W65,22", "YVGTP,22", "WNKFE,7", "6WZV3,7", "NSY6A,7", "SXNLT,7", "0NF9B,7", "7CDJA,7",
               "HB9MS,7", "S88PK,7", "UVRZT,7", "PCLCY,7", "2NP06,7", "V4N2T,7", "1KGR6,7", "75392,7", "FD0C5,7",
               "T2TFB,7", "WJ6NH,7", "4GDT0,7", "VH2B1,7", "W7JVA,7", "S8RQB,7", "L2T4K,7", "ARRM3,7", "J0N84,7",
               "TTJKJ,7", "5H84J,7", "6DKP0,7", "ZRVVZ,7", "LD2BR,7", "ZDLGA,7", "4RWSY,7", "21QHH,7", "0CYBA,7",
               "0H2N2,7", "1YHBK,7", "ZNC7P,7", "W6K3W,7", "N71XD,7", "6FNWS,7", "B1MDF,7", "TTVLB,7", "7J50J,7",
               "3WGY5,7", "CBL80,7", "BR01T,7", "3LFL0,7", "SCCR6,7", "ZGXH1,7", "7B10T,7", "2ZVFW,7", "NS0CZ,7",
               "R45F3,7", "7GBL2,7", "NLYFY,7", "V4HP0,7", "DF3G4,7", "0R2W1,7", "U15RW,7", "W43M4,7", "4HV1E,7",
               "J5QN7,7", "8NVX5,7", "X8BV5,7", "NMJZM,7", "01MM6,7", "AY7DC,7", "UM6QN,7", "03X73,151", "MNGRR,151",
               "HLCSP,13", "CP90F,13", "D5LF0,13", "GC2SE,13", "UWKDB,13", "D4V98,13", "51Q5N,13", "WWG9V,13",
               "6SQN0,13", "1PVN8,13", "4P3JS,13", "ZRQC3,13", "WSG86,13", "8Y55U,13", "UTD8U,13", "0SVJ7,13",
               "EPMSD,13", "APGFN,13", "8ZJJQ,13", "HWSBG,13", "21GD8,13", "PDX4S,13", "H9SLF,13", "KRP4H,13",
               "VDJY0,13", "MTHCL,13", "36V43,13", "B9YYN,13", "PS4Z8,13", "7BPBB,13", "BC32M,13", "1WJFK,13",
               "S4XGU,50", "VS6BW,50", "TQP3F,50", "TTS28,50", "ASGYK,50", "R1SBR,50", "8LNB0,50", "B6NHU,50",
               "MG5YL,50", "88ZH9,50", "Q6V9M,14", "SX750,14", "JZ8HA,14", "PBSVB,14", "XTQJG,14", "WSB6S,14",
               "LRBB5,14", "V1V0D,14", "8M0YA,14", "XF395,14", "VYW5R,14", "Y1P7S,14", "44GZ1,14", "AH3SH,14",
               "NGPMC,14", "8CKRA,14", "K6SMV,14", "5XRF9,14", "NCG43,14", "Z6FFY,14", "JLZZH,14", "6XG3B,14",
               "10WDV,14", "S7G2F,14", "QGJWD,14", "MDLRE,14", "D34T8,13", "T3DQF,13", "F912Y,13", "81FL9,13",
               "D9KJX,13", "FB7DB,13", "PPNVC,13", "YPPX8,13", "X5YMQ,13", "WZFLA,13", "87XPD,13", "FTDJ3,13",
               "QJ6NV,13", "E3Z97,13", "9L4XP,13", "9Y91S,13", "XP2C0,13", "H2YCT,13", "NVJPL,13", "TN43S,13",
               "RBDM8,13", "BYH8H,49", "BQDVW,49", "GW6PA,49", "D9DDT,29", "55T2F,29", "GYD62,29", "N08QD,29",
               "HN7YN,29", "U07B6,29", "4DGMF,29", "9ZZR7,29", "Z64PF,29", "AVCMB,29", "3Q50K,29", "7XCTX,29",
               "JR477,29", "1Q8QG,29", "8L8S6,29", "L9CVL,29", "NCXNS,29", "WDSB4,29", "1GMLX,29", "G30ZM,29",
               "2YV6K,29", "WQ901,29", "E7RB1,29", "3W1PK,29", "7T7RQ,29", "VS88V,29", "9YZNX,29", "1M98D,29",
               "GD6PP,29", "DN7T7,29", "UCVCT,29", "99CGN,1", "RRVX0,1", "DSL72,1", "7KJ75,1", "6G7VU,1", "4MGF5,1",
               "1894F,1", "KC0X8,1", "CZHJA,1", "0J5PF,1", "S87P9,1", "6LTBP,1", "3V56J,1", "VC0D1,1", "4HTZG,1",
               "BK1HA,11", "QZDBP,11", "F1SWC,11", "77J6W,11", "ZXKM4,11", "WS0CF,11", "KRV72,11", "LG4JB,11",
               "J523M,11", "FXRJ8,76", "1H51N,76", "SLQZA,76", "708D0,76", "C2400,6", "1WNCE,6", "L5RKX,6", "JH05U,6",
               "BPNZS,6", "1XYVD,6", "BN6WR,6", "25LFC,10", "99TK0,10", "WH2MC,10", "AH18V,10", "1X9RQ,10", "VZK9H,10",
               "V2L1T,10", "JNZDX,5", "FK7T6,5", "CX1VE,5", "8LB3W,15", "5VYSD,15", "PB93A,6", "RYVD0,6", "LN7WW,6",
               "EVL2Y,5", "46NKP,5", "5K739,5", "N0TP6,5", "XGHKK,5", "LC1ZF,5", "GFZRF,5", "RDSV7,5", "23X4K,5",
               "E760Y,5", "4PJVW,5", "GK7TF,5", "BNF16,5", "CK80S,5", "WW3X4,5", "BG49U,5", "160L5,5", "AST8G,5",
               "ZQZMM,5", "EK0RU,5", "Q02F1,5", "VTBC6,5", "9Q38P,5", "AMJQC,5", "ESWZC,10", "6HLFP,10", "AX7PV,10",
               "35WYM,10", "UPCWT,10", "G10YV,10", "ZHDG2,10", "QYSNX,10", "V23MS,10", "6BW1U,10", "13XZW,10",
               "H8DDQ,10", "4YZK2,10", "GRCHF,10", "RN41F,10", "88WVR,10", "FGVY4,10", "LB8ZA,10", "2FJR6,10",
               "FPG95,10", "Y7CR7,10", "PK4QK,10", "D3YMF,10", "ZSRND,10", "AYBJL,6", "A3V1E,6", "0F4Y2,6", "QRPSB,6",
               "SSGK1,6", "44LX9,6", "EWF0F,6", "C26JA,6", "WRPT2,6", "LXSR1,6", "E54SS,6", "HSJ95,6", "UQJ75,6",
               "JY495,6", "3J1YU,6", "5PK2F,6", "2GQC4,6", "A6XXX,6", "BG4KT,56", "EQVDW,7", "G09Y2,7", "MMG5T,7",
               "AZLMR,7", "GBCSK,7", "TW0WS,7", "QMYSS,7", "0CJX1,7", "MN655,7", "R5J3R,7", "ED2YP,7", "9W45V,7",
               "CVWFC,7", "0XJZJ,7", "YX5LX,7", "8S14Q,7", "TYR2Z,7", "C7HB0,7", "PSR58,7", "9G4XZ,7", "6MTQM,7",
               "GGM8A,7", "KTDJU,7", "Z7YP7,7", "64LS8,7", "3JF3Y,7", "7ZM49,7", "332K2,7", "TCRNU,7", "WWXXE,7",
               "51FZW,7", "0DCNP,7", "50347,34", "B9TK0,4", "1BRZ9,4", "1P2NH,4", "HJKJ7,4", "FVYLW,4", "5DDQX,4",
               "BBS9Y,4", "SWTFM,4", "HWCTA,4", "Y35Y8,4", "04HFF,4", "G58HZ,4", "0KH3N,4", "UQBP4,4", "4XV19,4",
               "J7WVM,4", "VSB15,4", "7811F,4", "M01RT,7", "2GJ71,7", "XVM2A,7", "KKHLF,7", "MBCXV,7", "YJFKC,7",
               "2F461,7", "NL677,7", "ZK6PX,7", "Y1DGP,7", "XY45X,7", "PBC3S,7", "L9VM4,7", "K6ZGD,7", "EXMT0,7",
               "W1TS8,7", "JM3RD,7", "BR2RN,7", "P7VGE,7", "W36HM,7", "WN457,7", "YQ8P1,7", "HPQJX,7", "QN2K3,7",
               "77F2U,7", "WTMBA,7", "BH5JS,7", "V4Q50,7", "M2W9Y,7", "2DW26,7", "2P5GC,7", "YK58N,7", "LWVT1,7",
               "KCGSQ,7", "5X2WL,7", "C8HWW,7", "NKYBF,2", "RHCPY,2", "Y00N1,2", "537LE,2", "6M4KK,2", "T5GDL,2",
               "C1YHA,2", "75WQE,2", "LT3V2,2", "P0TRV,2", "WC39K,2", "HZL1C,2", "7Q0W3,2", "1SRBE,2", "PVBCZ,2",
               "WS487,2", "LDJMH,2", "6PWYH,2", "9557A,2", "SQQ6T,2", "TCZWZ,2", "X5PCF,2", "T43DQ,6", "CNLKQ,6",
               "Y5TQZ,6", "AJLRZ,6", "G8ZH7,6", "5GVBK,6", "ZGKR1,6", "SNR7U,2", "UYWFC,2", "TWH3B,2", "SQ15H,2",
               "KXMFU,2", "8FVQF,1", "YS6ZH,1", "79RGK,1", "L7BBV,1", "Q1494,1", "KYV1J,1", "XQ0MB,1", "UXN28,1",
               "FLK65,1", "2CSSJ,1", "GHX2J,1", "76KQG,1", "45GCD,1", "FQZGD,1", "10RSZ,1", "7FNZP,1", "8VRQY,1",
               "41SC8,1", "UWDD9,1", "701Q5,1", "RGRX0,1", "33D0N,1", "V55RU,1", "0C7VR,1", "YTFQR,1", "HQ2YH,1",
               "H1H5J,1", "91K17,1", "SG10Q,1", "SQR45,1", "0QGNU,1", "KTGGK,1", "XSPB3,1", "M33GH,1", "A53T1,3",
               "G2JRU,3", "UZ6PR,3", "BJMFU,3", "1Z6D2,3", "GR6YN,3", "6SVQ2,3", "ZC7RL,3", "1PTQ4,3", "0B3MC,3",
               "F2MVZ,3", "0BFNL,3", "8LW2D,3", "GD159,3", "7DX0S,3", "RB0H3,63", "2V840,3", "TNP0P,3", "BYJVP,3",
               "KM0VD,3", "KYBCQ,3", "X6QZ1,3", "QV779,3", "78S6F,3", "VHJ5K,3", "1QT05,3", "6WVGY,3", "PRBLA,12",
               "QRJZ8,12", "D95Z4,12", "7FYVN,12", "86VP1,12", "04X73,6", "4RYSQ,6", "WT5NN,6", "UJCC9,6", "H0HNU,3",
               "K7S33,4", "W39HA,4", "PWWYH,4", "LWKGW,4", "W2574,4", "N6BDV,4", "8FFD0,4", "8MJ4L,4", "ML53C,4",
               "EH5TF,6", "PNV30,6", "0274D,6", "JFDDF,6", "J1VX1,1", "6G1P3,1", "7HS88,1", "4KFJF,1", "Z1TTU,6",
               "DCK4Q,6", "KH41K,6", "AQ6QL,6", "M5DK8,6", "15B04,6", "L7WXH,1", "SYFG7,1", "7VHWC,1", "C772E,1",
               "CGH9R,1", "9XJLJ,1", "UPFKK,1", "BKCQF,3", "6SQ8E,3", "KXC1N,3", "BMYS3,3", "G91ZT,3", "L30S5,3",
               "FKBVJ,3", "2Q7SW,3", "02BX6,3", "41B5P,3", "LHQXH,1", "FR6H4,2", "75QB5,2", "V5FM9,2", "S9G4V,2",
               "6MZX6,2", "D7JKM,2", "J1QYY,2", "QXYT8,2", "ZGSYB,2", "2J2TC,2", "8B97M,25", "BD3NF,7", "61VBM,7",
               "NPCR7,7", "8633F,7", "6N0T5,7", "C2BL7,7", "340TD,7", "QLS36,7", "8FCZL,3", "H6CPS,3", "909FR,3",
               "M76WE,3", "74CK3,6", "Y08RQ,6", "E5139,6", "35NJN,6", "BZL4L,3", "X7YPS,3", "LZG97,3", "TK8KH,3",
               "P0M07,3", "N93KW,3", "5KT0Z,3", "XRLWH,3", "P3105,3", "N29J6,11", "BV8ZV,11", "H7V6E,11", "CM7SL,11",
               "LG8BZ,11", "HF29R,11", "L7H25,11", "3ZGQE,11", "J074N,6", "S6K60,4", "DTMWX,4", "CX07C,4", "GGY0Q,4",
               "CW47J,4", "SGFCY,4", "0QDPW,4", "SSV09,4", "N7J5X,4", "GG87F,4", "FJ7N3,7", "PY3ZT,6");

   private void fixTestTaskResolutions() throws OseeCoreException {
      System.out.println("Started fixTestTaskResolutions...");
      for (Artifact artifact : ArtifactQuery.getArtifactsFromAttributeType(
            ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), AtsPlugin.getAtsBranch())) {
         if (artifact instanceof TaskArtifact) {
            TaskArtifact taskArt = (TaskArtifact) artifact;
            String resolution =
                  ((TaskArtifact) artifact).getSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(),
                        null);
            if (resolution == null) {
               System.err.println("Unexpected null resolution." + taskArt.getHumanReadableId());
               //               taskArt.deleteSoleAttribute(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
               //               taskArt.persistAttributes();
            } else {
               String newResolution = null;
               if (resolution.equals("Need_DTE_Test")) {
                  System.out.println("Rename Need_DTE_Test to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               } else if (resolution.equals("Awaiting_Code_Fix")) {
                  System.out.println("Rename Awaiting_Code_Fix to Awaiting_Code " + taskArt.getHumanReadableId());
                  newResolution = "Awaiting_Code";
               } else if (resolution.equals("Awaiting_Review")) {
                  System.out.println("Rename Awaiting_Review to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               } else if (resolution.equals("Unit_Tested")) {
                  System.out.println("Rename Unit_Tested to In_DTE_Test " + taskArt.getHumanReadableId());
                  newResolution = "In_DTE_Test";
               }
               if (newResolution != null) {
                  taskArt.setSoleAttributeFromString(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), newResolution);
                  taskArt.persistAttributes();
               }
            }
         }
      }
      System.out.println("Completed fixTestTaskResolutions...");
   }

   //   private void deleteUnAssignedUserRelations() throws OseeCoreException {
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
   //            User unassignedUser = SkynetAuthentication.getUser(UserEnum.UnAssigned);
   //            for (Artifact art : unassignedUser.getRelatedArtifacts(CoreRelationEnumeration.Users_Artifact)) {
   //               if (art instanceof StateMachineArtifact) {
   //                  unassignedUser.deleteRelation(CoreRelationEnumeration.Users_Artifact, art);
   //               }
   //            }
   //            unassignedUser.persistRelations();
   //         }
   //      };
   //      newActionTx.execute();
   //   }

   private final boolean fixIt = false;

   //   public void cleanXViewerCustomizations() throws OseeCoreException {
   //      for (User user : SkynetAuthentication.getUsers()) {
   //         System.out.println("User: " + user);
   //
   //         SkynetUserArtifactCustomizeDefaults custDefaults = new SkynetUserArtifactCustomizeDefaults(user);
   //
   //         // Get all customizations
   //         List<String> customizations = user.getAttributesToStringList("XViewer Customization");
   //         if (customizations.size() == 0 && custDefaults.size() == 0) continue;
   //         Set<String> validGuids = new HashSet<String>();
   //         int currNumDefaults = custDefaults.getGuids().size();
   //         for (String custStr : new CopyOnWriteArrayList<String>(customizations)) {
   //            CustomizeData custData = new CustomizeData(custStr);
   //            validGuids.add(custData.getGuid());
   //
   //            // check for old customizations to remove
   //            boolean orderFound = custStr.contains("<order>");
   //            boolean namespaceNullFound = custStr.contains("namespace=\"null\"");
   //            if (orderFound || namespaceNullFound) {
   //               System.err.println("Removing " + (orderFound ? "<order>" : "namespace==null") + " customizations " + custData.getGuid());
   //               validGuids.remove(custData.getGuid());
   //               custDefaults.removeDefaultCustomization(custData);
   //               customizations.remove(custStr);
   //            } else {
   //               // Check for sort columns that are hidden
   //               for (String columnName : custData.getSortingData().getSortingNames()) {
   //                  XViewerColumn xCol = custData.getColumnData().getXColumn(columnName);
   //                  if (xCol == null) {
   //                     System.err.println("sort column not found \"" + columnName + "\" - " + custData.getGuid());
   //                  } else if (xCol.isShow() == false) {
   //                     System.err.println("sort col is hidden \"" + columnName + "\" - " + custData.getGuid());
   //                  }
   //               }
   //            }
   //         }
   //         if (validGuids.size() != custDefaults.getGuids().size()) {
   //            System.err.println("Update default customizations : " + user + " - " + currNumDefaults + " valid: " + validGuids.size());
   //            custDefaults.setGuids(validGuids);
   //         }
   //         if (fixIt) {
   //            custDefaults.save();
   //            user.setAttributeValues("XViewer Customization", customizations);
   //            user.persistAttributes();
   //         }
   //      }
   //   }

   public Result isCustomizationSortErrored(String custDataStr, CustomizeData custData) {

      return Result.TrueResult;
   }

   //   String xViewerDefaults = user.getSoleAttributeValueAsString("XViewer Defaults", null);
   //   // Get all current default guids
   //   Set<String> currentDefaultGuids = new HashSet<String>();
   //   if (xViewerDefaults != null) {
   //      for (String guid : AXml.getTagDataArray(xViewerDefaults, XVIEWER_DEFAULTS_TAG)) {
   //         if (guid != null && !guid.equals("")) {
   //            currentDefaultGuids.add(guid);
   //         }
   //      }
   //   }
   //   private void relateDonDunne()throws OseeCoreException{
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork()throws OseeCoreException{
   //            for (Artifact art : ArtifactQuery.getArtifactsFromAttribute(
   //                  ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
   //                  "%<" + SkynetAuthentication.getUser().getUserId() + ">%", AtsPlugin.getAtsBranch())) {
   //               if ((art instanceof StateMachineArtifact) && ((StateMachineArtifact) art).getSmaMgr().getStateMgr().getAssignees().contains(
   //                     SkynetAuthentication.getUser())) {
   //                  art.addRelation(CoreRelationEnumeration.Users_User, SkynetAuthentication.getUser());
   //               }
   //            }
   //            SkynetAuthentication.getUser().persistRelations();
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }

   //   private void testDeleteAttribute() throws OseeCoreException {
   //      Artifact art =
   //            ArtifactQuery.getArtifactsFromIds(Arrays.asList("76589"), AtsPlugin.getAtsBranch()).iterator().next();
   //      for (Attribute<?> attr : art.getAttributes()) {
   //         if (attr.getValue() == null) {
   //            System.out.println(art.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //            attr.delete();
   //         }
   //      }
   //      art.persistAttributes();
   //   }

   //   private void deleteNullAttributes() throws OseeCoreException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
   //            int x = 0;
   //            for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
   //                  DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
   //                  "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
   //                  "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
   //                  "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
   //                  "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
   //                  "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {
   //               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsPlugin.getAtsBranch())) {
   //                  for (Attribute<?> attr : team.getAttributes(false)) {
   //                     if (attr.getValue() == null) {
   //                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //                        attr.delete();
   //                        x++;
   //                     }
   //                  }
   //                  if (team.isDirty()) team.persistAttributes();
   //               }
   //            }
   //            System.out.println("Deleted " + x);
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }
   //
   //   private void deleteNullUserAttributes() throws OseeCoreException {
   //
   //      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(AtsPlugin.getAtsBranch()) {
   //
   //         @Override
   //         protected void handleTxWork() throws OseeCoreException {
   //            int x = 0;
   //            for (String artTypeName : Arrays.asList(User.ARTIFACT_NAME)) {
   //               for (Artifact team : ArtifactQuery.getArtifactsFromType(artTypeName, AtsPlugin.getAtsBranch())) {
   //                  for (Attribute<?> attr : team.getAttributes(false)) {
   //                     if (attr.getValue() == null) {
   //                        System.out.println(team.getHumanReadableId() + " - " + attr.getNameValueDescription());
   //                        attr.delete();
   //                        x++;
   //                     }
   //                  }
   //                  if (team.isDirty()) team.persistAttributes();
   //               }
   //            }
   //            System.out.println("Deleted " + x);
   //         }
   //      };
   //      newActionTx.execute();
   //
   //   }

   //   for (String artTypeName : Arrays.asList(TeamWorkFlowArtifact.ARTIFACT_NAME, TaskArtifact.ARTIFACT_NAME,
   //         DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME,
   //         "Lba V13 Code Team Workflow", "Lba V13 Test Team Workflow", "Lba V13 Req Team Workflow",
   //         "Lba V13 SW Design Team Workflow", "Lba V13 Tech Approach Team Workflow",
   //         "Lba V11 REU Code Team Workflow", "Lba V11 REU Test Team Workflow", "Lba V11 REU Req Team Workflow",
   //         "Lba B3 Code Team Workflow", "Lba B3 Test Team Workflow", "Lba B3 Req Team Workflow",
   //         "Lba B3 SW Design Team Workflow", "Lba B3 Tech Approach Team Workflow")) {

}
