<!-- Do Not Remove: VUE mapping @version(1.1) jar:file:/C:/Program%20Files/vue/VUE.jar!/tufts/vue/resources/lw_mapping_1_1.xml -->
<!-- Do Not Remove: Saved date Thu Mar 06 20:33:22 MST 2008 by b0727536 on platform Windows XP 5.1 in JVM 1.5.0_07-b03 -->
<!-- Do Not Remove: Saving version @(#)VUE: built October 17 2006 at 0400 by vue on Linux 2.4.21-37.EL i386 JVM 1.5.0_06-b05 -->
<?xml version="1.0" encoding="US-ASCII"?>
<LW-MAP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="none" ID="0"
    label="demo_req_workflow.vue" x="105.31415" y="79.0"
    width="367.2859" height="376.0" strokeWidth="0.0" autoSized="false">
    <fillColor>#ffffff</fillColor>
    <strokeColor>#404040</strokeColor>
    <font>SansSerif-plain-14</font>
    <nodeFilter/>
    <child ID="4" label="Endorse" x="210.17499" y="79.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage startPage="true" pageId="apache.blk3.req.Endorse"&gt;%nl;%nl;&lt;XWidget displayName="Legacy PCR Number" storageName="ats.Legacy PCR Id" horizontalLabel="true" xwidgetType="XLabelDam"/&gt;%nl;%nl;&lt;XWidget displayName="Title" storageName="Name" required="true" xwidgetType="XTextDam"/&gt;%nl;%nl;&lt;XWidget displayName="Description" storageName="ats.Description" required="true" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution Override" storageName="ats.Proposed Resolution Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution" storageName="ats.Proposed Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Change Type" storageName="ats.Change Type" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" beginComposite="4"/&gt;%nl;%nl;&lt;XWidget displayName="Priority" storageName="ats.Priority" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" endComposite="true"/&gt;%nl;%nl;&lt;XWidget displayName="Deadline" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true" beginComposite="6"/&gt;%nl;%nl;&lt;XWidget displayName="Validation Required" storageName="ats.Validation Required" xwidgetType="XCheckBoxDam" horizontalLabel="true" endComposite="true" labelAfter="true"/&gt;%nl;%nl;&lt;XWidget displayName="Metrics from Tasks" storageName="ats.Metrics from Tasks" xwidgetType="XCheckBoxDam" horizontalLabel="true" endComposite="true" labelAfter="true"/&gt;%nl;%nl;&lt;XWidget displayName="User Community" storageName="ats.User Community" xwidgetType="XListDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" required="true"/&gt;%nl;%nl;&lt;XOrRequired xWidgets="ats.Proposed Resolution Override;ats.Proposed Resolution"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="53" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="6" label="Analyze" x="208.17499" y="179.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.req.Analyze" atsRequireStateHourSpentPrompt="true"&gt;%nl;%nl;&lt;XWidget displayName="Problem Override" storageName="ats.Problem Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Problem" storageName="ats.Problem" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution Override" storageName="ats.Proposed Resolution Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Proposed Resolution" storageName="ats.Proposed Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Change Type" storageName="ats.Change Type" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" beginComposite="6"/&gt;%nl;%nl;&lt;XWidget displayName="Priority" storageName="ats.Priority" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" /&gt;%nl;%nl;&lt;XWidget displayName="Deadline" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true" endComposite="true" /&gt;%nl;%nl;&lt;XWidget displayName="Estimated Hours" storageName="ats.Estimated Hours" xwidgetType="XFloatDam" required="true" horizontalLabel="true" /&gt;%nl;%nl;&lt;XOrRequired xWidgets="ats.Problem Override;ats.Problem"/&gt;%nl;&lt;XOrRequired xWidgets="ats.Proposed Resolution Override;ats.Proposed Resolution"/&gt;%nl;%nl;&lt;/WorkPage&gt;%nl;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="49" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="8" label="default" x="250.49998" y="108.50098"
        width="10.0" height="70.99902" strokeWidth="1.0"
        autoSized="false" controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.21" y="108.50098"/>
        <point2 x="254.78998" y="179.5"/>
        <ID1>4</ID1>
        <ID2>6</ID2>
    </child>
    <child ID="9" label="Authorize" x="206.17499" y="257.5"
        width="99.75" height="28.5" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.req.Authorize"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="59" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="10" label="default" x="250.2775" y="208.5" width="10.0"
        height="49.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="254.78906" y="208.5"/>
        <point2 x="255.76591" y="257.5"/>
        <ID1>6</ID1>
        <ID2>9</ID2>
    </child>
    <child ID="12" label="Implement" x="206.17499" y="350.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.req.Implement" atsRequireStateHourSpentPrompt="true" atsAllowCreateBranch="true" atsAllowCommitBranch="true"&gt;%nl;%nl;&lt;XWidget displayName="Resolution Override" storageName="ats.Resolution Override" xwidgetType="XCheckBoxDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Resolution" storageName="ats.Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XOrRequired xWidgets="ats.Resolution Override;ats.Resolution"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="64" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="13" label="default" x="252.15948" y="285.99902"
        width="10.0" height="64.50098" strokeWidth="1.0"
        autoSized="false" controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.38998" y="285.99902"/>
        <point2 x="257.92902" y="350.5"/>
        <ID1>9</ID1>
        <ID2>12</ID2>
    </child>
    <child ID="14" label="Completed" x="206.17499" y="425.5"
        width="108.399994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.req.Completed" validatePage="NonBlocking"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="68" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="16" label="default" x="254.32501" y="379.5" width="10.0"
        height="46.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="258.681" y="379.5"/>
        <point2 x="259.969" y="425.5"/>
        <ID1>12</ID1>
        <ID2>14</ID2>
    </child>
    <child ID="17" label="Cancelled" x="367.90002" y="159.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="apache.blk3.req.Cancelled"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="63" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="18" x="286.1344" y="108.5" width="104.23126"
        height="50.999992" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="286.1344" y="108.50001"/>
        <point2 x="390.36566" y="159.5"/>
        <ID1>4</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="20" x="300.82495" y="179.34895" width="67.07504"
        height="10.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="300.82498" y="188.40182"/>
        <point2 x="367.90002" y="180.29608"/>
        <ID1>6</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="21" x="279.95068" y="188.5" width="115.7294"
        height="69.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="279.95065" y="257.5"/>
        <point2 x="395.68005" y="188.5"/>
        <ID1>9</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="22" x="270.55255" y="188.5" width="137.16995"
        height="162.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="270.55252" y="350.5"/>
        <point2 x="407.72247" y="188.5"/>
        <ID1>12</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="24" label="return" x="128.925" y="208.3872"
        width="80.37579" height="49.112793" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="209.3008" y="257.5"/>
        <point2 x="208.17499" y="208.3872"/>
        <ID1>9</ID1>
        <ID2>6</ID2>
        <ctrlPoint0 x="128.925" y="233.0" xsi:type="point"/>
    </child>
    <child ID="25" label="return" x="106.925" y="208.5" width="124.5869"
        height="142.0" strokeWidth="1.0" autoSized="false"
        controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="231.5119" y="350.5"/>
        <point2 x="230.45688" y="208.5"/>
        <ID1>12</ID1>
        <ID2>6</ID2>
        <ctrlPoint0 x="106.925" y="283.0" xsi:type="point"/>
    </child>
    <child ID="26" label="return" x="105.81415" y="284.44812"
        width="112.2666" height="66.05185" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="218.08075" y="350.5"/>
        <point2 x="206.17499" y="284.44815"/>
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
