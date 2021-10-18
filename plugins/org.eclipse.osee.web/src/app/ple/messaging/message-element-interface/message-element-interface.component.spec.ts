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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';

import { MessageElementInterfaceComponent } from './message-element-interface.component';
import { ConvertMessageInterfaceTitlesToStringPipe } from '../shared/pipes/convert-message-interface-titles-to-string.pipe';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CurrentStateService } from './services/current-state.service';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { EditElementFieldComponent } from './components/sub-element-table/edit-element-field/edit-element-field.component';
import { EditStructureFieldComponentMock } from './mocks/components/EditStructureField.mock';
import { CurrentStateServiceMock } from './mocks/services/CurrentStateService.mock';
import { MatButtonHarness } from '@angular/material/button/testing';
import { SubElementTableComponentMock } from './mocks/components/sub-element-table.mock';
import { EditAuthService } from '../shared/services/edit-auth-service.service';
import { editAuthServiceMock } from '../connection-view/mocks/EditAuthService.mock';
import { MatMenuModule } from '@angular/material/menu';
import { structuresMock } from './mocks/ReturnObjects/Structures.mock';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { StructureTableComponentMock } from './mocks/components/StructureTable.mock';

let loader: HarnessLoader;

describe('MessageElementInterfaceComponent', () => {
  let component: MessageElementInterfaceComponent;
  let fixture: ComponentFixture<MessageElementInterfaceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatFormFieldModule,
        MatDialogModule,
        MatInputModule,
        MatSelectModule,
        MatMenuModule,
        MatProgressBarModule,
        FormsModule,
        NoopAnimationsModule,
        MatTableModule,
        MatTooltipModule,
        OseeStringUtilsPipesModule,
        OseeStringUtilsDirectivesModule,
        SharedMessagingModule
      ],
      declarations: [
        MessageElementInterfaceComponent,
        SubElementTableComponentMock,
        ConvertMessageInterfaceTitlesToStringPipe,
        EditElementFieldComponent,
        EditStructureFieldComponentMock,
        StructureTableComponentMock
      ],
      providers: [
        { provide: Router, useValue: { navigate: () => { } } },
        { provide: EditAuthService,useValue:editAuthServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(
              convertToParamMap({
                name: 'Name > Name',
              })
            ),
          },
        },
        {
          provide: CurrentStateService, useValue: CurrentStateServiceMock
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageElementInterfaceComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
