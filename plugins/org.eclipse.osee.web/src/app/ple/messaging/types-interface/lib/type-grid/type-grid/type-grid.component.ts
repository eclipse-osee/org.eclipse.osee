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
import {
	BreakpointObserver,
	Breakpoints,
	BreakpointState,
} from '@angular/cdk/layout';
import {
	Component,
	Input,
	OnChanges,
	OnDestroy,
	SimpleChanges,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, of, OperatorFunction, Subject } from 'rxjs';
import {
	switchMap,
	filter,
	takeUntil,
	debounceTime,
	map,
	pairwise,
	scan,
	startWith,
} from 'rxjs/operators';
import { applic } from '@osee/shared/types/applicability';
import { CurrentTypesService } from '../../services/current-types.service';
import { PlMessagingTypesUIService } from '../../services/pl-messaging-types-ui.service';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import type {
	newPlatformTypeDialogReturnData,
	PlatformType,
	enumeration,
} from '@osee/messaging/shared/types';
import { PlatformTypeCardComponent } from '@osee/messaging/shared/main-content';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { NewTypeDialogComponent } from '../../new-type-dialog/new-type-dialog.component';

@Component({
	selector: 'osee-messaging-types-type-grid',
	templateUrl: './type-grid.component.html',
	styles: [],
	standalone: true,
	imports: [
		NgIf,
		AsyncPipe,
		NgFor,
		FormsModule,
		MatButtonModule,
		MatIconModule,
		MatFormFieldModule,
		MatInputModule,
		MatPaginatorModule,
		PlatformTypeCardComponent,
	],
})
export class TypeGridComponent implements OnChanges, OnDestroy {
	private _done = new Subject();
	@Input() filterValue: string = '';
	columnCount = this.uiService.columnCount;
	gutterSize: string = '';
	filteredData = this.typesService.typeData.pipe(takeUntil(this._done));
	filteredDataSize = this.typesService.typeDataCount;
	rowHeight: string = '';
	inEditMode = this.typesService.inEditMode;

	/**
	 * @TODO this needs to be replaced with a SQL query since we can't tell whether a user decreased the size or the results themselves decreased
	 */
	currentPage = this.typesService.currentPage;

	currentOffset = combineLatest([
		this.typesService.currentPage.pipe(startWith(0), pairwise()),
		this.typesService.currentPageSize,
	]).pipe(
		debounceTime(100),
		scan((acc, [[previousPageNum, currentPageNum], currentSize]) => {
			if (previousPageNum < currentPageNum) {
				return (acc += currentSize);
			} else {
				return acc;
			}
		}, 10)
	);

	minPageSize = combineLatest([
		this.currentOffset,
		this.filteredDataSize,
	]).pipe(
		debounceTime(100),
		switchMap(([offset, types]) => of([offset, types])),
		map(([offset, length]) => Math.max(offset + 1, length + 1)),
		takeUntil(this._done)
	);

	constructor(
		private breakpointObserver: BreakpointObserver,
		private typesService: CurrentTypesService,
		private uiService: PlMessagingTypesUIService,
		public dialog: MatDialog
	) {
		this.uiService.filterString = this.filterValue;
		const breakpoint = this.breakpointObserver.observe([
			Breakpoints.XSmall,
			Breakpoints.Small,
			Breakpoints.Medium,
			Breakpoints.Large,
			Breakpoints.XLarge,
			Breakpoints.Web,
		]);
		const combined = combineLatest([
			breakpoint,
			this.uiService.singleLineAdjustment,
		]).subscribe((result) => {
			this.updateColumnsCount(result);
		});
	}
	ngOnDestroy(): void {
		this._done.next('');
	}

	ngOnChanges(changes: SimpleChanges): void {
		this.uiService.filterString = this.filterValue;
	}
	/**
	 * Adjusts the layout of the page based on CDK Layout Observer
	 * @param state Array containing the state of the page (i.e. what breakpoints) and whether or not to adjust the layout due to being on a single line
	 */
	updateColumnsCount(state: [BreakpointState, number]) {
		if (state[0].matches) {
			if (state[0].breakpoints[Breakpoints.XSmall]) {
				this.uiService.columnCountNumber = 1;
				this.gutterSize = '16';
			}
			if (state[0].breakpoints[Breakpoints.Small]) {
				this.uiService.columnCountNumber = 2;
				this.gutterSize = '16';
			}
			if (state[0].breakpoints[Breakpoints.Medium]) {
				this.rowHeight = 45 + state[1] + '%';
				//this.rowHeight="30%"
				this.uiService.columnCountNumber = 3;
				this.gutterSize = '24';
			}
			if (
				state[0].breakpoints[Breakpoints.Large] &&
				!state[0].breakpoints[Breakpoints.Medium]
			) {
				this.rowHeight = 45 + state[1] + '%'; //37
				this.uiService.columnCountNumber = 4;
				this.gutterSize = '24';
			}
			if (
				state[0].breakpoints[Breakpoints.XLarge] &&
				!state[0].breakpoints[Breakpoints.Large]
			) {
				//this.rowHeight = "45%";
				this.rowHeight = 45 + state[1] + '%';
				this.uiService.columnCountNumber = 5;
				this.gutterSize = '24';
			}
			if (state[0].breakpoints[Breakpoints.Web]) {
				this.rowHeight = 45 + state[1] + '%';
				this.uiService.columnCountNumber = 5;
				this.gutterSize = '24';
			}
		}
	}

	/**
	 * Sets the filter value so the API can update the data on the page.
	 * @param event Event containing user input from the filter
	 */
	applyFilter(event: Event) {
		this.filterValue = (event.target as HTMLInputElement).value
			.trim()
			.toLowerCase();
		this.uiService.filterString = this.filterValue;
	}

	openNewTypeDialog() {
		this.dialog
			.open(NewTypeDialogComponent, {
				id: 'new-type-dialog',
				minHeight: '70vh',
				minWidth: '80vw',
			})
			.afterClosed()
			.pipe(
				filter((x) => x !== undefined) as OperatorFunction<
					newPlatformTypeDialogReturnData | undefined,
					newPlatformTypeDialogReturnData
				>,
				switchMap(
					({ platformType, createEnum, enumSet, ...enumData }) =>
						this.mapTo(platformType, createEnum, enumData).pipe()
				)
			)
			.subscribe();
	}
	/**
	 *
	 * @TODO replace enumData with actual enum
	 */
	mapTo(
		results: Partial<PlatformType>,
		newEnum: boolean,
		enumData: {
			enumSetId: string;
			enumSetName: string;
			enumSetDescription: string;
			enumSetApplicability: applic;
			enums: enumeration[];
		}
	) {
		results.name =
			(results?.name?.charAt(0)?.toLowerCase() || '') +
			results?.name?.slice(1);
		return this.typesService.createType(results, newEnum, enumData);
	}

	getWidthString() {
		return (
			100 / this.columnCount.getValue() + '% -' + this.gutterSize + 'px'
		);
	}
	getMarginString() {
		return this.gutterSize + 'px';
	}

	setPage(event: PageEvent) {
		this.typesService.pageSize = event.pageSize;
		this.typesService.page = event.pageIndex;
	}
}
