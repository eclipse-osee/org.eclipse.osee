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
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';
import {
	Component,
	ContentChild,
	ContentChildren,
	Input,
	OnInit,
	QueryList,
	TemplateRef,
	ViewChild,
	ViewChildren,
} from '@angular/core';
import { MatOption } from '@angular/material/core';
import {
	Observable,
	BehaviorSubject,
	Subject,
	of,
	concatMap,
	from,
	scan,
	switchMap,
	tap,
} from 'rxjs';
import { paginationMode } from '../internal/pagination-options';

/**
 * Component utilized strictly for stubbing out functionality in tests
 */
@Component({
	selector: 'osee-mat-option-loading',
	templateUrl: './mat-option-loading.component.html',
	styleUrls: ['./mat-option-loading.component.sass'],
})
export class MatOptionLoadingComponent<T = unknown> implements OnInit {
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

	ngOnInit(): void {
		this._options = of('').pipe(
			switchMap((pageNum) => {
				if (this._isNotObservable(this.data)) {
					if (this._isNotPaginatedFunctionObservable(this.data)) {
						return this.data.call(this);
					}
					return this.data.call(this, pageNum);
				}
				return this.data;
			}),
			concatMap((elements) => from(elements)), //don't know why typescript isn't resolving the type normally like the regular component
			scan((acc, curr) => [...acc, curr], [] as T[])
		);
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
}
