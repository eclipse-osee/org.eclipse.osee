<!-- Do Not Remove: VUE mapping @version(1.1) jar:file:/C:/Program%20Files/vue/VUE.jar!/tufts/vue/resources/lw_mapping_1_1.xml -->
<!-- Do Not Remove: Saved date Fri Sep 28 10:38:04 MST 2007 by b0727536 on platform Windows XP 5.1 in JVM 1.5.0_07-b03 -->
<!-- Do Not Remove: Saving version @(#)VUE: built October 17 2006 at 0400 by vue on Linux 2.4.21-37.EL i386 JVM 1.5.0_06-b05 -->
<?xml version="1.0" encoding="US-ASCII"?>
<LW-MAP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="none" ID="0"
    label="osee_ats_peerToPeerReview_workflow(AAABFNKhB4EBzlr5Twr6hA) Fri Sep 28 10;35;49 MST 2007.xml"
    x="110.425" y="-7.0" width="333.17502" height="307.0"
    strokeWidth="0.0" autoSized="false">
    <fillColor>#ffffff</fillColor>
    <strokeColor>#404040</strokeColor>
    <font>SansSerif-plain-14</font>
    <nodeFilter/>
    <child ID="4" label="Prepare" x="210.17499" y="-6.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage startPage="true" pageId="osee.ats.peerToPeerReview.Prepare"&gt;%nl;%nl;&lt;XWidget displayName="Title" storageName="Name" required="true" xwidgetType="XTextDam"/&gt;%nl;%nl;&lt;XWidget displayName="Related State of Parent Workflow" storageName="ats.Related To State" horizontalLabel="true" xwidgetType="XLabelDam" beginComposite="4"/&gt;%nl;%nl;&lt;XWidget displayName="Legacy PCR Number" storageName="ats.Legacy PCR Id" horizontalLabel="true" xwidgetType="XLabelDam" endComposite="true"/&gt;%nl;%nl;&lt;XWidget displayName="Review Roles" storageName="ats.Role" required="true" xwidgetType="XUserRoleViewer" /&gt;%nl;%nl;&lt;XWidget displayName="Location of review materials" storageName="ats.Location" required="true" xwidgetType="XTextResourceDropDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Description" storageName="ats.Description" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Blocking Review" storageName="ats.Blocking Review" xwidgetType="XComboDam(yes,no)" required="true" horizontalLabel="true" beginComposite="4"/&gt;%nl;%nl;&lt;XWidget displayName="Need By" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="51" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="8" label="default" x="252.38751" y="22.501953"
        width="10.0" height="104.99805" strokeWidth="1.0"
        autoSized="false" controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.69208" y="22.501953"/>
        <point2 x="258.08292" y="127.5"/>
        <ID1>4</ID1>
        <ID2>12</ID2>
    </child>
    <child ID="12" label="Review" x="206.17499" y="127.5" width="104.2"
        height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.peerToPeerReview.Review"&gt;%nl;%nl;&lt;XWidget displayName="Review Roles" storageName="ats.Role" required="true" xwidgetType="XUserRoleViewer" /&gt;%nl;%nl;&lt;XWidget displayName="Review Defect" storageName="ats.Review Defect" required="false" xwidgetType="XDefectViewer" /&gt;%nl;%nl;&lt;XWidget displayName="Resolution" storageName="ats.Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;/WorkPage&gt;%nl;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="45" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="14" label="Completed" x="202.17499" y="270.5"
        width="108.399994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.peerToPeerReview.Completed"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="68" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="16" label="default" x="252.32501" y="156.5" width="10.0"
        height="114.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="258.08234" y="156.5"/>
        <point2 x="256.56766" y="270.5"/>
        <ID1>12</ID1>
        <ID2>14</ID2>
    </child>
    <child ID="17" label="Cancelled" x="338.90002" y="58.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.decisionReview.Cancelled"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="63" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="18" x="286.50385" y="22.499992" width="74.49237"
        height="36.000015" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="286.50385" y="22.499992"/>
        <point2 x="360.99622" y="58.500008"/>
        <ID1>4</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="22" x="286.1665" y="87.5" width="76.941986" height="40.0"
        strokeWidth="1.0" autoSized="false" controlCount="0"
        arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="286.16653" y="127.5"/>
        <point2 x="363.10852" y="87.5"/>
        <ID1>12</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="26" label="return" x="110.925" y="22.5" width="122.37897"
        height="105.0" strokeWidth="1.0" autoSized="false"
        controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="208.58722" y="127.5"/>
        <point2 x="233.30397" y="22.5"/>
        <ID1>12</ID1>
        <ID2>4</ID2>
        <ctrlPoint0 x="110.925" y="99.0" xsi:type="point"/>
    </child>
    <userZoom>1.0</userZoom>
    <userOrigin x="-21.074997" y="-158.0"/>
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
