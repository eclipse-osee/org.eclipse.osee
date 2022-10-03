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
import { Component, Input, OnInit, Output } from '@angular/core';
import { iif, Subject, of, BehaviorSubject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { applic } from '../../../../../../types/applicability/applic';
import { ApplicabilityListUIService } from '../../../services/ui/applicability-list-ui.service';
import { enumeration, enumerationSet } from '../../../types/enum';

@Component({
    selector: 'osee-enum-set-form',
    templateUrl: './enum-set-form.component.html',
    styleUrls: ['./enum-set-form.component.sass'],
})
export class EnumSetFormComponent implements OnInit {
    @Input() bitSize: string = '0';
    applics = this.applicabilityService.applic;
    enumSet: enumerationSet = {
        name: '',
        description: '',
        applicability: {
            id: '1',
            name: 'Base',
        },
    };
    @Output('unique') _unique = new Subject<boolean>();
    unique = this._unique.pipe(
        switchMap((unique) =>
            iif(() => unique, of(''), of('Enumeration Set Not Unique.'))
        )
    );

    @Output('enumSet') private _enumSet = new BehaviorSubject<enumerationSet>({
        name: '',
        description: '',
        applicability: {
            id: '1',
            name: 'Base',
        },
    });
    @Output('close') _closeForm = new Subject();
    constructor(private applicabilityService: ApplicabilityListUIService) {}

    ngOnInit(): void {}
    compareApplics(o1: applic, o2: applic) {
        return o1?.id === o2?.id && o1?.name === o2?.name;
    }
    updateDescription(value: string) {
        this.enumSet.description = value;
    }

    updateUnique(value: boolean) {
        this._unique.next(value);
    }

    updateEnums(value: enumeration[]) {
        let enumSet = this._enumSet.getValue();
        enumSet.enumerations = value;
        this._enumSet.next(enumSet);
    }
    updateEnumSet() {
        this._enumSet.next(this.enumSet);
    }
    closeForm() {
        this._closeForm.next(true);
    }
}
