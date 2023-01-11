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
export const ARTIFACTTYPEIDENUM = {
	ELEMENT_ARRAY: '6360154518785980502',
	ELEMENT: '2455059983007225765',
	STRUCTURE: '2455059983007225776',
	SUBMESSAGE: '126164394421696908',
	MESSAGE: '2455059983007225775',
	CONNECTION: '126164394421696910',
	TRANSPORTTYPE: '6663383168705248989',
	NODE: '6039606571486514295',
	PLATFORMTYPE: '6360154518785980503',
	ENUMSET: '2455059983007225791',
	ENUM: '2455059983007225793',
	CONFIGURATION_GROUP: '6',
	CONFIGURATION: '5849078277209560034',
	GLOBALUSERPREFERENCES: '5935321910901176667',
	FEATURE: '87',
} as const;

export type ARTIFACTTYPEID =
	(typeof ARTIFACTTYPEIDENUM)[keyof typeof ARTIFACTTYPEIDENUM];
