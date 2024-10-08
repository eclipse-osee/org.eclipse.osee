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
import { applic } from '@osee/applicability/types';

export type featureConstraint = {
	applicability1: applic;
	applicability2: applic;
};
export type featureConstraintData = {
	featureConstraint: featureConstraint;
};
export const defaultFeatureConstraint = {
	applicability1: {
		id: '',
		name: '',
	},
	applicability2: {
		id: '',
		name: '',
	},
};
// mapping used for GET response
export type applicWithConstraints = {
	id: string;
	name: string;
	constraints: applicWithConstraints[];
};
