import { Directive, ElementRef, Input, Renderer2, SimpleChanges } from '@angular/core';

@Directive({
  selector: '[appHighlightFilteredText]'
})
export class HighlightFilteredTextDirective {
  @Input() searchTerms: string = "";
  @Input() text: string = "";
  @Input() classToApply: string = "";
  constructor(private el: ElementRef, private renderer: Renderer2) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (!this.searchTerms || !this.searchTerms.length || !this.classToApply) {
      this.renderer.setProperty(this.el.nativeElement, 'innerHTML', this.text);
      return;
    }

    this.renderer.setProperty(
      this.el.nativeElement,
      'innerHTML',
      this.getFormattedText()
    )
  }
  getFormattedText() {
    const re = new RegExp(`(${ this.searchTerms })`, 'i');
    let returnValue=this.text?.replace(re, `<span class="${this.classToApply}">$1</span>`);
    return returnValue;
  }
}
