/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { ActionDropdownStub } from '../../../shared-components/components/action-state-button/action-drop-down/action-drop-down.mock.component';
import { BranchPickerStub } from '../../../shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';
import { BranchUndoButtonTestingModule } from '../../../shared-components/components/branch-undo-button/branch.undo-button.testing.module';
import { PlatformTypeCardComponent } from '../shared/components/platform-type-card/platform-type-card.component';
import { MockPlatformTypeCard } from '../shared/mocks/platform-type-card.component.mock';
import { TypeGridComponent } from './components/type-grid/type-grid.component';
import { CurrentTypesService } from './services/current-types.service';

import { TypesInterfaceComponent } from './types-interface.component';

describe('TypesInterfaceComponent', () => {
  let component: TypesInterfaceComponent;
  let fixture: ComponentFixture<TypesInterfaceComponent>;
  let typesService: Partial<CurrentTypesService> = {
    typeData:of(
      [
        {
          interfaceLogicalType: "boolean",
          description:'',
          interfacePlatform2sComplement: false,
          interfacePlatformTypeAnalogAccuracy: "Hello",
          interfacePlatformTypeBitsResolution: "1",
          interfacePlatformTypeBitSize: "8",
          interfacePlatformTypeCompRate: "1",
          interfacePlatformTypeDefaultValue: "1",
          interfacePlatformTypeEnumLiteral: "Enum Lit.",
          interfacePlatformTypeMaxval: "1",
          interfacePlatformTypeMinval: "0",
          interfacePlatformTypeMsbValue: "1",
          interfacePlatformTypeUnits: "N/A",
          interfacePlatformTypeValidRangeDescription: "Description",
          name:"boolean"
        },
        {
          interfaceLogicalType: "integer",
          description:'',
          interfacePlatform2sComplement: false,
          interfacePlatformTypeAnalogAccuracy: "Hello",
          interfacePlatformTypeBitsResolution: "1",
          interfacePlatformTypeBitSize: "8",
          interfacePlatformTypeCompRate: "1",
          interfacePlatformTypeDefaultValue: "1",
          interfacePlatformTypeEnumLiteral: "Enum Lit.",
          interfacePlatformTypeMaxval: "1",
          interfacePlatformTypeMinval: "0",
          interfacePlatformTypeMsbValue: "1",
          interfacePlatformTypeUnits: "N/A",
          interfacePlatformTypeValidRangeDescription: "Description",
          name:"integer"
        }
      ]
    )
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatCardModule,MatFormFieldModule,NoopAnimationsModule,MatInputModule,MatGridListModule, MatDialogModule, FormsModule, BranchUndoButtonTestingModule, RouterTestingModule],
      declarations: [TypesInterfaceComponent, TypeGridComponent, MockPlatformTypeCard, BranchPickerStub,ActionDropdownStub],
      providers:[{provide: CurrentTypesService, useValue:typesService}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TypesInterfaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
