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
import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	iif,
	map,
	mergeMap,
	of,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { CommandGroupOptionsService } from '../services/data-services/commands/command-group-options.service';
import { CommandFromUserHistoryService } from '../services/data-services/selected-command-data/command-from-history/command-from-user-history.service';
import { ColumnSortingService } from '../services/datatable-services/column-sorting/column-sorting.service';
import { DataTableService } from '../services/datatable-services/datatable.service';
import { DeleteRowService } from '../services/datatable-services/row-actions/delete-row.service';
import { FilterService } from '../services/datatable-services/filter/filter.service';
import { RowObjectActionsService } from '../services/datatable-services/row-actions/row-object-actions.service';
import { commandHistoryObject } from '../types/grid-commander-types/executedCommand';
import { RowObj } from '../types/grid-commander-types/table-data-types';
import { DeleteRowDialogComponent } from './delete-row-dialog/delete-row-dialog.component';

@Component({
	selector: 'osee-gc-datatable',
	templateUrl: './gc-datatable.component.html',
	styleUrls: ['./gc-datatable.component.sass'],
})
export class GcDatatableComponent implements AfterViewInit, OnDestroy {
	@ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
	@ViewChild(MatSort, { static: false }) sort!: MatSort;

	selection = new SelectionModel<RowObj>(true, []);

	private _rowsToBeSelected = new BehaviorSubject<RowObj[]>([]);
	hiddenRows = new BehaviorSubject<RowObj[]>([]);
	private _updateMatSort = new BehaviorSubject<boolean>(false);

	filterValue = this.filterService.constructFilterString;
	filterColumns = this.filterService.rowObjPropertiesForFilter;
	filterVisibleColumns = this.filterService.columnOptionsForFilter;

	//Flags for Action Row Icons:
	canEditRowViaAction =
		this.dataTableService.canEditableViaActionIcon.asObservable();
	canAddRowViaAction =
		this.dataTableService.canAddRowViaActionIcon.asObservable();
	canHideRowViaAction =
		this.dataTableService.canHideViaActionIcon.asObservable();
	multiRowDeleteActionIcon =
		this.dataTableService.multiRowDeleteActionIcon.asObservable();

	columnData = this.dataTableService.columnSchema;
	displayedColumns = this.dataTableService.displayedCols;
	displayedTableData = this.dataTableService.displayedTableData;

	//using this observable we are able to set the paginator, MatSort, the data used in the table, as well as the _isSelected behvaior subject
	dataSource = combineLatest([
		this.displayedTableData,
		this._updateMatSort,
		this.filterValue,
		this.filterColumns,
		this.filterVisibleColumns,
	]).pipe(
		switchMap(([data, update, filter, allColumns, visibleColumns]) =>
			iif(
				() => filter === '_$_$_$_$_$_$_$_',
				of(new MatTableDataSource(data)).pipe(
					tap((val) => this._rowsToBeSelected.next(val.data)),
					map((ds) => {
						if (this.paginator) {
							ds.paginator = this.paginator;
						}
						if (this.sort) {
							ds.sortData = this.sortFunction;
							ds.sort = this.sort;
						}
						return ds;
					})
				),
				//if there is a filter value that is provided the data table will udpated based on the filter value
				of(new MatTableDataSource(data)).pipe(
					tap((val) => this._rowsToBeSelected.next(val.data)),
					map((data) => {
						if (this.paginator) {
							data.paginator = this.paginator;
						}
						data.filter = filter;
						//this overrides the default filter funcitonality
						data.filterPredicate = (
							row: RowObj,
							filter: string
						) => {
							const matchingRows = [];
							//index 0 of filter when converted to an array of strings is the value used to search all columns in table
							const filterAllValue = filter.split('$')[0];
							//array that reflects the strings of each property for the Row object without index 0 ('all')
							const rowPropertiesArray = filter
								.split('$')
								.slice(1);
							//an array of properties of all columns that are available to be filtered through excluding 'all'
							const columnHeadersUsedForFilter =
								allColumns.slice(1);

							//check to search all columns
							if (
								filterAllValue !== '_' &&
								filterAllValue !== undefined &&
								filterAllValue !== ''
							) {
								//getting the indicies of the visible columns (after removing 'all' from array) from the array that aligns with each rows properties
								const indicesOfVisibleCols = visibleColumns
									.slice(1)
									.map((val) =>
										columnHeadersUsedForFilter.indexOf(val)
									);
								for (
									let i = 0;
									i < indicesOfVisibleCols.length;
									i++
								) {
									//using the numbers from indiciesOfVisibleCols we can see if each specified property of row contains the 'filterAllValue'
									const customFilter = row[
										Object.keys(row)[
											indicesOfVisibleCols[i]
										]
									]
										.toString()
										.includes(filterAllValue);
									//if the value does include the filter string then it will be pushed to the matchingRows array
									matchingRows.push(customFilter);
								}
								//we return rowObjects that have all true values inside of the matchingRows array
								//note filterStrings that are an empty string will return true
								return matchingRows.some(Boolean);
							}
							//for every column that is going to be filtered we iterate over the rowObject and check if the value of the property includes the filterString
							for (
								let i = 0;
								i < columnHeadersUsedForFilter.length;
								i++
							) {
								if (
									rowPropertiesArray[i] !== '_' &&
									rowPropertiesArray[i] !== undefined
								) {
									const customFilter = row[
										Object.keys(row)[i]
									]
										.toString()
										.includes(rowPropertiesArray[i]);
									//if the value does include the filter string then true will be pushed to the matchingRows array
									matchingRows.push(customFilter);
								}
							}
							return matchingRows.every(Boolean);
						};
						if (data.paginator) {
							data.paginator.firstPage();
						}
						if (this.sort) {
							data.sortData = this.sortFunction;
							data.sort = this.sort;
						}
						return data;
					})
				)
			)
		)
	);

	constructor(
		private dataTableService: DataTableService,
		private filterService: FilterService,
		public dialog: MatDialog,
		private commandGroupOptService: CommandGroupOptionsService,
		private commandFromUserHistoryService: CommandFromUserHistoryService,
		private rowObjectActionService: RowObjectActionsService,
		private columnSortingService: ColumnSortingService,
		private deleteRowService: DeleteRowService
	) {}

	ngOnDestroy(): void {
		this.dataTableService.doneFx = '';
	}

	ngAfterViewInit() {
		this._updateMatSort.next(true);
		this.filterService.rowObjPropertiesForFilter.subscribe();
		this.filterService.columnOptionsForFilter.subscribe();
	}

	sortData(sort: Sort, data: RowObj[]) {
		this._updateMatSort.next(true);
		return this.sortFunction;
	}

	sortFunction = (data: RowObj[], sort: Sort): RowObj[] => {
		if (!sort.active || sort.direction === '') {
			return data;
		}
		return this.columnSortingService.columnSort(
			sort.active,
			sort.direction === 'asc',
			data
		);
	};

	masterToggle() {
		this.isAllSelected()
			? this.selection.clear()
			: this._rowsToBeSelected.value.forEach((row) =>
					this.selection.select(row)
			  );
	}

	isAllSelected() {
		return (
			this.selection.selected.length ===
			this._rowsToBeSelected.value.length
		);
	}

	//toggles the row selected when checkbox is clicked
	onElementToggled(rowData: RowObj) {
		this.selection.toggle(rowData);
	}

	hideRow(rowData: RowObj) {
		let tempArr = this.hiddenRows.value;
		tempArr.push(rowData);

		this.hiddenRows.next(tempArr);
		this.selection.clear();
	}

	hideSelectedRows() {
		this.hiddenRows.next(this.selection.selected);
		this.selection.clear();
	}

	showHiddenRows() {
		this.hiddenRows.next([]);
	}

	onRowClicked(rowData: commandHistoryObject) {
		this.commandGroupOptService.stringToFilterCommandsBy = rowData.Command;
		this.commandFromUserHistoryService.selectedCommandFromHistoryTableId =
			rowData['Artifact Id'];
		this.commandFromUserHistoryService.fromHistory = true;
	}

	toggleFavorite(rowData: commandHistoryObject) {
		rowData.Favorite === 'false'
			? (rowData.Favorite = 'true')
			: (rowData.Favorite = 'false');
		const modifiedArtifact =
			this.rowObjectActionService.createModifiedFavoriteObject(rowData);
		this.rowObjectActionService
			.updateArtifactInDataTable(modifiedArtifact)
			.subscribe();
	}

	//DialogBox Component Functionality
	openDeleteRowDialog(action: string, obj: string[]) {
		const dialogRef = this.dialog.open(DeleteRowDialogComponent, {
			data: {
				action: action,
				object: obj,
			},
		});
		dialogRef
			.afterClosed()
			.pipe(
				filter(
					(dialogresponse) =>
						dialogresponse !== undefined &&
						dialogresponse.event !== undefined &&
						dialogresponse.data !== undefined
				),
				take(1),
				filter((dialogResponse) => dialogResponse.event === 'Delete'),
				map((response) => response?.data),
				mergeMap((data) =>
					this.deleteRowService
						.createModifiedObjectToDelete(data)
						.pipe(
							switchMap((artId) =>
								this.deleteRowService.deleteArtifactFromDataTable(
									artId
								)
							)
						)
				)
			)
			.subscribe();
	}

	public get rowsToBeSelectedVal() {
		return this._rowsToBeSelected;
	}
}
