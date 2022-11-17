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
import navigationStructure from '../../../src/app/navigation/top-level-navigation/top-level-navigation-structure';
export const amountOfPages = navigationStructure.length;

export const amountOfMessagePages =
	navigationStructure
		.find((v) => v.cypressLabel === 'ple')
		?.children.find((v) => v.cypressLabel === 'messaging')?.children
		.length || 0;
