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
import { Component, Input, Output } from '@angular/core';
import { enumerationSetMock } from '@osee/messaging/shared/testing';
import type { enumeration } from '@osee/messaging/shared/types';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { EnumFormComponent } from './enum-form.component';

@Component({
	selector: 'osee-enum-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockEnumFormUniqueComponent implements Partial<EnumFormComponent> {
	private _unique = new Subject<boolean>();
	@Input() bitSize: string = '32';
	@Input() enumSetName: string = 'testenumset';
	@Input() enumSetId?: string = '';
	@Input() preload: enumeration[] = [];
	@Output() tableData: BehaviorSubject<enumeration[]> = new BehaviorSubject(
		enumerationSetMock[0].enumerations || []
	);
	@Output() enumSetString: Observable<string> = new Subject();
	@Output() unique: Observable<boolean> = this._unique;
	constructor() {
		this._unique.next(true);
	}
}
