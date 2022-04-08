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
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Router, ActivatedRoute, convertToParamMap, NavigationEnd } from '@angular/router';
import { of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';

import { MessageElementInterfaceComponent } from './message-element-interface.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CurrentStructureService } from './services/current-structure.service';
import { SharedMessagingModule } from '../shared/shared-messaging.module';
import { EditElementFieldComponent } from './components/sub-element-table/edit-element-field/edit-element-field.component';
import { EditStructureFieldComponentMock } from './mocks/components/EditStructureField.mock';
import { CurrentStateServiceMock } from './mocks/services/CurrentStateService.mock';
import { SubElementTableComponentMock } from './mocks/components/sub-element-table.mock';
import { EditAuthService } from '../shared/services/edit-auth-service.service';
import { editAuthServiceMock } from '../connection-view/mocks/EditAuthService.mock';
import { MatMenuModule } from '@angular/material/menu';
import { StructureTableComponentMock } from './mocks/components/StructureTable.mock';
import { changeReportMock } from 'src/app/ple-services/http/change-report.mock';
import { RouterTestingModule } from '@angular/router/testing';
import { MatIconModule } from '@angular/material/icon';

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
        MatIconModule,
        MatSelectModule,
        MatMenuModule,
        FormsModule,
        NoopAnimationsModule,
        MatTableModule,
        MatTooltipModule,
        OseeStringUtilsPipesModule,
        OseeStringUtilsDirectivesModule,
        RouterTestingModule,
        SharedMessagingModule
      ],
      declarations: [
        MessageElementInterfaceComponent,
        SubElementTableComponentMock,
        EditElementFieldComponent,
        EditStructureFieldComponentMock,
        StructureTableComponentMock
      ],
      providers: [
        {
          provide: Router, useValue: {
            navigate: () => { }, url: '', events: of<NavigationEnd>({
              id: 1,
              url: '',
              urlAfterRedirects:''
        }) } },
        { provide: EditAuthService,useValue:editAuthServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            data:of({diff:changeReportMock}),
            paramMap: of(
              convertToParamMap({
                name: 'Name > Name',
              })
            ),
          },
        },
        {
          provide: CurrentStructureService, useValue: CurrentStateServiceMock
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
