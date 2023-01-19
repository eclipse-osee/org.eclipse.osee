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
const _gcAttributeTypeId = {
	CONTENT_URL: '1152921504606847100',
	DESCRIPTION: '1152921504606847090',
	EXECUTION_FREQUNCY: '5494590235875265429',
	COMMAND_TIMESTAMP: '6908130616864675217',
	PARAMETERIZED_COMMAND: '8062747461195678171',
	FAVORITE: '2516126323929150072',
} as const;
type gcAttributeTypeId =
	(typeof _gcAttributeTypeId)[keyof typeof _gcAttributeTypeId];
