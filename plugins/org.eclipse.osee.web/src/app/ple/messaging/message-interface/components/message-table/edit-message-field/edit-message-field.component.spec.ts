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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { applicabilityListServiceMock } from 'src/app/ple/messaging/shared/mocks/ApplicabilityListService.mock';
import { ApplicabilityListService } from 'src/app/ple/messaging/shared/services/http/applicability-list.service';
import { apiURL } from 'src/environments/environment';
import { ConvertMessageTableTitlesToStringPipe } from '../../../pipes/convert-message-table-titles-to-string.pipe';
import { UiService } from '../../../services/ui.service';

import { EditMessageFieldComponent } from './edit-message-field.component';

describe('EditMessageFieldComponent', () => {
  let component: EditMessageFieldComponent;
  let fixture: ComponentFixture<EditMessageFieldComponent>;
  let httpTestingController: HttpTestingController;
  let uiService: UiService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, FormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatDialogModule, NoopAnimationsModule],
      providers:[{provide:ApplicabilityListService,useValue:applicabilityListServiceMock}],
      declarations: [ EditMessageFieldComponent, ConvertMessageTableTitlesToStringPipe ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    httpTestingController = TestBed.inject(HttpTestingController);
    uiService = TestBed.inject(UiService);
    fixture = TestBed.createComponent(EditMessageFieldComponent);
    component = fixture.componentInstance;
    component.header='applicability'
    component.value={id:'1',name:'Base'}
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update value', fakeAsync(() => {
    uiService.BranchIdString = '8'
    uiService.connectionIdString='10'
    component.updateMessage('description', 'v2');
    tick(500);
    const req = httpTestingController.expectOne(apiURL + "/orcs/txs");
    expect(req.request.method).toEqual('POST');
  }));
});
