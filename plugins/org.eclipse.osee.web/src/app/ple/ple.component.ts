import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'ple-main',
  templateUrl: './ple.component.html',
  styleUrls: ['./ple.component.sass']
})
export class PleComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
  }
  navigateTo(location: string) {
    this.router.navigate([location], {
      relativeTo: this.route.parent,
      queryParamsHandling: 'merge',
    });
  }
}
