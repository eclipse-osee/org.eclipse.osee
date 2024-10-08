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
export type TrainingRoleRecord = {
	userName: string;
	roleName: string;
	startDate: string;
	endDate: string;
};
export class DefaultTrainingRoleRecord implements TrainingRoleRecord {
	public userName = '';
	public roleName = '';
	public startDate = '';
	public endDate = '';
}
