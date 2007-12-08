<!-- Do Not Remove: VUE mapping @version(1.1) jar:file:/C:/Program%20Files/vue/VUE.jar!/tufts/vue/resources/lw_mapping_1_1.xml -->
<!-- Do Not Remove: Saved date Mon Sep 10 10:05:56 MST 2007 by b0727536 on platform Windows XP 5.1 in JVM 1.5.0_07-b03 -->
<!-- Do Not Remove: Saving version @(#)VUE: built October 17 2006 at 0400 by vue on Linux 2.4.21-37.EL i386 JVM 1.5.0_06-b05 -->
<?xml version="1.0" encoding="US-ASCII"?>
<LW-MAP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="none" ID="0"
    label="osee_ats_defaultSimpleTeam_workflow.vue" x="131.425" y="81.0"
    width="341.17502" height="287.0" strokeWidth="0.0" autoSized="false">
    <fillColor>#ffffff</fillColor>
    <strokeColor>#404040</strokeColor>
    <font>SansSerif-plain-14</font>
    <nodeFilter/>
    <child ID="4" label="Endorse" x="210.17499" y="81.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage startPage="true" pageId="osee.ats.defaultSimpleTeam.Endorse"&gt;%nl;%nl;&lt;XWidget displayName="Title" storageName="Name" required="true" xwidgetType="XTextDam"/&gt;%nl;%nl;&lt;XWidget displayName="Description" storageName="ats.Description" required="true" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Change Type" storageName="ats.Change Type" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" beginComposite="6"/&gt;%nl;%nl;&lt;XWidget displayName="Priority" storageName="ats.Priority" xwidgetType="XComboDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" horizontalLabel="true" /&gt;%nl;%nl;&lt;XWidget displayName="Need By" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;XWidget displayName="Validation Required" storageName="ats.Validation Required" xwidgetType="XCheckBoxDam" horizontalLabel="true" endComposite="true" labelAfter="true"/&gt;%nl;%nl;&lt;XWidget displayName="User Community" storageName="ats.User Community" xwidgetType="XListDam(OPTIONS_FROM_ATTRIBUTE_VALIDITY)" required="true"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="53" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="8" label="default" x="251.88748" y="110.50391"
        width="10.0" height="111.99609" strokeWidth="1.0"
        autoSized="false" controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.5797" y="110.50391"/>
        <point2 x="257.19525" y="222.5"/>
        <ID1>4</ID1>
        <ID2>12</ID2>
    </child>
    <child ID="12" label="InWork" x="205.17497" y="222.5" width="104.2"
        height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultSimpleTeam.InWork" atsRequireStateHourSpentPrompt="true"&gt;%nl;%nl;&lt;XWidget displayName="Resolution" storageName="ats.Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="44" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="14" label="Completed" x="205.17499" y="338.5"
        width="108.399994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultSimpleTeam.Completed" validatePage="NonBlocking"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="68" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="16" label="default" x="253.32498" y="251.5" width="10.0"
        height="87.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="257.53748" y="251.5"/>
        <point2 x="259.1125" y="338.5"/>
        <ID1>12</ID1>
        <ID2>14</ID2>
    </child>
    <child ID="17" label="Cancelled" x="367.90002" y="159.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.defaultSimpleTeam.Cancelled"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="63" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="18" x="286.89423" y="110.5" width="102.71158"
        height="49.000008" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="286.89423" y="110.49999"/>
        <point2 x="389.6058" y="159.5"/>
        <ID1>4</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="22" x="294.72754" y="188.5" width="87.819855"
        height="34.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="294.72754" y="222.5"/>
        <point2 x="382.5474" y="188.5"/>
        <ID1>12</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="26" label="return" x="131.925" y="110.5"
        width="103.57109" height="112.0" strokeWidth="1.0"
        autoSized="false" controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="224.22815" y="222.5"/>
        <point2 x="235.4961" y="110.5"/>
        <ID1>12</ID1>
        <ID2>4</ID2>
        <ctrlPoint0 x="131.925" y="182.0" xsi:type="point"/>
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
