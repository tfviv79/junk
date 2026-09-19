import { Component, AfterViewInit, ViewChild, ElementRef, input } from '@angular/core';
import { Chart as ChartJs, registerables } from 'chart.js';

ChartJs.register(...registerables);

@Component({
  selector: 'chart',
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss',
})
export class Chart implements AfterViewInit {
  config = input<any>();

  @ViewChild('chart') chart?: ElementRef;

  chartCtx: CanvasRenderingContext2D|null = null;

  chartObj?: ChartJs;

  ngAfterViewInit() {
    if (!this.chart) {
      return;
    }
    // canvas01
    this.chartCtx= this.chart.nativeElement.getContext('2d');
    if (!this.chartCtx) {
      return;
    }
    this.chartObj = new ChartJs(this.chartCtx, this.config())
  }

  ngOnChanges() {
    if (this.chartObj) {
      this.chartObj.destroy();
      this.chartObj = undefined;
    }
    if (!this.chartCtx) {
      return;
    }
    this.chartObj = new ChartJs(this.chartCtx, this.config())
  }

  ngOnDestroy() {
    if (this.chartObj) {
      this.chartObj.destroy();
    }
  }
}
