import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { enumsServiceMock } from 'src/app/ple/messaging/shared/mocks/EnumsService.mock';
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { graphServiceMock } from '../../../mocks/CurrentGraphService.mock';
import { dialogRef } from '../../../mocks/dialogRef.mock';
import { CurrentGraphService } from '../../../services/current-graph.service';

import { CreateConnectionDialogComponent } from './create-connection-dialog.component';

describe('CreateConnectionDialogComponent', () => {
  let component: CreateConnectionDialogComponent;
  let fixture: ComponentFixture<CreateConnectionDialogComponent>;
  let loader: HarnessLoader;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatDialogModule,MatInputModule,MatFormFieldModule,MatSelectModule,MatButtonModule,NoopAnimationsModule,FormsModule],
      declarations: [CreateConnectionDialogComponent],
      providers: [{ provide: MatDialogRef, useValue: dialogRef },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: CurrentGraphService, useValue: graphServiceMock },
        {provide:EnumsService,useValue:enumsServiceMock}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateConnectionDialogComponent);
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

  it('should select a new transport type', async() => {
    let form = loader.getHarness(MatFormFieldHarness.with({ selector: '#connection-transport-type-selector' }));
    let select = (await (await form).getControl(MatSelectHarness));
    await select?.open();
    expect((await select?.getOptions())?.length).toEqual(3);
    await select?.clickOptions({ text: 'HSDN' });
    expect(await select?.getValueText()).toEqual('HSDN')
  })

  it('should select a new node to connect to', async() => {
    let form = loader.getHarness(MatFormFieldHarness.with({ selector: '#connection-node-selector' }));
    let select = (await (await form).getControl(MatSelectHarness));
    await select?.open();
    expect((await select?.getOptions())?.length).toEqual(2);
    await select?.clickOptions({ text: 'Second' });
    expect(await select?.getValueText()).toEqual('Second')
  })

  it('should enter a description', async() => {
    let form = loader.getHarness(MatFormFieldHarness.with({ selector: '#connection-description-field' }));
    let input = (await (await form).getControl(MatInputHarness));
    expect(await input?.getType()).toEqual("text");
    await input?.setValue('Description');
    expect(await input?.getValue()).toEqual('Description');
  })

  it('should enter a name', async() => {
    let form = loader.getHarness(MatFormFieldHarness.with({ selector: '#connection-name-field' }));
    let input = (await (await form).getControl(MatInputHarness));
    expect(await input?.getType()).toEqual("text");
    await input?.setValue('Name');
    expect(await input?.getValue()).toEqual('Name');
  })
});
