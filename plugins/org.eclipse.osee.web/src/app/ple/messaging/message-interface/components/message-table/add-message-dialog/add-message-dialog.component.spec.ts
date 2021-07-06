import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { AddMessageDialog } from '../../../types/AddMessageDialog';

import { AddMessageDialogComponent } from './add-message-dialog.component';

describe('AddMessageDialogComponent', () => {
  let component: AddMessageDialogComponent;
  let fixture: ComponentFixture<AddMessageDialogComponent>;
  let dialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
  let dialogData: AddMessageDialog = {
    id: '-1',
    name: '',
    description: '',
    interfaceMessageNumber: '',
    interfaceMessagePeriodicity: '',
    interfaceMessageRate: '',
    interfaceMessageType: '',
    interfaceMessageWriteAccess:''
  }
  let enumServiceMock: Partial<EnumsService> = {
    types: of(['type1', 'type2', 'type3']),
    rates: of(['r1','r2','r3']),
    periodicities:of(['p1','p2','p3'])
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatDialogModule,FormsModule,MatFormFieldModule,MatInputModule,MatSelectModule,MatButtonModule,MatSlideToggleModule,NoopAnimationsModule],
      declarations: [AddMessageDialogComponent],
      providers: [{
        provide: MatDialogRef, useValue: dialogRef
      }, { provide: MAT_DIALOG_DATA, useValue: dialogData },
      {provide: EnumsService, useValue:enumServiceMock}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddMessageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
