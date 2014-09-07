package org.eclipse.osee.ote.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.enums.PromptResponseType;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.prompt.InformationalPrompt;
import org.eclipse.osee.ote.core.framework.prompt.PassFailPromptImpl;
import org.eclipse.osee.ote.core.framework.prompt.PassFailPromptResult;
import org.eclipse.osee.ote.core.framework.prompt.ScriptPausePromptImpl;
import org.eclipse.osee.ote.core.framework.prompt.UserInputPromptImpl;
import org.eclipse.osee.ote.core.framework.prompt.YesNoPromptImpl;
import org.eclipse.osee.ote.core.framework.prompt.YesNoPromptResult;
import org.eclipse.osee.ote.core.log.record.AttentionRecord;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;

class TestPromptImpl {

   private final Executor promptInitWorker;

   public TestPromptImpl() {
      promptInitWorker = Executors.newSingleThreadExecutor();
   }

   
   public String prompt(final TestPrompt prompt, final TestEnvironment environment, final TestScript test) throws InterruptedException {

      if (environment.isInBatchMode()) {
         promptInitWorker.execute(new Runnable() {
            @Override
            public void run() {
               try {
                  test.getUserSession().initiateInformationalPrompt(prompt.toString());
               } catch (Exception e) {
                  System.out.println(prompt.toString());
               }
            }
         });
         if (prompt.getType() == PromptResponseType.PASS_FAIL) {
            test.getLogger().log(
                  new TestPointRecord(environment, new CheckPoint(prompt.toString(), "PROMPT DURING BATCH", "N/A", false),
                        true));
         } else {
            test.getLogger().log(new AttentionRecord(environment, prompt.getType().name() + " : " + prompt.toString(), true));
         }
         return "";
      } else if (!environment.getRunManager().isCurrentThreadScript()){
         promptInitWorker.execute(new Runnable() {
            @Override
            public void run() {
               try {
                  test.getUserSession().initiateInformationalPrompt(prompt.toString());
               } catch (Exception e) {
                  System.out.println(prompt.toString());
               }
            }
         });
         if (prompt.getType() != PromptResponseType.NONE) {
            test.getLogger().log(new AttentionRecord(environment, String.format("ERROR: Blocking prompt type[%s] in non script thread[%s] message[%s]", prompt.getType().name(), Thread.currentThread().getName(), prompt.toString()), true));
         } else {
            test.getLogger().log(new AttentionRecord(environment, prompt.getType().name() + " : " + prompt.toString(), true));
         }
         return "";
      } else {
         try {
            final String returnValue;
            String logOutput;
            final TestRecord testRecord;
            final IServiceConnector connector = environment.getConnector();
            synchronized (test){
               switch (prompt.getType()) {
               case NONE:
                  InformationalPrompt infoPrompt = new InformationalPrompt(connector, "", prompt.toString());
                  infoPrompt.open(test.getUserSession(), promptInitWorker);
                  infoPrompt.close();
                  returnValue = "";
                  testRecord = new AttentionRecord(environment, String.format("PROMPT [%s]\n{\n%s\n}\n", PromptResponseType.NONE.name(), prompt.toString()), true);
                  break;
               case PASS_FAIL:
                  PassFailPromptImpl passFailPrompt = new PassFailPromptImpl(connector, test, "", prompt.toString());
                  PassFailPromptResult result = passFailPrompt.open(promptInitWorker);
                  returnValue = result.getText();
                  passFailPrompt.close();
                  testRecord =
                        new TestPointRecord(environment, new CheckPoint("Pass/Fail Prompt", prompt.toString(),
                              returnValue, result.isPass()), true);
                  break;
               case YES_NO:
                  YesNoPromptImpl yesNoPrompt = new YesNoPromptImpl(connector, test, "", prompt.toString());
                  YesNoPromptResult yesNo = yesNoPrompt.open(promptInitWorker);
                  if (yesNo.isYes()) {
                     returnValue = "YES";
                  } else {
                     returnValue = "NO";
                  }
                  logOutput =
                        String.format("PROMPT [%s]\n{\n%s\n}\n\tRETURN VALUE : %s", PromptResponseType.YES_NO.name(),
                              prompt.toString(), returnValue);
                  yesNoPrompt.close();
                  testRecord = new AttentionRecord(environment, logOutput, true);
                  break;
               case SCRIPT_PAUSE:
                  ScriptPausePromptImpl scriptPausePrompt =
                  new ScriptPausePromptImpl(connector, test, "", prompt.toString());
                  returnValue = scriptPausePrompt.open(promptInitWorker);
                  scriptPausePrompt.close();
                  if (returnValue != null && returnValue.length() > 0) {
                     logOutput =
                           String.format("PROMPT [%s]\n{\n%s\n}\n\tRETURN VALUE : %s", PromptResponseType.SCRIPT_PAUSE.name(),
                                 prompt.toString(), returnValue);
                  } else {
                     logOutput =
                           String.format("PROMPT [%s]\n{\n%s\n}\n", PromptResponseType.SCRIPT_PAUSE.name(), prompt.toString());
                  }

                  testRecord = new AttentionRecord(environment, logOutput, true);
                  ;
                  break;
               case USER_INPUT:
                  UserInputPromptImpl userInputPrompt = new UserInputPromptImpl(connector, test, "", prompt.toString());
                  returnValue = userInputPrompt.open(promptInitWorker);
                  userInputPrompt.close();
                  if (returnValue != null && returnValue.length() > 0) {
                     logOutput =
                           String.format("PROMPT [%s]\n{\n%s\n}\n\tRETURN VALUE : %s", PromptResponseType.USER_INPUT.name(),
                                 prompt.toString(), returnValue);
                  } else {
                     logOutput =
                           String.format("PROMPT [%s]\n{\n%s\n}\n", PromptResponseType.USER_INPUT.name(), prompt.toString());
                  }
                  testRecord = new AttentionRecord(environment, logOutput, true);
                  break;
               case SCRIPT_STEP:
                  returnValue = "";
                  testRecord =
                        new AttentionRecord(environment,
                              PromptResponseType.SCRIPT_STEP.name() + " : " + prompt.toString(), true);
                  break;
               case OFP_DEBUG_RESPONSE:
                  returnValue = "";
                  testRecord = null;
                  break;
               default:
                  returnValue = "";
                  testRecord = null;
               }
            }
            if (testRecord != null) {
               testRecord.setStackTrace(new Throwable());
               test.getLogger().log(testRecord);
            }
            return returnValue;
         } catch (InterruptedException e) {
            throw new InterruptedException();
         } catch (Exception e) {
            // what
         }
         return "";
      }

   }
}
