<!-- Do Not Remove: VUE mapping @version(1.1) jar:file:/C:/Program%20Files/vue/VUE.jar!/tufts/vue/resources/lw_mapping_1_1.xml -->
<!-- Do Not Remove: Saved date Mon Aug 20 09:28:16 MST 2007 by b0727536 on platform Windows XP 5.1 in JVM 1.5.0_07-b03 -->
<!-- Do Not Remove: Saving version @(#)VUE: built October 17 2006 at 0400 by vue on Linux 2.4.21-37.EL i386 JVM 1.5.0_06-b05 -->
<?xml version="1.0" encoding="US-ASCII"?>
<LW-MAP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="none" ID="0"
    label="apache_blk3_test_workflow(AAABER+9imIA8O7Wb1u5tw) Mon Aug 20 09;27;54 MST 2007.xml"
    x="73.711624" y="81.0" width="398.88843" height="371.0"
    strokeWidth="0.0" autoSized="false">
    <fillColor>#ffffff</fillColor>
    <strokeColor>#404040</strokeColor>
    <font>SansSerif-plain-14</font>
    <nodeFilter/>
    <child ID="4" label="Endorse" x="212.17499" y="81.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage startPage="true" pageId="apache.blk3.test.Endorse"&gt;%nl;%nl;&lt;XWidget displayName="Legacy PCR Number" storageName="ats.Legacy PCR Id" horizontalLabel="true" xwidgetType="XLabelDam"/&gt;%nl;%nl;&lt;XWidget displayName="Title" storageName="Name" required="true" xwidgetType="XTextDam"/&gt;%nl;%nl;&lt;XWidget displayName="Description" storageName="ats.Description" required="true" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution Override" storageName="ats.Proposed Resolution Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution" storageName="ats.Proposed Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Change Type" storageName="ats.Change Type" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" beginComposite="4"/&gt;%nl;%nl;&lt;XWidget displayName="Priority" storageName="ats.Priority" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" endComposite="true"/&gt;%nl;%nl;&lt;XWidget displayName="Deadline" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true" beginComposite="6"/&gt;%nl;%nl;&lt;XWidget displayName="Validation Required" storageName="ats.Validation Required" xwidgetType="XCheckBoxDam" horizontalLabel="true" endComposite="true" labelAfter="true"/&gt;%nl;%nl;&lt;XWidget displayName="Metrics from Tasks" storageName="ats.Metrics from Tasks" xwidgetType="XCheckBoxDam" horizontalLabel="true" endComposite="true" labelAfter="true"/&gt;%nl;%nl;&lt;XWidget displayName="User Community" storageName="ats.User Community" xwidgetType="XListDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" required="true"/&gt;%nl;%nl;&lt;XOrRequired xWidgets="ats.Proposed Resolution Override;ats.Proposed Resolution"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="53" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="6" label="Analyze" x="210.17499" y="177.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.test.Analyze" atsRequireStateHourSpentPrompt="true"&gt;%nl;%nl;&lt;XWidget displayName="Problem Override" storageName="ats.Problem Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Problem" storageName="ats.Problem" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution Override" storageName="ats.Proposed Resolution Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution" storageName="ats.Proposed Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Change Type" storageName="ats.Change Type" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" beginComposite="6"/&gt;%nl;%nl;&lt;XWidget displayName="Priority" storageName="ats.Priority" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" /&gt;%nl;%nl;&lt;XWidget displayName="Deadline" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true" endComposite="true" /&gt;%nl;%nl;&lt;XWidget displayName="Estimated Hours" storageName="ats.Estimated Hours" xwidgetType="XFloatDam" required="true" horizontalLabel="true" /&gt;%nl;%nl;&lt;XOrRequired xWidgets="ats.Problem Override;ats.Problem"/&gt;%nl;&lt;XOrRequired xWidgets="ats.Proposed Resolution Override;ats.Proposed Resolution"/&gt;%nl;%nl;&lt;/WorkPage&gt;%nl;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="49" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="8" label="default" x="252.5" y="110.5" width="10.0"
        height="66.99902" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="258.1979" y="110.5"/>
        <point2 x="256.8021" y="177.49902"/>
        <ID1>4</ID1>
        <ID2>6</ID2>
    </child>
    <child ID="9" label="Authorize" x="207.17499" y="258.5"
        width="99.75" height="28.5" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.test.Authorize"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="59" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="10" label="default" x="251.77585" y="206.5" width="10.0"
        height="52.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.59875" y="206.5"/>
        <point2 x="256.95294" y="258.5"/>
        <ID1>6</ID1>
        <ID2>9</ID2>
    </child>
    <child ID="12" label="Implement" x="206.17499" y="352.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.test.Implement" atsRequireStateHourSpentPrompt="true"&gt;%nl;%nl;&lt;XWidget displayName="Resolution Override" storageName="ats.Resolution Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Resolution" storageName="ats.Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XOrRequired xWidgets="ats.Resolution Override;ats.Resolution"/&gt;%nl;%nl;&lt;AtsTaskOptions&gt;%nl;&lt;AtsTaskOption name="None" desc="Nothing has been done." complete="false" percent="0" /&gt;%nl;&lt;AtsTaskOption name="Investigating" desc="Looking into task." complete="false" percent="5" /&gt;%nl;&lt;AtsTaskOption name="In_Work" desc="Working on task." complete="false" percent="15" /&gt;%nl;&lt;AtsTaskOption name="Awaiting_Code_Fix" desc="Awaiting code fix to continue." complete="false" percent="20" color="DARK_RED"/&gt;%nl;&lt;AtsTaskOption name="Need_DTE_Test" desc="Needs Desk-Top-Environment (DTE) test to continue." complete="false" percent="50" color="DARK_RED"/&gt;%nl;&lt;AtsTaskOption name="Need_STE_Test" desc="Script needs STE test to confirm completion." complete="false" percent="80" color="DARK_RED"/&gt;%nl;&lt;AtsTaskOption name="Awaiting_Review" desc="Awaiting a review on changes made." complete="false" percent="95" color="DARK_RED"/&gt;%nl;&lt;AtsTaskOption name="Unit_Tested" desc="Script has been unit tested, but not checked in/promoted." complete="false" percent="95" /&gt;%nl;&lt;AtsTaskOption name="Need_Requirement_Fix" desc="Requires requirement fix." complete="true" percent="100" color="DARK_RED"/&gt;%nl;&lt;AtsTaskOption name="Need_Hot_Bench_Testing" desc="Requires Hot Bench Testing." complete="true" percent="100" /&gt;%nl;&lt;AtsTaskOption name="Need_Inspection" desc="Requires visual inspection." complete="true" percent="100" /&gt;%nl;&lt;AtsTaskOption name="Deferred" desc="Requirement changes deferred. %sp;CPCR written against item." complete="true" percent="100" color="DARK_GREEN"/&gt;%nl;&lt;AtsTaskOption name="Removed" desc="Requirement change was removed. Task no longer applicable." complete="true" percent="100" color="DARK_GREEN"/&gt;%nl;&lt;AtsTaskOption name="No_Change" desc="No change to code is necessary." complete="true" percent="100" color="DARK_GREEN"/&gt;%nl;&lt;AtsTaskOption name="Complete" desc="Code finished and promoted to at least a developmental build." complete="true" percent="100" color="DARK_GREEN"/&gt;%nl;&lt;/AtsTaskOptions&gt;%nl;%nl;&lt;/WorkPage&gt;%nl;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="64" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="13" label="default" x="252.66086" y="287.0" width="10.0"
        height="65.5" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="257.2352" y="287.0"/>
        <point2 x="258.08652" y="352.5"/>
        <ID1>9</ID1>
        <ID2>12</ID2>
    </child>
    <child ID="14" label="Completed" x="206.17499" y="422.5"
        width="108.399994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.test.Completed" validatePage="NonBlocking"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="68" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="16" label="default" x="254.32501" y="381.5" width="10.0"
        height="41.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="258.71" y="381.5"/>
        <point2 x="259.94" y="422.5"/>
        <ID1>12</ID1>
        <ID2>14</ID2>
    </child>
    <child ID="17" label="Cancelled" x="367.90002" y="159.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.test.Cancelled"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="63" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="18" x="288.52243" y="110.5" width="101.45514"
        height="49.000008" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="288.52243" y="110.49999"/>
        <point2 x="389.97757" y="159.5"/>
        <ID1>4</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="20" x="302.82495" y="178.31789" width="65.07504"
        height="10.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="302.82498" y="186.9"/>
        <point2 x="367.90002" y="179.73578"/>
        <ID1>6</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="21" x="280.56427" y="188.5" width="115.50888"
        height="70.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="280.5643" y="258.5"/>
        <point2 x="396.07318" y="188.5"/>
        <ID1>9</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="22" x="270.42532" y="188.5" width="137.42438"
        height="164.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="270.42532" y="352.5"/>
        <point2 x="407.8497" y="188.5"/>
        <ID1>12</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="23" label="return" x="74.211624" y="282.77472"
        width="137.64745" height="69.72528" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="211.85907" y="352.5"/>
        <point2 x="207.17499" y="282.77472"/>
        <ID1>12</ID1>
        <ID2>9</ID2>
        <ctrlPoint0 x="74.211624" y="309.5" xsi:type="point"/>
    </child>
    <child ID="24" label="return" x="97.322464" y="204.6597"
        width="112.85252" height="56.459" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="207.17499" y="261.11868"/>
        <point2 x="210.17499" y="204.65968"/>
        <ID1>9</ID1>
        <ID2>6</ID2>
        <ctrlPoint0 x="97.322464" y="235.5" xsi:type="point"/>
    </child>
    <child ID="25" label="return" x="76.322464" y="206.5"
        width="154.18176" height="146.0" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="222.86139" y="352.5"/>
        <point2 x="230.50423" y="206.5"/>
        <ID1>12</ID1>
        <ID2>6</ID2>
        <ctrlPoint0 x="76.322464" y="292.5" xsi:type="point"/>
    </child>
    <userZoom>1.0</userZoom>
    <userOrigin x="-21.074997" y="-157.0"/>
    <PathwayList currentPathway="0" revealerIndex="-1">
        <pathway label="Untitled Pathway" x="0.0" y="0.0"
            width="1.4E-45" height="1.4E-45" hidden="true"
            strokeWidth="0.0" autoSized="false" currentIndex="-1"
            locked="false" open="true">
            <strokeColor>#993333</strokeColor>
            <font>SansSerif-plain-14</font>
            <nodeFilter/>
        </pathway>
    </PathwayList>
    <date>2006-12-19</date>
    <mapFilterModel/>
</LW-MAP>
