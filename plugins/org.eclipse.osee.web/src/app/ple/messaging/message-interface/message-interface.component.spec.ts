import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { BehaviorSubject, of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { AddMessageDialogComponent } from './components/message-table/add-message-dialog/add-message-dialog.component';
import { EditMessageFieldComponent } from './components/message-table/edit-message-field/edit-message-field.component';
import { MessageTableComponent } from './components/message-table/message-table.component';
import { AddSubMessageDialogComponent } from './components/sub-message-table/add-sub-message-dialog/add-sub-message-dialog.component';
import { EditSubMessageFieldComponent } from './components/sub-message-table/edit-sub-message-field/edit-sub-message-field.component';
import { SubMessageTableComponent } from './components/sub-message-table/sub-message-table.component';

import { MessageInterfaceComponent } from './message-interface.component';
import { ConvertMessageTableTitlesToStringPipe } from './pipes/convert-message-table-titles-to-string.pipe';
import { ConvertSubMessageTitlesToStringPipe } from './pipes/convert-sub-message-titles-to-string.pipe';
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
    interfaceMessageWriteAccess: 'true',
    interfaceMessageType: 'Connection',
    subMessages: [{
      id: '0',
      name: 'sub message name',
      description: '',
      interfaceMessageRate: '50Hz',
      interfaceSubMessageNumber:'0'
    }]
  }];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatTableModule,
        MatFormFieldModule,
        MatMenuModule,
        FormsModule,
        MatInputModule,
        NoopAnimationsModule,
        RouterTestingModule,
        MatTooltipModule,
        OseeStringUtilsDirectivesModule,
        OseeStringUtilsPipesModule,
        MatDialogModule],
      declarations: [MessageInterfaceComponent, MessageTableComponent, SubMessageTableComponent, ConvertMessageTableTitlesToStringPipe, ConvertSubMessageTitlesToStringPipe, EditSubMessageFieldComponent, AddSubMessageDialogComponent, EditMessageFieldComponent, AddMessageDialogComponent],
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
