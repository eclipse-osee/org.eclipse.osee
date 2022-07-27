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
import { Observable, Subject } from 'rxjs';
import { enumeration } from '../../../types/enum';
import { EnumFormComponent } from './enum-form.component';

@Component({
    selector: 'osee-enum-form',
    template: '<p>Dummy</p>',
})
export class MockEnumFormUnique implements Partial<EnumFormComponent> {
    private _unique = new Subject<boolean>();
    @Input() bitSize: string = '32';
    @Input() enumSetName: string = 'testenumset';
    @Output() tableData: Subject<enumeration[]> = new Subject();
    @Output() enumSetString: Observable<string> = new Subject();
    @Output() unique: Observable<boolean> = this._unique;
    constructor() {
        this._unique.next(true);
    }
}
