import type { Meta, StoryObj } from '@storybook/angular-vite';

import { Chart } from './chart.component';

const meta = {
  component: Chart,
} satisfies Meta<typeof Chart>;

export default meta;
type Story = StoryObj<typeof meta>;

export const 棒グラフ: Story = {
  args: {
    config: {
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
        devicePixelRatio: 1,
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    }
  },
};
