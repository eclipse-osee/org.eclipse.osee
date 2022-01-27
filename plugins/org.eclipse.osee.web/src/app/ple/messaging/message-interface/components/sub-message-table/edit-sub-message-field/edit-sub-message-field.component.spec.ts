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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { CurrentMessageServiceMock } from '../../../mocks/services/CurrentMessageService.mock';
import { ConvertSubMessageTitlesToStringPipe } from '../../../pipes/convert-sub-message-titles-to-string.pipe';
import { CurrentMessagesService } from '../../../services/current-messages.service';
import { EditSubMessageFieldComponent } from './edit-sub-message-field.component';


describe('EditSubMessageFieldComponent', () => {
  let component: EditSubMessageFieldComponent;
  let fixture: ComponentFixture<EditSubMessageFieldComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, NoopAnimationsModule],
      providers:[{provide:CurrentMessagesService, useValue:CurrentMessageServiceMock}],
      declarations: [ EditSubMessageFieldComponent,ConvertSubMessageTitlesToStringPipe ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditSubMessageFieldComponent);
    component = fixture.componentInstance;
    component.header='applicability'
    component.value={id:'1',name:'Base'}
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update the applicabilities', async () => {
    let spy = spyOn(component, 'updateSubMessage').and.callThrough();
    let select = await loader.getHarness(MatSelectHarness);
    component.focusChanged(null);
    await select.open();
    if (await select.isOpen()) {
      await select.clickOptions({ text: 'Second' });
      expect(spy).toHaveBeenCalled()
    } else {
      expect(spy).not.toHaveBeenCalled()
    }
  })
});
