/*********************************************************************
 * Copyright (c) 2024 Boeing
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
export const PRIORITIES = {
	LowestPriority: '1',
	LowPriority: '2',
	MediumPriority: '3',
	HighPriority: '4',
	HighestPriority: '5',
} as const;

export type Priority = (typeof PRIORITIES)[keyof typeof PRIORITIES];
