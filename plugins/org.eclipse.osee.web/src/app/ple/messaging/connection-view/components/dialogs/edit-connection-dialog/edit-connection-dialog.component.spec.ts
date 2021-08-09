import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { graphServiceMock } from '../../../mocks/CurrentGraphService.mock';
import { dialogRef } from '../../../mocks/dialogRef.mock';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { connection, transportType } from '../../../types/connection';

import { EditConnectionDialogComponent } from './edit-connection-dialog.component';

describe('EditConnectionDialogComponent', () => {
  let component: EditConnectionDialogComponent;
  let fixture: ComponentFixture<EditConnectionDialogComponent>;
  let loader: HarnessLoader;
  let dialogData: connection = {
    name: '',
    transportType: transportType.Ethernet,
    applicability:{id:'1',name:'Base'}
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogModule,MatInputModule,MatSelectModule,MatButtonModule,NoopAnimationsModule,FormsModule],
      declarations: [EditConnectionDialogComponent],
      providers: [{ provide: MatDialogRef, useValue: dialogRef },
        { provide: MAT_DIALOG_DATA, useValue: dialogData },
        { provide: CurrentGraphService, useValue: graphServiceMock }]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditConnectionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close without anything returning', async() => {
    let buttons = await loader.getAllHarnesses(MatButtonHarness);
    let spy = spyOn(component, 'onNoClick').and.callThrough();
    if ((await buttons[0].getText()) === 'Cancel') {
      await buttons[0].click();
      expect(spy).toHaveBeenCalled() 
    }
  })
});
