import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddElementDialog } from '../../types/AddElementDialog';

import { AddElementDialogComponent } from './add-element-dialog.component';

describe('AddElementDialogComponent', () => {
  let component: AddElementDialogComponent;
  let fixture: ComponentFixture<AddElementDialogComponent>;
  let dialogData: AddElementDialog = {
    id: '12345',
    name: 'structure',
    type: { id: '', name: '' },
    element: {
      id: '-1',
      name: '',
      description: '',
      notes: '',
      interfaceElementAlterable: true,
      interfaceElementIndexEnd: 0,
      interfaceElementIndexStart:0
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,MatStepperModule,MatDialogModule,MatButtonModule,FormsModule,MatFormFieldModule,MatSelectModule,MatInputModule,MatSlideToggleModule,NoopAnimationsModule],
      declarations: [AddElementDialogComponent],
      providers: [{
        provide: MatDialogRef, useValue: {
        
        }
      },
        {
          provide: MAT_DIALOG_DATA, useValue: dialogData
        }]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddElementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
