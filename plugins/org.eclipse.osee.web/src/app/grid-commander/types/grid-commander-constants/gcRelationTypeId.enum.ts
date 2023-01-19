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
const _gcRelationTypeId = {
	RELATION_ORDER: '1152921504606847089',
	DEFAULT_HIERARCHICAL: '2305843009213694292',
	USER_TO_CONTEXT: '3588536741885708579',
	USERGROUP_TO_CONTEXT: '6518538741815208374',
	USER_TO_HISTORY: '6360156234301395903',
	CONTEXT_TO_COMMAND: '3568736811283748971',
} as const;
type gcRelationTypeId =
	(typeof _gcRelationTypeId)[keyof typeof _gcRelationTypeId];
