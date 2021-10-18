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
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { MessagingHelpComponent } from './messaging-help.component';
import { ColumnDescriptionsMessageHelpComponentMock } from './mocks/components/column-descriptions-message-help.mock';

describe('MessagingHelpComponent', () => {
  let component: MessagingHelpComponent;
  let fixture: ComponentFixture<MessagingHelpComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[RouterTestingModule.withRoutes([{path:'columnDescriptions',component:ColumnDescriptionsMessageHelpComponentMock}]),MatButtonModule,NoopAnimationsModule],
      declarations: [ MessagingHelpComponent,ColumnDescriptionsMessageHelpComponentMock ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessagingHelpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to the column descriptions page', async() => {
    let spy = spyOn(component, 'navigateTo').and.callThrough();
    const button = await loader.getHarness(MatButtonHarness.with({ text: 'Column Descriptions' }));
    await button.click();
    expect(spy).toHaveBeenCalledWith('columnDescriptions')
  })
});
