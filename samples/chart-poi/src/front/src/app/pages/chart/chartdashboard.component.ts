import { Component, OnInit, resource, signal } from '@angular/core';
import { CMN_IMPORTS } from '@/common';
import { Chart, ClickEvent } from '@/app/parts/chart/chart.component';
import { api } from '@/api';


interface ReHist {
  reRank: number
  count: number
}
interface ReHistRes {
  reHistograms: ReHist[]

}

@Component({
  selector: 'chartdashboard',
  templateUrl: './chartdashboard.component.html',
  styleUrl: './chartdashboard.component.scss',
  imports: [ Chart, ... CMN_IMPORTS ],
})
export class ChartDashboardPage implements OnInit {
  histDetailRe = signal<number|string|null>(null)

  rawsResource = resource({
    params: undefined,
    loader: async () => {
      const raws = await api.getRaws();
      return raws
    }
  })

  histResource = resource({
    params: undefined,
    loader: async () => {
      const hist = await api.getReHistogram() as ReHistRes;
      return {
        type: 'bar',
        data: {
          labels: hist.reHistograms.map((x:ReHist) => x.reRank),
          datasets: [{
            label: "rank vs count",
            data: hist.reHistograms.map((x:ReHist) => x.count),
          }],
        },
        options: {
          events: ['click'],
          devicePixelRatio: 1.5,
        },
        plugins: [
          {
            id: "myeventer",
            beforeEvent(chart: any, args: any, pluginOptions: any) {
              const event = args.event;
              console.log("event:", event, chart, args, pluginOptions);
            }
          },
        ],
      }
    }
  })

  histDetailResource = resource({
    params: this.histDetailRe,
    loader: async () => {
      if (this.histDetailRe() === null) {
        return
      }
      const hist = await api.getReHistogramWithRank(this.histDetailRe() as string);

      const gen_bar = function(opts: any, labels: (string|number)[], data: number[]) {
        return {
          type: 'bar',
          data: {
            labels: labels,
            datasets: [{
              label: "rank vs count",
              data: data,
            }],
          },
          options: {
            events: ['click'],
            devicePixelRatio: 1.5,
          },
          plugins: [
            {
              id: "myeventer",
              beforeEvent(chart: any, args: any, pluginOptions: any) {
                const event = args.event;
                console.log("event:", event, chart, args, pluginOptions);
              }
            },
          ],
          ...opts,
        }
      }

      const age = gen_bar({}, hist.raws.age.map((x) => x.key), hist.raws.age.map((x) => x.count))
      const black = gen_bar({
        options: {
          scales: {
            x: { stacked: true, },
            y: { stacked: true, },
          }
        }
      }, hist.raws.black.map((x) => x.key), hist.raws.black.map((x) => x.count))


      return {
        age,
        black,
      }
    },
  })

  ngOnInit(): void {
  }

  datasetHandler(val: ClickEvent): void {
    console.debug("receive event", val);
    this.histDetailRe.set(val.label)
  }
}

