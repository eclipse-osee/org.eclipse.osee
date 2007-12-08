<!-- Do Not Remove: VUE mapping @version(1.1) jar:file:/C:/Program%20Files/vue/VUE.jar!/tufts/vue/resources/lw_mapping_1_1.xml -->
<!-- Do Not Remove: Saved date Fri Sep 28 14:58:42 MST 2007 by b0727536 on platform Windows XP 5.1 in JVM 1.5.0_07-b03 -->
<!-- Do Not Remove: Saving version @(#)VUE: built October 17 2006 at 0400 by vue on Linux 2.4.21-37.EL i386 JVM 1.5.0_06-b05 -->
<?xml version="1.0" encoding="US-ASCII"?>
<LW-MAP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="none" ID="0"
    label="osee_ats_defaultTeam_workflow.vue" x="105.31415" y="84.0"
    width="367.2859" height="372.0" strokeWidth="0.0" autoSized="false">
    <fillColor>#ffffff</fillColor>
    <strokeColor>#404040</strokeColor>
    <font>SansSerif-plain-14</font>
    <nodeFilter/>
    <child ID="4" label="Endorse" x="210.17499" y="84.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage startPage="true" pageId="osee.ats.defaultTeam.Endorse"&gt;%nl;%nl;&lt;XWidget displayName="Title" storageName="Name" required="true" xwidgetType="XTextDam"/&gt;%nl;%nl;&lt;XWidget displayName="Description" storageName="ats.Description" required="true" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution" storageName="ats.Proposed Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Change Type" storageName="ats.Change Type" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" beginComposite="4"/&gt;%nl;%nl;&lt;XWidget displayName="Priority" storageName="ats.Priority" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" endComposite="true" helpPluginId="osee.ats" helpContextId="atsPriority"/&gt;%nl;%nl;&lt;XWidget displayName="Deadline" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true" beginComposite="6"/&gt;%nl;%nl;&lt;XWidget displayName="Validation Required" storageName="ats.Validation Required" xwidgetType="XCheckBoxDam" horizontalLabel="true" endComposite="true" labelAfter="true"/&gt;%nl;%nl;&lt;XWidget displayName="Metrics from Tasks" storageName="ats.Metrics from Tasks" xwidgetType="XCheckBoxDam" horizontalLabel="true" endComposite="true" labelAfter="true"/&gt;%nl;%nl;&lt;XWidget displayName="User Community" storageName="ats.User Community" xwidgetType="XListDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" required="true"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="53" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="6" label="Analyze" x="209.17499" y="176.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultTeam.Analyze" atsRequireStateHourSpentPrompt="true"&gt;%nl;%nl;&lt;XWidget displayName="Problem" storageName="ats.Problem" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution" storageName="ats.Proposed Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Change Type" storageName="ats.Change Type" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" beginComposite="6"/&gt;%nl;%nl;&lt;XWidget displayName="Priority" storageName="ats.Priority" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" /&gt;%nl;%nl;&lt;XWidget displayName="Deadline" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true" endComposite="true" /&gt;%nl;%nl;&lt;XWidget displayName="Estimated Hours" storageName="ats.Estimated Hours" xwidgetType="XFloatDam" required="true" horizontalLabel="true" /&gt;%nl;%nl;&lt;/WorkPage&gt;%nl;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="49" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="8" label="default" x="251.0" y="113.49805" width="10.0"
        height="63.001953" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.3424" y="113.49805"/>
        <point2 x="255.65761" y="176.5"/>
        <ID1>4</ID1>
        <ID2>6</ID2>
    </child>
    <child ID="9" label="Authorize" x="210.17499" y="259.5"
        width="99.75" height="28.5" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultTeam.Authorize"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="59" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="10" label="default" x="252.78186" y="205.5" width="10.0"
        height="54.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.29727" y="205.5"/>
        <point2 x="259.26645" y="259.5"/>
        <ID1>6</ID1>
        <ID2>9</ID2>
    </child>
    <child ID="12" label="Implement" x="206.17499" y="352.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultTeam.Implement" atsRequireStateHourSpentPrompt="true"&gt;%nl;%nl;&lt;XWidget displayName="Resolution" storageName="ats.Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="64" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="13" label="default" x="254.16486" y="288.0" width="10.0"
        height="64.50098" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="259.77875" y="288.0"/>
        <point2 x="258.551" y="352.50098"/>
        <ID1>9</ID1>
        <ID2>12</ID2>
    </child>
    <child ID="14" label="Completed" x="207.17499" y="426.5"
        width="108.399994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultTeam.Completed" validatePage="NonBlocking"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="68" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="16" label="default" x="254.82501" y="381.5" width="10.0"
        height="45.00049" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="258.88242" y="381.5"/>
        <point2 x="260.76758" y="426.5005"/>
        <ID1>12</ID1>
        <ID2>14</ID2>
    </child>
    <child ID="17" label="Cancelled" x="367.90002" y="159.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultTeam.Cancelled"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="63" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="18" x="288.11" y="113.5" width="100.28003" height="46.0"
        strokeWidth="1.0" autoSized="false" controlCount="0"
        arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="288.11" y="113.5"/>
        <point2 x="388.39" y="159.5"/>
        <ID1>4</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="20" x="301.82495" y="177.7984" width="66.07504"
        height="10.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="301.82498" y="186.21262"/>
        <point2 x="367.90002" y="179.3842"/>
        <ID1>6</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="21" x="282.90002" y="188.5" width="113.84915"
        height="71.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="282.9" y="259.5"/>
        <point2 x="396.74915" y="188.5"/>
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
    <child ID="24" label="return" x="128.925" y="205.5"
        width="85.271484" height="54.0" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="214.19649" y="259.5"/>
        <point2 x="211.8015" y="205.5"/>
        <ID1>9</ID1>
        <ID2>6</ID2>
        <ctrlPoint0 x="128.925" y="233.0" xsi:type="point"/>
    </child>
    <child ID="25" label="return" x="106.925" y="205.5"
        width="125.224106" height="147.0" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="232.14911" y="352.5"/>
        <point2 x="232.08325" y="205.5"/>
        <ID1>12</ID1>
        <ID2>6</ID2>
        <ctrlPoint0 x="106.925" y="283.0" xsi:type="point"/>
    </child>
    <child ID="26" label="return" x="105.81415" y="285.4721"
        width="113.676956" height="67.02789" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="219.4911" y="352.5"/>
        <point2 x="210.17499" y="285.4721"/>
        <ID1>12</ID1>
        <ID2>9</ID2>
        <ctrlPoint0 x="105.81415" y="310.0" xsi:type="point"/>
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
