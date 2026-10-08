import {
  Component,
  AfterViewInit,
  ViewChild,
  ElementRef,
  input,
  signal,
} from '@angular/core';
import {
  DecimalPipe,
} from '@angular/common';
import { Chart as ChartJs, registerables } from 'chart.js';

ChartJs.register(...registerables);

@Component({
  selector: 'chart',
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss',
  imports: [DecimalPipe],
})
export class Chart implements AfterViewInit {
  config = input<any>();
  width = input<number>(200);
  height = input<number>(200);

  @ViewChild('chart') chart?: ElementRef;


  chartCtx: CanvasRenderingContext2D|null = null;

  chartObj?: ChartJs;
  imgInfo = signal([{format: '', size: 0, data: ''}]);

  ngAfterViewInit() {
    if (!this.chart) {
      return;
    }
    // canvas01
    this.chartCtx= this.chart.nativeElement.getContext('2d');
    this.drawChart();
  }

  ngOnChanges() {
    this.drawChart();
  }

  ngOnDestroy() {
    if (this.chartObj) {
      this.chartObj.destroy();
    }
  }

  drawChart() {
    if (!this.chartCtx) {
      return;
    }
    if (this.chartObj) {
      this.chartObj.destroy();
      this.chartObj = undefined;
    }
    let config = {...this.config()};
    console.log("config", config);
    if (!config.options) {
      config.options = {}
    }
    if (!config.animation) {
      config.options.animation = {onComplete: undefined}
    }
    let orgHandler = undefined;
    if (config.options.animation.onComplete) {
      orgHandler = config.options.animation.onComplete
    }
    config.options.animation = { ...config.animation, onComplete: () => {
      if (orgHandler) {
        orgHandler();
      }
      console.log("onComplete");
      if (!this.chartObj) {
        return;
      }
      const imgPng = this.chartObj.toBase64Image();
      const imgJpeg = this.chartObj.toBase64Image('image/jpeg', 1);
      this.imgInfo.set([
        { 'format': 'png', size: imgPng.length / 4 * 3 / 1024, data: imgPng},
        { 'format': 'jpeg', size: imgJpeg.length / 4 * 3 / 1024, data: imgJpeg},
      ]);
    }}

    this.chartObj = new ChartJs(this.chartCtx, config)
  }
}
