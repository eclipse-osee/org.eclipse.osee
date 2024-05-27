/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import type { applic } from '@osee/applicability/types';
import type { enumeration, enumerationSet } from './enum';
import type { PlatformType } from './platformType';

export type newTypeDialogData = {
	fields: logicalTypefieldValue[];
};
export type logicalTypefieldValue = {
	name: string;
	value: string;
};
export type newPlatformTypeDialogReturnData = {
	platformType: PlatformType;
	createEnum: boolean;
	enumSet?: enumerationSet;
	enumSetId: string;
	enumSetName: string;
	enumSetDescription: string;
	enumSetApplicability: applic;
	enums: enumeration[];
};
