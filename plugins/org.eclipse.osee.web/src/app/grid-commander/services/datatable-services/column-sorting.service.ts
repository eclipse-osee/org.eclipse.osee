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
import { Injectable } from '@angular/core';
import { RowObj } from '../../types/grid-commander-types/table-data-types';

@Injectable({
	providedIn: 'root',
})
export class ColumnSortingService {
	constructor() {}

	columnSort(property: string, isAsc: boolean, data: RowObj[]) {
		//create array of favorites to sort
		const favoritedRows = data
			.filter((row) => row.Favorite === 'true')
			.sort((a: RowObj, b: RowObj) => {
				return this.sortDataObj(property, isAsc, a, b);
			});
		//creat array of non-favorites to sort
		const nonFavoriteRows = data
			.filter((row) => row.Favorite !== 'true')
			.sort((a: RowObj, b: RowObj) => {
				return this.sortDataObj(property, isAsc, a, b);
			});
		return [...favoritedRows, ...nonFavoriteRows];
	}

	sortDataObj(property: string, isAsc: boolean, objA: RowObj, objB: RowObj) {
		switch (property) {
			case 'Times Used':
				return this.compareNumberProps(
					objA[property],
					objB[property],
					isAsc
				);
			case 'Last Used':
				return this.compareDateProps(
					objA[property],
					objB[property],
					isAsc
				);
			default:
				return this.compareStringProps(
					objA[property],
					objB[property],
					isAsc
				);
		}
	}

	compareStringProps<T>(propA: T, propB: T, isAsc: boolean): number {
		return (propA < propB ? -1 : 1) * (isAsc ? 1 : -1);
	}

	compareNumberProps<T>(propA: T, propB: T, isAsc: boolean): number {
		return (Number(propA) < Number(propB) ? -1 : 1) * (isAsc ? 1 : -1);
	}

	compareDateProps<T>(propA: T, propB: T, isAsc: boolean): number {
		return (
			(Date.parse(String(propA)) < Date.parse(String(propB)) ? -1 : 1) *
			(isAsc ? 1 : -1)
		);
	}
}
