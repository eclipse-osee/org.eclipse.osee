<!-- Do Not Remove: VUE mapping @version(1.1) jar:file:/C:/Program%20Files/vue/VUE.jar!/tufts/vue/resources/lw_mapping_1_1.xml -->
<!-- Do Not Remove: Saved date Fri Sep 28 10:39:26 MST 2007 by b0727536 on platform Windows XP 5.1 in JVM 1.5.0_07-b03 -->
<!-- Do Not Remove: Saving version @(#)VUE: built October 17 2006 at 0400 by vue on Linux 2.4.21-37.EL i386 JVM 1.5.0_06-b05 -->
<?xml version="1.0" encoding="US-ASCII"?>
<LW-MAP xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="none" ID="0"
    label="osee_ats_decisionReview_workflow(AAABEu5a1BQAaR82IjQ3GQ) Fri Sep 28 10;39;03 MST 2007.xml"
    x="110.425" y="-9.0" width="337.09998" height="378.0"
    strokeWidth="0.0" autoSized="false">
    <fillColor>#ffffff</fillColor>
    <strokeColor>#404040</strokeColor>
    <font>SansSerif-plain-14</font>
    <nodeFilter/>
    <child ID="4" label="Prepare" x="209.17499" y="-8.5"
        width="92.649994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage startPage="true" pageId="osee.ats.decisionReview.Prepare"&gt;%nl;%nl;&lt;XWidget displayName="Title" storageName="Name" required="true" xwidgetType="XTextDam"/&gt;%nl;%nl;&lt;XWidget displayName="Decision Review Options" storageName="ats.Decision Review Options" required="true" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Description" storageName="ats.Description" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;XWidget displayName="Blocking Review" storageName="ats.Blocking Review" xwidgetType="XComboDam(yes,no)" required="true" horizontalLabel="true" beginComposite="4"/&gt;%nl;%nl;&lt;XWidget displayName="Need By" storageName="ats.Need By" xwidgetType="XDateDam" horizontalLabel="true"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="51" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="8" label="default" x="250.88748" y="20.5" width="10.0"
        height="105.99609" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="255.58324" y="20.5"/>
        <point2 x="256.19174" y="126.49609"/>
        <ID1>4</ID1>
        <ID2>12</ID2>
    </child>
    <child ID="12" label="Decision" x="204.17499" y="126.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.decisionReview.Decision"&gt;%nl;%nl;&lt;XWidget displayName="Question" storageName="Name" xwidgetType="XLabelDam"/&gt;%nl;%nl;&lt;XWidget displayName="Decision" storageName="ats.Decision" required="true" xwidgetType="XComboDam(1,2,3)" /&gt;%nl;%nl;&lt;XWidget displayName="Resolution" storageName="ats.Resolution" xwidgetType="XTextDam" fill="Vertically"/&gt;%nl;%nl;&lt;/WorkPage&gt;%nl;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="54" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="14" label="Completed" x="205.17499" y="339.5"
        width="108.399994" height="29.0" strokeWidth="1.0"
        autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.decisionReview.Completed"&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="68" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="16" label="default" x="252.82501" y="155.5" width="10.0"
        height="184.0" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="256.48602" y="155.5"/>
        <point2 x="259.16397" y="339.5"/>
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
    <child ID="18" x="284.82465" y="20.500008" width="76.85074"
        height="37.999985" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="284.82465" y="20.500008"/>
        <point2 x="361.67538" y="58.499992"/>
        <ID1>4</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="22" x="285.00308" y="87.5" width="77.2688" height="39.0"
        strokeWidth="1.0" autoSized="false" controlCount="0"
        arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="285.00308" y="126.5"/>
        <point2 x="362.27188" y="87.5"/>
        <ID1>12</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="26" label="return" x="110.925" y="20.5" width="122.03372"
        height="106.0" strokeWidth="1.0" autoSized="false"
        controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="206.09464" y="126.5"/>
        <point2 x="232.95872" y="20.5"/>
        <ID1>12</ID1>
        <ID2>4</ID2>
        <ctrlPoint0 x="110.925" y="99.0" xsi:type="point"/>
    </child>
    <child ID="27" label="Followup" x="342.82498" y="233.5"
        width="104.2" height="29.0" strokeWidth="1.0" autoSized="false" xsi:type="node">
        <notes>&lt;WorkPage pageId="osee.ats.decisionReview.Followup"&gt;%nl;%nl;&lt;XWidget displayName="Resolution" storageName="ats.Resolution" xwidgetType="XTextDam" horizontalLabel="true" %sp;fill="Vertically"/&gt;%nl;%nl;&lt;/WorkPage&gt;</notes>
        <fillColor>#7f9cc6</fillColor>
        <strokeColor>#776d6d</strokeColor>
        <font>Arial-plain-14</font>
        <textBox width="56" height="17"/>
        <nodeFilter/>
        <shape xsi:type="rectangle"/>
    </child>
    <child ID="28" x="275.06403" y="155.50002" width="101.07193"
        height="77.99997" strokeWidth="1.0" autoSized="false"
        controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="275.06403" y="155.50002"/>
        <point2 x="376.13596" y="233.49998"/>
        <ID1>12</ID1>
        <ID2>27</ID2>
    </child>
    <child ID="29" x="387.96252" y="87.5" width="10.0" height="146.0"
        strokeWidth="1.0" autoSized="false" controlCount="0"
        arrowState="3" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <nodeFilter/>
        <point1 x="394.5998" y="233.5"/>
        <point2 x="391.32523" y="87.5"/>
        <ID1>27</ID1>
        <ID2>17</ID2>
    </child>
    <child ID="30" label="default" x="277.91724" y="262.5"
        width="98.465485" height="77.0" strokeWidth="1.0"
        autoSized="false" controlCount="0" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="37" height="15"/>
        <nodeFilter/>
        <point1 x="376.38272" y="262.5"/>
        <point2 x="277.91724" y="339.5"/>
        <ID1>27</ID1>
        <ID2>14</ID2>
    </child>
    <child ID="31" label="return" x="308.375" y="132.0" width="83.04999"
        height="101.500015" strokeWidth="1.0" autoSized="false"
        controlCount="1" arrowState="2" xsi:type="link">
        <strokeColor>#404040</strokeColor>
        <textColor>#404040</textColor>
        <font>Arial-plain-12</font>
        <textBox width="32" height="15"/>
        <nodeFilter/>
        <point1 x="391.425" y="233.5"/>
        <point2 x="308.375" y="136.76231"/>
        <ID1>27</ID1>
        <ID2>12</ID2>
        <ctrlPoint0 x="366.925" y="132.0" xsi:type="point"/>
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
