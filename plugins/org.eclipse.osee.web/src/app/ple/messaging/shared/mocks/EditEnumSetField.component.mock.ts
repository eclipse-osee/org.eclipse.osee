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
 import { Component, EventEmitter, Input, Output } from "@angular/core";
import { Observable } from 'rxjs';
import { applic } from '../../../../types/applicability/applic';
import { EditEnumSetFieldComponent } from '../components/edit-enum-set-field/edit-enum-set-field.component';
import { PlatformTypeCardComponent } from '../components/platform-type-card/platform-type-card.component';
import { enumerationSet } from '../types/enum';
import { PlatformType } from '../types/platformType';

 
  @Component({
      selector: 'osee-edit-enum-set-field',
      template:'<p>Dummy</p>'
  })
export class EditEnumSetFieldMock implements Partial<EditEnumSetFieldComponent>{
      @Input() editable: boolean = false;
      @Input() platformTypeId: string | undefined = '';
      @Input() platformType: PlatformType | undefined =undefined;
      @Output() enumUpdated: EventEmitter<enumerationSet | undefined> = new EventEmitter();
}