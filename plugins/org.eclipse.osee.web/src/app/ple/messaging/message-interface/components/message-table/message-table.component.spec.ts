import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { ConvertMessageTableTitlesToStringPipe } from '../../pipes/convert-message-table-titles-to-string.pipe';
import { SubMessageTableComponent } from '../sub-message-table/sub-message-table.component';

import { MessageTableComponent } from './message-table.component';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { message } from '../../types/messages';
import { BehaviorSubject, of } from 'rxjs';
import { ConvertSubMessageTitlesToStringPipe } from '../../pipes/convert-sub-message-titles-to-string.pipe';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule } from '@angular/material/dialog';
import { EditMessageFieldComponentMock } from '../../mocks/components/EditMessageField.mock';
import { SubMessageTableComponentMock } from '../../mocks/components/SubMessageTable.mock';

let loader: HarnessLoader;

describe('MessageTableComponent', () => {
  let component: MessageTableComponent;
  let fixture: ComponentFixture<MessageTableComponent>;
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
      interfaceMessageRate: '50Hz',
      interfaceSubMessageNumber:'0'
    }]
  }];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatTableModule,
        OseeStringUtilsDirectivesModule,
        OseeStringUtilsPipesModule,
        NoopAnimationsModule,
        MatTooltipModule,
        MatMenuModule,
        MatDialogModule,
        RouterTestingModule
      ],
      declarations: [MessageTableComponent, ConvertMessageTableTitlesToStringPipe, SubMessageTableComponentMock, EditMessageFieldComponentMock],
      providers: [{
        provide: CurrentMessagesService, useValue: {
          messages: of(expectedData),
          BranchId:new BehaviorSubject("10")
      }}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageTableComponent);
    component = fixture.componentInstance;
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  beforeEach(function () {
    var store:any = {};
  
    spyOn(localStorage, 'getItem').and.callFake(function (key) {
      return store[key];
    });
    spyOn(localStorage, 'setItem').and.callFake(function (key, value) {
      return store[key] = value + '';
    });
    spyOn(localStorage, 'clear').and.callFake(function () {
        store = {};
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should expand a row and hide a row on click', async () => {
    expect(component).toBeTruthy();
    const expandRow = spyOn(component, 'expandRow').and.callThrough();
    const hideRow = spyOn(component, 'hideRow').and.callThrough();
    let table = await loader.getHarness(MatTableHarness);
    let buttons = await table.getAllHarnesses(MatButtonHarness);
    await buttons[0].click();
    await expect(expandRow).toHaveBeenCalled();
    fixture.detectChanges();
    await fixture.whenStable();
    let hiddenButtons = await table.getAllHarnesses(MatButtonHarness);
    await hiddenButtons[0].click();
    expect(hideRow).toHaveBeenCalled();
  });

  it('should filter the top level table', async () => {
    let form = await loader.getHarness(MatFormFieldHarness);
    let input = await form.getControl(MatInputHarness);
    await input?.focus();
    //await input?.setValue('Hello');
  })

  // it('should filter the sub level table', async () => {
  // don't know how to test yet
  //   let form = await loader.getHarness(MatFormFieldHarness);
  //   let input = await form.getControl(MatInputHarness);
  //   await input?.setValue('sub message: Hello');
  // })
});
