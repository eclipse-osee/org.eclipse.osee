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
import {
	CdkFixedSizeVirtualScroll,
	CdkVirtualForOf,
	CdkVirtualScrollViewport,
} from '@angular/cdk/scrolling';
import { AsyncPipe, NgTemplateOutlet } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	Host,
	Input,
	OnChanges,
	Optional,
	SimpleChanges,
	TemplateRef,
	contentChild,
	viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatAutocomplete } from '@angular/material/autocomplete';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatListItemTitle } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
import {
	BehaviorSubject,
	Observable,
	ReplaySubject,
	Subscription,
	auditTime,
	catchError,
	combineLatest,
	concatMap,
	filter,
	ignoreElements,
	of,
	scan,
	shareReplay,
	switchMap,
	tap,
	timer,
} from 'rxjs';
import { paginationMode } from '../internal/pagination-options';
/**
 * Component that handles loading, pagination and error states for mat-options
 */
@Component({
	selector: 'osee-mat-option-loading',
	templateUrl: './mat-option-loading.component.html',
	standalone: true,
	imports: [
		AsyncPipe,
		MatOption,
		CdkVirtualScrollViewport,
		CdkFixedSizeVirtualScroll,
		CdkVirtualForOf,
		MatProgressSpinner,
		MatButton,
		MatListItemTitle,
		NgTemplateOutlet,
	],
	changeDetection: ChangeDetectionStrategy.OnPush, //lessen the amount of redrawing necessary to cause less "bounciness"
})
export class MatOptionLoadingComponent<T> implements OnChanges {
	/**
	 * Total number of items that should come from the paginated data source
	 */
	@Input() count: number = -1;

	/**
	 * Input data source that is used to display available options, and also the desired observable to paginate
	 *
	 * Note: only use the Observable<T[]> signature when in a non-paginated case.
	 */
	@Input() data!:
		| ((pageNum: number | string) => Observable<T[]>)
		| Observable<T[]>;

	/**
	 * Whether or not to disable the select while loading options
	 */
	@Input() disableSelect = false;

	/**
	 * Name of the objects being returned, eg. Platform Types, Messages etc.
	 */
	@Input() objectName = 'options';

	/**
	 * Pagination mode:
	 *
	 * OFF: No pagination occurs.
	 *
	 * Manual: Shows a "Show More" button for user to interact with
	 *
	 * Auto: Loads more data as user scrolls(250ms poll rate).
	 */
	@Input() paginationMode: paginationMode = 'OFF';

	/**
	 * Number of rows to expect per each query.
	 */
	@Input() paginationSize: number = 5;

	/**
	 * Rate Limit Api requests to once per x ms. Auto mode only.
	 */
	@Input() rateLimit: number = 500;

	@Input() noneOption: T | undefined = undefined;

	private _currentPageNumber = new BehaviorSubject<number | string>(1);
	template = contentChild.required(TemplateRef, {
		read: TemplateRef<{ $implicit: T; opt: T }>,
	});
	_paginationComplete = new BehaviorSubject<boolean>(false);

	cdkVirtualScrollViewPort = viewChild(CdkVirtualScrollViewport);

	private _autoPaginate = timer(0, 250).pipe(
		filter(
			(_) =>
				this.paginationMode === 'AUTO' &&
				this.cdkVirtualScrollViewPort() !== undefined &&
				this.cdkVirtualScrollViewPort()!.getViewportSize() !== 0 &&
				this.cdkVirtualScrollViewPort()!.getRenderedRange().end ===
					this.cdkVirtualScrollViewPort()!.getDataLength()
		),
		switchMap((_) => this._paginationComplete),
		filter((complete) => complete === false),
		auditTime(this.rateLimit),
		tap((_) => this.createPaginationEvent()),
		tap((_) => this.cdkVirtualScrollViewPort()!.checkViewportSize()),
		takeUntilDestroyed()
	);
	private _autoPaginateSubscription: Subscription | undefined = undefined;
	private _dataSubject = new ReplaySubject<
		((pageNum: number | string) => Observable<T[]>) | Observable<T[]>
	>();
	private _count: BehaviorSubject<number> = new BehaviorSubject<number>(-1);
	_options: Observable<T[]> = combineLatest([
		this._dataSubject,
		this._count,
	]).pipe(
		tap((_) => {
			this._currentPageNumber.next(1);
			this._paginationComplete.next(false);
		}),
		switchMap(([query, count]) =>
			combineLatest([
				this._currentPageNumber,
				this._paginationComplete.pipe(filter((c) => c === false)),
			]).pipe(
				// This needs to be a concatmap in order to complete all of the page emissions
				// in order. The catch with concatmap is if the query does not complete, or the
				// observable stays hot, the it will not move on to the next page.
				// If the options aren't paginating, make sure there's a take(1) on the source
				// observable and that it's completing.
				concatMap(([pageNum, complete]) => {
					if (this._isNotObservable(query)) {
						return query.call(this, pageNum).pipe(
							tap((results) => {
								if (results.length < this.paginationSize) {
									this._paginationComplete.next(true);
								}
							})
						);
					}
					this._paginationComplete.next(true);
					return query;
				}),
				scan(
					(acc, curr) => {
						// For plain non-paginated observables, return the initial query results.
						if (!this._isNotObservable(query)) {
							return curr;
						}

						if (count < 0) {
							if (acc.length === 0) {
								return [...curr];
							} else if (curr.length === 0) {
								this._paginationComplete.next(true);
								return acc;
							}
						} else if (acc.length + curr.length >= count) {
							this._paginationComplete.next(true);
						}
						const deDupedArr = [...acc, ...curr].filter(
							(value, index, array) =>
								array.findIndex(
									(value2) =>
										JSON.stringify(value) ===
										JSON.stringify(value2)
								) === index
						);

						if (deDupedArr.length !== acc.length + curr.length) {
							return deDupedArr;
						}
						return [...acc, ...curr];
					},
					this.noneOption ? ([this.noneOption] as T[]) : ([] as T[])
				)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);
	error: Observable<string> = this._options.pipe(
		ignoreElements(),
		catchError((err) =>
			err ? of('Error when fetching data from OSEE server') : of()
		)
	);
	constructor(
		@Host()
		@Optional()
		private _parentSelect: MatSelect
	) {}

	ngOnChanges(changes: SimpleChanges): void {
		//turn subscription on or off based on auto mode to keep the polling off in manual/no-paginate modes.
		if (
			this.paginationMode === 'AUTO' &&
			this._autoPaginateSubscription === undefined
		) {
			this._autoPaginateSubscription = this._autoPaginate.subscribe();
		} else if (
			this._autoPaginateSubscription !== undefined &&
			this.paginationMode !== 'AUTO'
		) {
			this._autoPaginateSubscription.unsubscribe();
			this._autoPaginateSubscription = undefined;
		}
		if (changes.data) {
			this._dataSubject.next(changes.data.currentValue);
		}
		if (changes.count) {
			this._count.next(changes.count.currentValue);
		}
	}
	/**
	 * @TODO shareReplay observables need to be handled, or mat-select with delayed values needs to be handled
	 */

	getHeightPx(itemSize: number, optLength: number) {
		return itemSize * Math.min(5, Math.max(optLength, 1));
	}

	private _isMatSelect(
		value: MatSelect | MatAutocomplete
	): value is MatSelect {
		return value !== null && value !== undefined && 'disabled' in value;
	}

	setDisabled(value: boolean) {
		if (this._isMatSelect(this._parentSelect)) {
			this._parentSelect.disabled = value;
		}
	}

	private _isNotObservable(
		value: ((pageNum: number | string) => Observable<T[]>) | Observable<T[]>
	): value is (pageNum: number | string) => Observable<T[]> {
		return !('subscribe' in value);
	}

	createPaginationEvent() {
		if (this.paginationMode !== 'OFF') {
			this._currentPageNumber.next(
				Number(this._currentPageNumber.getValue()) + 1
			);
		}
	}
}
