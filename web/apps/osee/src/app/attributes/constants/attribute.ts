/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
export const ATTRIBUTETYPEIDENUM = {
	NAME: '1152921504606847088',
	NAMEABBREV: '8355308043647703563',
	DESCRIPTION: '1152921504606847090',
	NOTES: '1152921504606847085',
	ACTIVE: '1152921504606847065',
	INTERFACEUNITMEASUREMENT: '2478822847543373494',
	INTERFACENODEBACKGROUNDCOLOR: '5221290120300474048',
	INTERFACENODEADDRESS: '5726596359647826656',
	INTERFACENODENUMBER: '5726596359647826657',
	INTERFACENODEGROUPID: '5726596359647826658',
	INTERFACENODECODEGENNAME: '5390401355909179776',
	INTERFACENODETYPE: '6981431177168910500',
	INTERFACENODECODEGEN: '4980834335211418740',
	INTERFACENODEBUILDCODEGEN: '5806420174793066197',
	INTERFACENODETOOLUSE: '5863226088234748106',
	INTERFACETRANSPORTTYPE: '4522496963078776538',
	INTERFACEMESSAGENUMBER: '2455059983007225768',
	INTERFACEMESSAGEPERIODICITY: '3899709087455064789',
	INTERFACEMESSAGERATE: '2455059983007225763',
	INTERFACEMESSAGETYPE: '2455059983007225770',
	INTERFACEMESSAGEWRITEACCESS: '2455059983007225754',
	INTERFACEMESSAGEDOUBLEBUFFER: '5156869772694848711',
	INTERFACEMESSAGEEXCLUDE: '2455059983007225811',
	INTERFACEMESSAGEIOCODE: '2455059983007225813',
	INTERFACEMESSAGEMODECODE: '2455059983007225810',
	INTERFACEMESSAGERATEVER: '2455059983007225805',
	INTERFACEMESSAGEPRIORITY: '2455059983007225806',
	INTERFACEMESSAGEPROTOCOL: '2455059983007225809',
	INTERFACEMESSAGERPTWORDCOUNT: '2455059983007225807',
	INTERFACEMESSAGERPTCMDWORD: '2455059983007225808',
	INTERFACEMESSAGERUNBEFOREPROC: '2455059983007225812',
	INTERFACEMESSAGEVER: '2455059983007225804',
	INTERFACESUBMESSAGENUMBER: '2455059983007225769',
	INTERFACEMAXSIMULTANEITY: '2455059983007225756',
	INTERFACEMINSIMULTANEITY: '2455059983007225755',
	INTERFACESTRUCTURECATEGORY: '2455059983007225764',
	INTERFACETASKFILETYPE: '2455059983007225760',
	INTERFACEELEMENTALTERABLE: '2455059983007225788',
	INTERFACEELEMENTSTART: '2455059983007225801',
	INTERFACEELEMENTEND: '2455059983007225802',
	INTERFACEELEMENTBLOCKDATA: '1523923981411079299',
	INTERFACEELEMENTARRAYHEADER: '3313203088521964923',
	INTERFACEELEMENTWRITEARRAYHEADERNAME: '3313203088521964924',
	INTERFACEELEMENTARRAYINDEXDELIMITERONE: '6818939106523472582',
	INTERFACEELEMENTARRAYINDEXDELIMITERTWO: '6818939106523472583',
	INTERFACELEMENTARRAYINDEXORDER: '6818939106523472581',
	INTERFACEPLATFORMTYPEUNITS: '4026643196432874344',
	INTERFACEPLATFORMTYPEBITSIZE: '2455059983007225786',
	INTERFACEPLATFORMTYPEMINVAL: '3899709087455064782',
	INTERFACEPLATFORMTYPEMAXVAL: '3899709087455064783',
	INTERFACEDEFAULTVAL: '2886273464685805413',
	INTERFACEPLATFORMTYPEMSBVAL: '3899709087455064785',
	INTERFACEPLATFORMTYPEANALOGACCURACY: '3899709087455064788',
	INTERFACEPLATFORMTYPEBITSRESOLUTION: '3899709087455064786',
	INTERFACEPLATFORMTYPECOMPRATE: '3899709087455064787',
	INTERFACEPLATFORMTYPE2SCOMPLEMENT: '3899709087455064784',
	INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION: '2121416901992068417',
	INTERFACEENUMLITERAL: '2455059983007225803',
	BYTEALIGNVALIDATION: '1682639796635579163',
	BYTEALIGNVALIDATIONSIZE: '6745328086388470469',
	MESSAGEGENERATION: '6696101226215576386',
	MESSAGEGENERATIONTYPE: '7121809480940961886',
	MESSAGEGENERATIONPOSITION: '7004358807289801815',
	MINIMUMSUBSCRIBERMULTIPLICITY: '6433031401579983113',
	MAXIMUMSUBSCRIBERMULTIPLICITY: '7284240818299786725',
	MINIMUMPUBLISHERMULTIPLICITY: '7904304476851517',
	MAXIMUMPUBLISHERMULTIPLICITY: '8536169210675063038',
	INTERFACELEVELSTOUSE: '1668394842614655222',
	AVAILABLEMESSAGEHEADERS: '2811393503797133191',
	AVAILABLESUBMESSAGEHEADERS: '3432614776670156459',
	AVAILABLESTRUCTUREHEADERS: '3020789555488549747',
	AVAILABLEELEMENTHEADERS: '3757258106573748121',
	SPAREAUTONUMBERING: '6696101226215576390',
	DASHEDPRESENTATION: '3564212740439618526',
	LOGICALTYPE: '2455059983007225762',
	INTERFACEENUMORDINAL: '2455059983007225790',
	INTERFACEENUMORDINALTYPE: '2664267173310317306',
	CROSSREFVALUE: '1761323951115447407',
	CROSSREFADDITIONALCONTENT: '6645243569977378792',
	CROSSREFARRAYVALUES: '1395395257321371828',
	PRODUCT_TYPE: '4522673803793808650',
	MULTIVALUED: '3641431177461038717',
	FEATUREVALUE: '31669009535111027',
	DEFAULTVALUE: '2221435335730390044',
	VALUE: '861995499338466438',
	MARKDOWNCONTENT: '1152921504606847900',
	CONTENTURL: '1152921504606847100',
	COMMANDTIMESTAMP: '6908130616864675217',
	FAVORITE: '251612632392915007',
	HTTPMETHOD: '2412383418964323219',
	ISVALIDATED: '729356860089871',
	PARAMETERIZEDCOMMAND: '8062747461195678171',
	SCRIPTSUBSYSTEM: '1152921504606848173',
	TEAMNAME: '1152921504606847354',
	TESTRESULTSTOKEEP: '6846375894770628832',
} as const;

export type ATTRIBUTETYPEID =
	(typeof ATTRIBUTETYPEIDENUM)[keyof typeof ATTRIBUTETYPEIDENUM];

export const BASEATTRIBUTETYPEIDENUM = {
	NAME: ATTRIBUTETYPEIDENUM.NAME,
	DESCRIPTION: ATTRIBUTETYPEIDENUM.DESCRIPTION,
};

export type BASEATTRIBUTETYPEID =
	(typeof BASEATTRIBUTETYPEIDENUM)[keyof typeof BASEATTRIBUTETYPEIDENUM];
