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
package org.eclipse.osee.ote.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.requirement.RequirementId;
import org.eclipse.osee.framework.logging.ILoggerFilter;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.enums.PromptResponseType;
import org.eclipse.osee.ote.core.enums.ScriptTypeEnum;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptCompleteListener;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptInitializer;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.status.CommandEndedStatusEnum;
import org.eclipse.osee.ote.core.framework.prompt.InformationalPrompt;
import org.eclipse.osee.ote.core.framework.prompt.PassFailPromptImpl;
import org.eclipse.osee.ote.core.framework.prompt.PassFailPromptResult;
import org.eclipse.osee.ote.core.framework.prompt.ScriptPausePromptImpl;
import org.eclipse.osee.ote.core.framework.prompt.UserInputPromptImpl;
import org.eclipse.osee.ote.core.framework.testrun.ITestRunListener;
import org.eclipse.osee.ote.core.framework.testrun.ITestRunListenerProvider;
import org.eclipse.osee.ote.core.log.ITestPointTally;
import org.eclipse.osee.ote.core.log.ScriptLogHandler;
import org.eclipse.osee.ote.core.log.record.AttentionRecord;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;

/**
 * TestScript is the abstract base class for all test scripts. This class provides the interfaces necessary to allow a
 * script to be run against remote testing environment servers.
 * <p>
 * The TestScript class contains the following information:
 * <ul>
 * <li><b>crew</b> - Defines if the script is for pilot or copilot.
 * <li><b>isBatchable</b> - Defines whether or not the script is batchable. This typically indicates whether or not
 * there are any prompts in the script.
 * <li><b>isMpLevel</b> - Defines if the script tests at the Mission Processor level or if it tests at the node level.
 * <li>Selective run list of TestCase's for test case skipping
 * <li>ScriptInitializer object
 * <li>TestCase objects that make up the runtime of the script
 * </ul>
 * <p>
 * When building a new TestScript object it is important to understand that the object does not only contain the runtime
 * code for performing the test, but also contains information about the test being performed that can be retrieved by
 * instantiating the object with out necessarily running it.
 * <p>
 * The following sample should be followed closely when building TestScript objects. <br>
 * <br>
 * <hr>
 * <br>
 * <code>
 * public class SampleScript extends TestCase {
 * <ul style="list-style: none">
 * <li>	</code><i>Place declarations for <b>LRU Models</b>,<b>Messages</b>, and <b>Support</b> classes here.</i><code>
 * <li>
 * <li>	</code><i>This constructor is necessary for instantiating the TestScript object to get data!</i><code>
 * <li>	SampleScript () {
 * 		<ul style="list-style: none">
 * <li>		this (null,null);
 * 		</ul>
 * <li>	}
 * <li>	
 * <li>	</code><i>This constructor is used at runtime.</i><code>
 * <li>	SampleScript (TestEnvironment environment, TestEnvironmentController connection) {
 * 		<ul style="list-style: none">
 * <li>		super (environment, connection, [batchability, true or false]);
 * <li>
 * <li>		</code><i>Place construction for <b>LRU Models</b>,<b>Messages</b>, and <b>Support</b> classes here.</i><code>
 * <li> 
 * <li>		</code><i>Construct all test cases here. The base TestCase constructor will automatically add
 * <li>itself to the run list of the TestScript.</i><code>
 * <li>		new OipCase(this);
 * <li>		</code><i>...</i><code>
 * 		</ul>
 * <li>	}
 * <li>
 * <li>	</code><i>This inner class defines the setup for the script and must be present.</i><code>
 * <li>	private class LocalSetupTestCase extends SetupTestCase {
 * 		<ul style="list-style: none">
 * 	<li>
 * 	<li>		</code><i>Allows the setup to add itself to the runlist.</i><code>
 * 	<li>		protected LocalSetupTestCase(TestScript parent) {
 * 			<ul style="list-style: none">
 * <li>      	super(parent);
 * 			</ul>
 * <li>		}
 * <li>
 * <li>		</code><i>Provides the runtime code for initialization.</i><code>
 * <li>		public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {
 * 			<ul style="list-style: none">
 * <li>			</code><i>Place any necessary setup code here and it will be run prior to any of the test cases.</i><code>
 * 			</ul>
 * <li>		}
 * 		</ul>
 * <li>	}
 * <li>	<br>
 * <li>	</code><i>Place all of the test cases here. These will be inner classes that extend </i>
 * {@link org.eclipse.osee.ote.core.TestCase TestCase}<i>.
 * <li><b>NOTE:</b>All of these inner classes must be instantiated in the constructor for the TestScript object. If an
 * inner class is not instantiated then it will <b>not</b> be run, and information within the class will not be
 * available when the TestScript is instantiated for data retrieval.</i><code>
 * <li>	public class OipCase extends TestCase {
 * 		<ul style="list-style: none">
 * <li>		public OipCase(TestScript parent) {
 * 			<ul style="list-style: none">
 * <li>			</code><i>Use <b>one</b> of the following constructors based on if this TestCase is standalone</i><code>
 * <li>			super(parent);</code>
 * <i>Standalone defaulted to <b>false</b></i><code>
 * <li>			super(parent, true);</code><i>Standalone explicitly set to
 * <b>true</b></i><code>
 * <li>			
 * <li>			</code><i>All requirements tested in the test case should be noted here with the </i>
 * <code>{@link org.eclipse.osee.ote.core.TestCase#addTracability(RequirementId) addTracability}</code><i> method.</i>
 * <code>
 * 			</ul>
 * <li>		}
 * <li>
 * <li>		</code><i><b>Note:</b>It is very important that </i><code>doTestCase</code><i> always has the </i>
 * <code>throws InterrupedException</code><i> in the method declaration. The ability to abort a script relies on this
 * statement, and without it many calls would have to be wrapped with a try/catch block.</i><code>
 * <li>		public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) throws InterruptedException {
 * 			<ul style="list-style: none">
 * <li>			</code><i>Place all of the runtime code for the test case here.</i><code>
 * 			</ul>
 * <li>		}
 * 		</ul>
 * <li>	}
 * </ul>
 * }
 * </code> <br>
 * <hr>
 * 
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 * @see org.eclipse.osee.ote.core.TestCase
 */
public abstract class TestScript implements ITimeout {
   private static final Set<Class<? extends TestScript>> instances =
         Collections.synchronizedSet(new HashSet<Class<? extends TestScript>>());
   private static final AtomicLong constructed = new AtomicLong(0);
   private static final AtomicLong finalized = new AtomicLong(0);
   private final IUserSession userSession;
   private boolean isBatchable;
   private final ITestStation testStation;
   private CommandDescription cmdDescription;
   protected TestCase currentTestCase;
   protected final TestEnvironment environment;
   private boolean isMpLevel;
   private IScriptInitializer scriptInitializer;
   private ScriptLogHandler scriptLogHandler;
   private final ScriptTypeEnum scriptType;
   private final ArrayList<TestCase> selectiveRunList = new ArrayList<TestCase>(32);
   private Date startTime;
   private final ArrayList<TestCase> testCases = new ArrayList<TestCase>(32);
   private ITestPointTally testPointTally;
   protected CommandEndedStatusEnum status;
   private Throwable rootCause;
   private volatile boolean timedOut;
   private volatile boolean aborted = false;
   private final ArrayList<IScriptCompleteListener> scriptCompleteListeners =
         new ArrayList<IScriptCompleteListener>(32);

   private ScriptResultRecord sciprtResultRecord;
   private int pass;
   private int fail;
   private ScriptLoggingListener loggingListener;
   private Executor promptInitWorker;
   private ITestRunListenerProvider listenerProvider;

   /*
    * @param testManager
    */
   public TestScript(TestEnvironment environment, IUserSession callback, ScriptTypeEnum scriptType, boolean isBatchable) {
      constructed.incrementAndGet();
      this.scriptType = scriptType;
      this.userSession = callback;
      this.isBatchable = isBatchable;
      this.isMpLevel = false;

      promptInitWorker = Executors.newSingleThreadExecutor();
      sciprtResultRecord = new ScriptResultRecord(this);
      if (environment != null) {
         this.environment = environment;
         this.startTime = new Date(0);
         testStation = this.environment.getTestStation();
         GCHelper.getGCHelper().addRefWatch(this);
         instances.add(getClass());
      } else {
         throw new TestException("No environment found: Can not run script ", Level.SEVERE);
      }
      this.testPointTally = new TestPointTally(this.getClass().getName());
   }

   public void abort() {
      OseeLog.log(TestEnvironment.class, Level.SEVERE,
            "Aborting script", new Exception());
      aborted = true;
   }

   public void abortDueToThrowable(Throwable cause) {
      this.rootCause = cause;
      abort();
   }

   /**
    * Removes all of the test cases from the selective run list. The list will be empty after this call returns.
    */
   public void clearRunList() {
      selectiveRunList.clear();
   }

   public void endTest() {
      // provided as a hook for scripts to override
   }

   /**
    * @return Returns the startTime.
    */
   public Date getStartTime() {
      return startTime;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.test.core.TestAccessor#getTestCase()
    */
   public TestCase getTestCase() {
      return currentTestCase;
   }

   /**
    * Get list of test cases.
    * 
    * @return reference to arrayList testCases.
    */
   public/* TestCase */List<TestCase> getTestCases() {
      ArrayList<TestCase> testCaseList = new ArrayList<TestCase>();
      testCaseList.add(getSetupTestCase());
      testCaseList.addAll(testCases);
      testCaseList.add(getTearDownTestCase());
      return testCaseList;
   }

   public ITestEnvironmentAccessor getTestEnvironment() {
      return environment;
   }

   public IUserSession getUserSession() {
      return userSession;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.test.core.TestAccessor#getTestScript()
    */
   public TestScript getTestScript() {
      return this;
   }

   public ITestLogger getLogger() {
      return environment.getLogger();
   }

   public ScriptTypeEnum getType() {
      return this.scriptType;
   }

   public final boolean isBatchable() {
      return isBatchable;
   }

   public final boolean isMpLevel() {
      return isMpLevel;
   }

   public synchronized String prompt(final TestPrompt prompt) throws InterruptedException {

      if (environment.isInBatchMode()) {
         promptInitWorker.execute(new Runnable() {
            public void run() {
               try {
                  userSession.initiateInformationalPrompt(prompt.toString());
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         });
         if (prompt.getType() == PromptResponseType.PASS_FAIL) {
            getLogger().log(
                  new TestPointRecord(environment, new CheckPoint(prompt.toString(), "PROMPT DURING BATCH", "N/A",
                        false), true));
         } else {
            getLogger().log(new AttentionRecord(environment, prompt.getType().name() + " : " + prompt.toString(), true));
         }
         return "";
      } else {

         try {
            final String returnValue;
            final TestRecord testRecord;
            final IServiceConnector connector = environment.getConnector();
            switch (prompt.getType()) {
               case NONE:
                  InformationalPrompt infoPrompt = new InformationalPrompt(connector, "", prompt.toString());
                  infoPrompt.open(getUserSession(), promptInitWorker);
                  infoPrompt.close();
                  returnValue = "";
                  testRecord = null;
                  break;
               case PASS_FAIL:
                  PassFailPromptImpl passFailPrompt = new PassFailPromptImpl(connector, this, "", prompt.toString());
                  PassFailPromptResult result = passFailPrompt.open(promptInitWorker);
                  returnValue = result.getText();
                  passFailPrompt.close();
                  testRecord =
                        new TestPointRecord(getTestEnvironment(), new CheckPoint("Pass/Fail Prompt", prompt.toString(),
                              returnValue, result.isPass()), true);
                  break;
               case SCRIPT_PAUSE:
                  ScriptPausePromptImpl scriptPausePrompt =
                        new ScriptPausePromptImpl(connector, this, "", prompt.toString());
                  returnValue = scriptPausePrompt.open(promptInitWorker);
                  scriptPausePrompt.close();
                  testRecord =
                        new AttentionRecord(getTestEnvironment(),
                              PromptResponseType.SCRIPT_PAUSE.name() + " : " + prompt.toString(), true);
                  ;
                  break;
               case USER_INPUT:
                  UserInputPromptImpl userInputPrompt = new UserInputPromptImpl(connector, this, "", prompt.toString());
                  returnValue = userInputPrompt.open(promptInitWorker);
                  userInputPrompt.close();
                  testRecord =
                        new AttentionRecord(getTestEnvironment(),
                              PromptResponseType.USER_INPUT.name() + " : " + prompt.toString(), true);
                  break;
               case SCRIPT_STEP:
                  returnValue = "";
                  testRecord =
                        new AttentionRecord(getTestEnvironment(),
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
            if (testRecord != null) {
               testRecord.setStackTrace(new Throwable());
               getLogger().log(testRecord);
            }
            return returnValue;
         } catch (Exception e) {

         }
         return "";
      }

   }

   /**
    * This method will display a null prompt to the console.
    * 
    * @throws InterruptedException
    */
   public void prompt() throws InterruptedException {
      getTestScript().prompt(new TestPrompt(null, PromptResponseType.NONE));
   }

   /**
    * This method will display the message input to the console.
    * 
    * @param message
    * @throws InterruptedException
    */
   public void prompt(String message) throws InterruptedException {
      getTestScript().prompt(new TestPrompt(message, PromptResponseType.NONE));
   }

   /**
    * This method will display the message input to the console. It will also prompt the user with a dialog box to input
    * whether the condition passed or failed.
    * 
    * @param message
    * @throws InterruptedException
    */
   public void promptPassFail(String message) throws InterruptedException {
      getTestScript().prompt(new TestPrompt(message, PromptResponseType.PASS_FAIL));
   }

   /**
    * This method will display the message input to the console. It also pauses the script running, and will prompt the
    * user with a dialog box to continue on with the running of the script.
    * 
    * @param message
    * @throws InterruptedException
    */
   public void promptPause(String message) throws InterruptedException {
      prompt(new TestPrompt(message, PromptResponseType.SCRIPT_PAUSE));
   }

   /**
    * Takes result of test point and updates the total test point tally.
    * 
    * @param passed
    * @return total number of test points completed as an int.
    */
   public int recordTestPoint(boolean passed) {
      return testPointTally.tallyTestPoint(passed);
   }

   public int getCurrentPointNumber() {
      return testPointTally.getTestPointTotal();
   }

   public void addScriptSummary(Xmlizable xml) {
      sciprtResultRecord.addChildElement(xml);
   }

   /**
    * Add a single test case to selective run list.
    * 
    * @param testCaseNumber
    * @throws IllegalArgumentException
    */
   public void selectTestCase(int testCaseNumber) {
      TestCase testCase = testCases.get(testCaseNumber - 1);
      if (!testCase.isStandAlone()) {
         throw new IllegalArgumentException("Test case " + testCaseNumber + " is not stand alone.");
      }
      selectiveRunList.add(testCase);
   }

   /**
    * Add multiple test cases to the selective run list.
    * 
    * @param testCaseNumberStart
    * @param testCaseNumberEnd
    */
   public void selectTestCases(int testCaseNumberStart, int testCaseNumberEnd) {
      for (int i = testCaseNumberStart; i <= testCaseNumberEnd; i++) {
         selectTestCase(i);
      }
   }

   /**
    * Sets the script initializer.
    * 
    * @param scriptInitializer
    */
   public void setScriptInitializer(IScriptInitializer scriptInitializer) {

      this.scriptInitializer = scriptInitializer;

   }

   public IScriptInitializer getScriptInitializer() {
      return scriptInitializer;
   }

   /**
    * Causes current thread to wait until another thread invokes the {@link java.lang.Object#notify()}method or the
    * {@link java.lang.Object#notifyAll()}method for this object.
    * 
    * @param milliseconds
    * @throws InterruptedException
    */
   public synchronized void testWait(int milliseconds) throws InterruptedException {
      environment.getLogger().methodCalled(this.environment, new MethodFormatter().add(milliseconds));
      environment.setTimerFor(this, milliseconds);
      wait();
      this.getTestEnvironment().getScriptCtrl().lock();
      environment.getLogger().methodEnded(this.environment);
   }

   public synchronized void testWaitNoLog(int milliseconds) throws InterruptedException {
      environment.setTimerFor(this, milliseconds);
      wait();
      this.getTestEnvironment().getScriptCtrl().lock();
   }

   /**
    * Prints text to the console informing the user of how much time is left in the wait. Any wait time can be passed
    * in, but this is intended for long waits; usually > 20 seconds. Will not print any info is the time is less than
    * 1000 ms.
    * 
    * @param ms Milliseconds to wait for.
    * @throws InterruptedException
    */
   public synchronized void testWaitWithInfo(int ms) throws InterruptedException {
      int mult = 0;
      if (ms > 999) {
         prompt(new TestPrompt("\tWaiting for " + (ms / 1000.0) + " seconds."));
         while (ms >= 30000) {
            mult++;
            ms -= 20000;
            testWait(20000);
            prompt(new TestPrompt("\t" + (20 * mult) + " seconds elapsed."));
         }

         prompt(new TestPrompt("\tFinishing up; " + (ms / 1000.0) + " more seconds."));
      }
      testWait(ms);
      prompt(new TestPrompt("\ttestWait done."));
   }

   // public String toString() {

   // String description = "Test Script:\n";

   // for (Iterator iter = testCases.iterator(); iter.hasNext();) {
   // TestCase testCase = (TestCase) iter.next();
   // description += "\t" + testCase + "\n";
   // }

   // return description;
   // }

   /**
    * Add test case to the test scripts list of test cases.
    * 
    * @param testCase
    * @return the number of test cases in the arrayList testCases as an int.
    */
   protected int addTestCase(TestCase testCase) {
      testCases.add(testCase);
      return testCases.size();
   }

   @Deprecated
   public CommandDescription getCommandDescription() {
      return this.cmdDescription;
   }

   protected TestCase getSetupTestCase() {
      return null;
   }

   protected TestCase getTearDownTestCase() {
      return null;
   }

   public void logTestPoint(boolean isPassed, String testPointName, String expected, String actual) {
      this.getLogger().testpoint(this.getTestEnvironment(), this, this.getTestCase(), isPassed, testPointName,
            expected, actual);
   }

   public boolean isTimedOut() {
      return this.timedOut;
   }

   public void setTimeout(boolean timeout) {
      this.timedOut = timeout;
   }

   /**
    * Use addTestRunListener(ITestRunListener listener) instead.
    * 
    * @see addTestRunListener(ITestRunListener listener)
    */
   @Deprecated
   public void addScriptCompleteListener(IScriptCompleteListener listener) {
      scriptCompleteListeners.add(listener);
   }

   public ITestStation getTestStation() {
      return testStation;
   }

   protected void dispose() {
      OseeLog.log(TestScript.class, Level.FINEST, "calling dispose on the TestScript.class");
   }

   public void addTestPoint(boolean pass) {
      if (pass) {
         this.pass++;
      } else {
         this.fail++;
      }
   }

   private class ScriptLoggingListener implements ILoggerListener {

      ILoggerFilter filter = new TestScriptLogFilter();

      public ILoggerFilter getFilter() {
         return filter;
      }

      public void log(String loggerName, Level level, String message, Throwable th) {
         if (environment.getLogger() != null) {
            environment.getLogger().log(level, message, th);
         }
      }

   }

   @Override
   protected void finalize() throws Throwable {
      instances.remove(getClass());
      finalized.incrementAndGet();
      super.finalize();
   }

   public static long getConstructed() {
      return constructed.get();
   }

   public static long getFinalized() {
      return finalized.get();
   }

   public static Collection<Class<? extends TestScript>> getInstances() {
      return new HashSet<Class<? extends TestScript>>(instances);
   }

   public void disposeTest() {
      dispose();
   }

   public boolean isAborted() {
      return aborted;
   }

   @Deprecated
   /**
    * 
    */
   public void processScriptcompleteListeners() {
      for (IScriptCompleteListener listener : scriptCompleteListeners) {
         try {
            listener.onScriptComplete();
         } catch (Throwable t) {
            OseeLog.log(TestEnvironment.class, 
                  Level.SEVERE, "exception while notifying script complete listener " + listener.getClass().getName(),
                  t);
         }
      }
   }

   /**
    * @param listenerProvider
    */
   public void setListenerProvider(ITestRunListenerProvider listenerProvider) {
      this.listenerProvider = listenerProvider;
   }

   public boolean addTestRunListener(ITestRunListener listener) {
      return this.listenerProvider.addTestRunListener(listener);
   }

   public boolean removeTestRunListener(ITestRunListener listener) {
      return this.listenerProvider.removeTestRunListener(listener);
   }

   /**
    * @return
    */
   public int getPasses() {
      return pass;
   }

   /**
    * @return
    */
   public int getFails() {
      return fail;
   }

   @Deprecated
   public ScriptResultRecord getScriptResultRecord() {
      return this.sciprtResultRecord;
   }

   /**
    * @return
    */
   public ScriptLogHandler getScriptLogHandler() {
      return this.scriptLogHandler;
   }

   /**
    * @param b
    */
   public void setAborted(boolean aborted) {
      this.aborted = aborted;
   }

   /**
    * @param currentTestCase the currentTestCase to set
    */
   void setTestCase(TestCase currentTestCase) {
      this.currentTestCase = currentTestCase;
   }

   /**
    * @return
    */
   public String getOutfileComment() {
      return "\nNO_OUTFILE_COMMENT\n";
   }

}