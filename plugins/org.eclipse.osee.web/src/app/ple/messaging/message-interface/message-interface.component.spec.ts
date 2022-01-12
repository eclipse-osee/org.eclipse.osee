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
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';

import { MessageInterfaceComponent } from './message-interface.component';
import { MessageTableComponentMock } from './mocks/components/MessageTableComponent.mock';
import { CurrentMessagesService } from './services/current-messages.service';
import { message } from './types/messages';

describe('MessageInterfaceComponent', () => {
  let component: MessageInterfaceComponent;
  let fixture: ComponentFixture<MessageInterfaceComponent>;
  let expectedData: message[] = [{
    id:'-1',
    name: 'name',
    description: 'description',
    interfaceMessageRate: '50Hz',
    interfaceMessageNumber: '0',
    interfaceMessagePeriodicity: '1Hz',
    interfaceMessageWriteAccess: true,
    interfaceMessageType: 'Connection',
    subMessages: [{
      id: '0',
      name: 'sub message name',
      description: '',
      interfaceSubMessageNumber: '0',
      applicability: {
        id: '1',
        name:'Base'
      }
    }]
  }];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatTableModule,
        MatFormFieldModule,
        MatMenuModule,
        MatIconModule,
        FormsModule,
        MatInputModule,
        NoopAnimationsModule,
        RouterTestingModule,
        MatTooltipModule,
        OseeStringUtilsDirectivesModule,
        OseeStringUtilsPipesModule,
        MatDialogModule,],
      declarations: [MessageInterfaceComponent, MessageTableComponentMock],
      providers: [{
        provide: CurrentMessagesService, useValue: {
          filter: '',
          string: '',
          messages: of(expectedData),
          BranchId:new BehaviorSubject("10")
      }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageInterfaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
