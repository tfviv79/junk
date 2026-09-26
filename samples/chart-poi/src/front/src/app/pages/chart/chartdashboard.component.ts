import { Component, OnInit, resource } from '@angular/core';
import { CMN_IMPORTS } from '@/common';
import { Chart } from '@/app/parts/chart/chart.component';
import { api } from '@/api';


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
      console.info(raws);
      return raws
    }
  })
  histResource = resource({
    params: undefined,
    loader: async () => {
      const hist = await api.getReHistogram();
      console.info(hist);
      return hist
    }
  })

  ngOnInit(): void {
  }
}

