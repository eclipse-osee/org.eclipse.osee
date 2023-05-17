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
import { AsyncPipe, NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
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
} from 'rxjs';
import { paginationMode } from '../internal/pagination-options';

/**
 * Component utilized strictly for stubbing out functionality in tests
 */
@Component({
	selector: 'osee-mat-option-loading',
	templateUrl: './mat-option-loading.component.mock.html',
	standalone: true,
	imports: [AsyncPipe, NgTemplateOutlet, NgFor, NgIf],
})
export class MockMatOptionLoadingComponent<T = unknown> implements OnInit {
	@Input() count: Observable<number> | undefined = undefined;
	@Input() data!:
		| ((pageNum: number | string) => Observable<T[]>)
		| Observable<T[]>;
	@Input() disableSelect = false;
	@Input() objectName = 'options';
	@Input() paginationMode: paginationMode = 'OFF';
	@Input() paginationSize: number = 5;
	@Input() rateLimit: number = 500;

	_options!: Observable<T[]>;
	@ContentChild(TemplateRef) template!: TemplateRef<T>;

	ngOnInit(): void {
		this._options = of('').pipe(
			switchMap((pageNum) => {
				if (this._isNotObservable(this.data)) {
					return this.data.call(this, pageNum);
				}
				return this.data;
			}),
			concatMap((elements) => from(elements)), //don't know why typescript isn't resolving the type normally like the regular component
			scan((acc, curr) => [...acc, curr], [] as T[])
		);
	}
	private _isNotObservable(
		value: ((pageNum: number | string) => Observable<T[]>) | Observable<T[]>
	): value is (pageNum: number | string) => Observable<T[]> {
		return !('subscribe' in value);
	}
}
