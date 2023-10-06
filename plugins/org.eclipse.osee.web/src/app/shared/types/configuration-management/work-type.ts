/*********************************************************************
 * Copyright (c) 2023 Boeing
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

/**
 * @see org.eclipse.osee.ats.api.config.WorkType for this definition
 */
export const WORKTYPES = [
	'Program',
	'Code',
	'MissionCode',
	'Test',
	'IntegrationTest',
	'SoftwareTest',
	'Test_Librarian',
	'Requirements',
	'ImplDetails',
	'Applicability',
	'SW_Design',
	'SW_TechAppr',
	'Test_Procedures',
	'SubSystems',
	'Software',
	'Hardware',
	'Issues',
	'Support',
	'Integration',
	'Systems',
	'ICDs',
	'PIDS',
	'SSDD',
	'Maintenance',
	'All',
	'Custom',
	'ARB',
	'MIM',
	'Change Requst',
	'None',
	'Problem Report',
] as const;

/**
 * @see org.eclipse.osee.ats.api.config.WorkType for this definition
 */
export type workType = (typeof WORKTYPES)[number];
