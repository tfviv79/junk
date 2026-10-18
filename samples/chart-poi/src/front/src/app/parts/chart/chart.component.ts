import {
  AfterViewInit,
  Component,
  ElementRef,
  input,
  output,
  signal,
  ViewChild,
} from '@angular/core';
import {
  DecimalPipe,
} from '@angular/common';
import { ChartEvent, Chart as ChartJs, registerables } from 'chart.js';
import { getRelativePosition } from 'chart.js/helpers';

ChartJs.register(...registerables);

export type ClickEvent = {label: string, value: number|string}

@Component({
  selector: 'chart',
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss',
  imports: [DecimalPipe],
})
export class Chart implements AfterViewInit {
  config = input<any>();

  clickDataset = output<ClickEvent>();

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
    config.options = {...config.options,
      onClick: (e: ChartEvent, _elm: unknown, chart: any) => {
        console.debug("click events", e);
        if (!this.chartObj) {
          return;
        }
        const activeElms = chart.getElementsAtEventForMode(e, 'nearest', { intersect: false }, true);
        if (activeElms.length > 0) {
          const aelm = activeElms[0];
          const datasetIndex = aelm.datasetIndex;
          const index = aelm.index;
          const label = chart.data.labels[index];
          const value = chart.data.datasets[datasetIndex].data[index];
          console.debug("click events: activeData", aelm, label, value);
          this.clickDataset.emit({label, value});
        }

        const canvasPosition = getRelativePosition(e, this.chartObj);

        // Substitute the appropriate scale IDs
        const dataX = this.chartObj.scales['x'].getValueForPixel(canvasPosition.x);
        const dataY = this.chartObj.scales['y'].getValueForPixel(canvasPosition.y);
        console.debug("click events: data", dataX, dataY);
      },
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

    this.chartObj = new ChartJs(this.chartCtx, {
      ...config,
    })
  }
}
