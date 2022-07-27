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
import { Component, Input } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { applic } from '../../../../../../types/applicability/applic';
import { enumerationSet } from '../../../types/enum';
import { EnumSetFormComponent } from './enum-set-form.component';

@Component({
    selector: 'osee-enum-set-form',
    template: '<p>Dummy</p>',
})
export class MockEnumSetFormUnique implements Partial<EnumSetFormComponent> {
    @Input() bitSize: string = '32';
    applics: Observable<applic[]> = of([]);
    enumSet: enumerationSet = {
        name: '',
        description: '',
        applicability: {
            id: '1',
            name: 'Base',
        },
    };
    _unique = new Subject<boolean>();
    unique: Observable<string> = of('');
    _closeForm: Subject<unknown> = new Subject();
}
