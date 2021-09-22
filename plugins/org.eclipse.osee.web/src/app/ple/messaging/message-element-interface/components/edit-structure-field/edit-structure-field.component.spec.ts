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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { apiURL } from 'src/environments/environment';
import { enumsServiceMock } from '../../../shared/mocks/EnumsService.mock';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { SharedMessagingModule } from '../../../shared/shared-messaging.module';
import { CurrentStateServiceMock } from '../../mocks/services/CurrentStateService.mock';
import { CurrentStateService } from '../../services/current-state.service';
import { UiService } from '../../services/ui.service';

import { EditStructureFieldComponent } from './edit-structure-field.component';

describe('EditStructureFieldComponent', () => {
  let component: EditStructureFieldComponent;
  let fixture: ComponentFixture<EditStructureFieldComponent>;
  let uiService: UiService;
  let loader:HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, NoopAnimationsModule, FormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, SharedMessagingModule],
      providers: [
        { provide: CurrentStateService, useValue: CurrentStateServiceMock },
        { provide:EnumsService,useValue:enumsServiceMock}
      ],
      declarations: [ EditStructureFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    uiService = TestBed.inject(UiService);
    fixture = TestBed.createComponent(EditStructureFieldComponent);
    component = fixture.componentInstance;
    component.header = 'applicability';
    component.value = { id: '1', name: 'Base' };
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update value', fakeAsync(async() => {
    uiService.BranchIdString = '8';
    uiService.messageIdString = '10';
    uiService.subMessageIdString = '20';
    uiService.connectionIdString = '10';
    let spy = spyOn(component, 'updateStructure').and.callThrough();
      let select = await loader.getHarness(MatSelectHarness);
      await select.open();
      if (await select.isOpen()) {
        await select.clickOptions({ text: 'Second' });
        expect(spy).toHaveBeenCalled()
      } else {
        expect(spy).not.toHaveBeenCalled()
      }
  }));
});
