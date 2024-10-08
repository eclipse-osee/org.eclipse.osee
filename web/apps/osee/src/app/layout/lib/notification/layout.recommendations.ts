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
export type tableRecommendations = {
	width: string;
	columns: number;
};
export type pageState = {
	xsmall: boolean;
	small: boolean;
	medium: boolean;
	large: boolean;
	xlarge: boolean;
	recommendedCardColumnCount: number;
	tableRecommendations: tableRecommendations;
};
