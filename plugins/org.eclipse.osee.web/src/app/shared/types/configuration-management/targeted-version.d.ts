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
export interface targetedVersion {
	id: number;
	name: string;
	Description: string | null;
	workflow: [];
	Name: string;
	'ats.Released': boolean;
	'ats.Next Version': boolean;
	'ats.Baseline Branch Id': string;
	'ats.Allow Create Branch': boolean;
	'ats. Allow Commit Branch': boolean;
}
