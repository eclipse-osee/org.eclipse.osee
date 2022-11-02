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
	Component,
	EventEmitter,
	Input,
	Output,
	SimpleChanges,
} from '@angular/core';
import { BehaviorSubject, Observable, of, ReplaySubject } from 'rxjs';
import {
	logicalTypeFormDetail,
	logicalTypeFieldInfo,
} from '../../../types/logicaltype';
import { logicalTypefieldValue } from '../../../types/newTypeDialogDialogData';
import { NewTypeFormComponent } from './new-type-form.component';

@Component({
	selector: 'osee-new-type-form',
	template: '<p>Dummy</p>',
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class MockNewTypeForm implements Partial<NewTypeFormComponent> {
	@Input() logicalType: string = '1';
	@Output() attributesUnique: Observable<string> = of();
	@Output() stepComplete: Observable<boolean> = of();
	@Output('attributes') private _nameToValueMap = new BehaviorSubject<
		Map<string, string>
	>(new Map());
	@Output('fields') private _attrnameToLogicalTypeFields =
		new BehaviorSubject<Map<string, logicalTypefieldValue>>(new Map());
}
