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
	CdkVirtualScrollViewport,
	ScrollingModule,
} from '@angular/cdk/scrolling';
import { AsyncPipe, NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
import {
	ChangeDetectionStrategy,
	OnChanges,
	OnDestroy,
	SimpleChanges,
	ViewChild,
} from '@angular/core';
import {
	AfterViewInit,
	Component,
	ContentChild,
	ContentChildren,
	Host,
	Input,
	OnInit,
	Optional,
	QueryList,
	TemplateRef,
	ViewChildren,
} from '@angular/core';
import { MatAutocomplete } from '@angular/material/autocomplete';
import { MatOption, MatOptionModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
import {
	auditTime,
	BehaviorSubject,
	catchError,
	combineLatest,
	concatMap,
	distinct,
	filter,
	from,
	ignoreElements,
	map,
	Observable,
	of,
	pipe,
	scan,
	Subject,
	Subscription,
	switchMap,
	takeUntil,
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
	styleUrls: ['./mat-option-loading.component.sass'],
	standalone: true,
	imports: [
		MatOptionModule,
		NgIf,
		NgFor,
		AsyncPipe,
		ScrollingModule,
		MatProgressSpinnerModule,
		NgTemplateOutlet,
	],
	changeDetection: ChangeDetectionStrategy.OnPush, //lessen the amount of redrawing necessary to cause less "bounciness"
})
export class MatOptionLoadingComponent<T>
	implements OnInit, AfterViewInit, OnDestroy, OnChanges
{
	/**
	 * Input data source that is used to display available options, and also the desired observable to paginate
	 *
	 * Note: only use the Observable<T[]> signature when in a non-paginated case.
	 */
	@Input() data!:
		| (() => Observable<T[]>)
		| ((pageNum: number | string) => Observable<T[]>)
		| Observable<T[]>;
	_options!: Observable<T[]>;
	error!: Observable<string>;
	@ViewChildren(MatOption) protected options!: QueryList<MatOption>;
	/**
	 * Whether or not to disable the select while loading options
	 */
	@Input() disableSelect = false;

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
	 * Highest value that should be put in here is 5. Anything more than this and you'll get two scrollbars.
	 */
	@Input() paginationSize: number = 5;

	/**
	 * Rate Limit Api requests to once per x ms. Auto mode only.
	 */
	@Input() rateLimit: number = 500;

	private _paginationSubject = new BehaviorSubject<number | string>(1);

	@ContentChild(TemplateRef) template!: TemplateRef<T>;
	@ContentChildren(MatOption)
	protected resolvedOptions!: QueryList<MatOption>;
	@ViewChild(CdkVirtualScrollViewport, { static: false })
	cdkVirtualScrollViewPort!: CdkVirtualScrollViewport;
	_paginationComplete = new BehaviorSubject<boolean>(false);
	/**
	 * Name of the objects being returned, eg. Platform Types, Messages etc.
	 */
	@Input() objectName = 'options';
	private _done = new Subject<void>();

	private _autoPaginate = timer(0, 250).pipe(
		filter((v) => this.cdkVirtualScrollViewPort !== undefined),
		filter((x) => this.paginationMode === 'AUTO'),
		filter(
			(event) =>
				this.cdkVirtualScrollViewPort.getRenderedRange().end ===
				this.cdkVirtualScrollViewPort.getDataLength()
		),
		switchMap((x) => this._paginationComplete),
		filter((complete) => complete === false),
		auditTime(this.rateLimit),
		tap((pos) => this.createPaginationEvent()),
		tap(() => this.cdkVirtualScrollViewPort.checkViewportSize()),
		takeUntil(this._done)
	);
	private _autoPaginateSubscription: Subscription | undefined = undefined;
	constructor(
		@Host()
		@Optional()
		private _parentSelect: MatSelect,
		@Host()
		@Optional()
		private _parentAutoComplete: MatAutocomplete // Angular apparently doesn't like DI'ing multiple types
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
	}
	ngOnDestroy(): void {
		this._done.next();
		this._done.complete();
	}
	/**
	 * @TODO shareReplay observables need to be handled, or mat-select with delayed values needs to be handled
	 */
	ngOnInit(): void {
		this._options = this._paginationSubject.pipe(
			switchMap((pageNum) => {
				if (this._isNotObservable(this.data)) {
					if (this._isNotPaginatedFunctionObservable(this.data)) {
						return this.data.call(this).pipe(
							tap(() => {
								this._paginationComplete.next(true);
							})
						);
					}
					return this.data.call(this, pageNum).pipe(
						tap((results) => {
							if (results.length < this.paginationSize) {
								this._paginationComplete.next(true);
							}
						})
					);
				}
				return this.data.pipe(
					tap(() => {
						this._paginationComplete.next(true);
					})
				);
			}),
			this._isNotObservable(this.data) &&
				!this._isNotPaginatedFunctionObservable(this.data)
				? pipe(
						concatMap((elements) => from(elements)),
						distinct(),
						scan((acc, curr) => [...acc, curr], [] as T[]),
						map((valueArray) =>
							valueArray.filter(
								(value, index, array) =>
									array.findIndex(
										(value2) =>
											JSON.stringify(value) ===
											JSON.stringify(value2)
									) === index
							)
						)
				  )
				: pipe()
		);
		this.error = this._options.pipe(
			ignoreElements(),
			catchError((err) =>
				err ? of('Error when fetching data from OSEE server') : of()
			)
		);
	}
	ngAfterViewInit(): void {
		if (this._parentSelect !== undefined && this._parentSelect !== null) {
			/**leaving disabled for now, will need to figure out test fix. 
			/However, this needs to remain on for autocomplete since the most common pattern for autocomplete
			involves something like (pageNum)=>this.opened.pipe(switchMap(v=>this.filter.pipe(PAGINATED_OBSERVABLE_HERE)))
			*/
			//this.registerSelectOptions(this._parentSelect, this.options);
			combineLatest([
				this.resolvedOptions.changes,
				this.options.changes,
			]).subscribe(([changes]) => {
				this.registerSelectOptions(this._parentSelect, this.options);
				if (this.cdkVirtualScrollViewPort !== undefined) {
					this.cdkVirtualScrollViewPort.scrollToIndex(0);
					this.cdkVirtualScrollViewPort.checkViewportSize();
				}
			});
		}
		if (
			this._parentAutoComplete !== undefined &&
			this._parentAutoComplete !== null
		) {
			this.registerSelectOptions(this._parentAutoComplete, this.options);
			combineLatest([
				this.resolvedOptions.changes,
				this.options.changes,
			]).subscribe(([changes]) => {
				this.registerSelectOptions(
					this._parentAutoComplete,
					this.options
				);
				if (this.cdkVirtualScrollViewPort !== undefined) {
					this.cdkVirtualScrollViewPort.scrollToIndex(0);
					this.cdkVirtualScrollViewPort.checkViewportSize();
				}
			});
		}
	}

	protected registerSelectOptions(
		select: MatSelect | MatAutocomplete,
		options: QueryList<MatOption>
	): void {
		const _options = [
			...new Map(
				[...select.options.toArray(), ...options.toArray()].map((m) => [
					m.id,
					m,
				])
			).values(),
		];
		const __options = _options.filter(
			(v, i) => _options.map((a) => a.value).indexOf(v.value) === i
		);
		select.options.reset([...__options]);
		select.options.notifyOnChanges();
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
		value:
			| (() => Observable<T[]>)
			| ((pageNum: number | string) => Observable<T[]>)
			| Observable<T[]>
	): value is
		| (() => Observable<T[]>)
		| ((pageNum: number | string) => Observable<T[]>) {
		return !('subscribe' in value);
	}

	private _isNotPaginatedFunctionObservable(
		value:
			| (() => Observable<T[]>)
			| ((pageNum: number | string) => Observable<T[]>)
	): value is () => Observable<T[]> {
		return value.length === 0;
	}

	createPaginationEvent() {
		if (this.paginationMode !== 'OFF') {
			this._paginationSubject.next(
				Number(this._paginationSubject.getValue()) + 1
			);
		}
	}
}
