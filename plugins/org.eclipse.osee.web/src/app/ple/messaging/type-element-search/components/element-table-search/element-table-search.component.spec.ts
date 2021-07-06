import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TestScheduler } from 'rxjs/testing';
import { SearchService } from '../../services/router/search.service';

import { ElementTableSearchComponent } from './element-table-search.component';

describe('ElementTableSearchComponent', () => {
  let component: ElementTableSearchComponent;
  let fixture: ComponentFixture<ElementTableSearchComponent>;
  let loader: HarnessLoader;
  let service: SearchService;
  let scheduler: TestScheduler;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatInputModule,MatFormFieldModule,FormsModule,NoopAnimationsModule],
      declarations: [ ElementTableSearchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ElementTableSearchComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    service = TestBed.inject(SearchService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update search terms to Hello World', () => {
    scheduler.run(async () => {
      (await (await loader.getHarness(MatFormFieldHarness)).getControl(MatInputHarness))?.setValue('Hello World')
      let values={a:'hello world'}
      let marble = 'a';
      scheduler.expectObservable(service.searchTerm).toBe(marble, values);
    });
  });
});
