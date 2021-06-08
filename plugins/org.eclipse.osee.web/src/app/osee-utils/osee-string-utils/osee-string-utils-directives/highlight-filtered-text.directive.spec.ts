import { Component, DebugElement, Renderer2, Type } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HighlightFilteredTextDirective } from './highlight-filtered-text.directive';
import { By } from '@angular/platform-browser';
@Component({
  selector: 'my-test-component',
  template: '<div appHighlightFilteredText searchTerms="this" text="Hello World This is Text" classToApply="highlightTextClass">Hello World This is Text</div>'
})
export class TestComponent {
}

describe('HighlightFilteredTextDirective', () => {
  let renderer: Renderer2;
  let inputEl: DebugElement;
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>
  beforeEach((async () => {
    TestBed.configureTestingModule({
      declarations: [
        TestComponent,
        HighlightFilteredTextDirective,
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
    inputEl = fixture.debugElement;
    component = fixture.componentInstance;
    renderer = fixture.componentRef.injector.get(Renderer2 as Type<Renderer2>);
  }))


  it('should create an instance', () => {
    const directive = new HighlightFilteredTextDirective(inputEl,renderer);
    expect(directive).toBeTruthy();
  });
  
  it('should insert a span of type highlightTextClass', () => {
    fixture.detectChanges();
    let nativeEl = inputEl;
    let spanDe = nativeEl.query(By.css('span'));
    let span = spanDe.nativeElement;
    expect(span.classList.contains('highlightTextClass')).toBeTruthy();
  })
});
