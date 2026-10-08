import { Component, OnInit, resource } from '@angular/core';
import { CMN_IMPORTS } from '@/common';
import { Chart } from '@/app/parts/chart/chart.component';
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
  config = {
    type: 'bar',
    data: {
      labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
      datasets: [{
        label: '# of Votes',
        data: [12, 19, 3, 5, 2, 3],
        borderWidth: 1
      }]
    },
    options: {
      devicePixelRatio: 0.5,
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  }

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
          devicePixelRatio: 3.5,
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

  ngOnInit(): void {
  }
}

