
import { environment } from '@/environments/environment'

const BASE_URL = environment.apiBaseUrl

interface Raws {
  id: number
  treat: number
  age: number
  education: number
  black: boolean
  hispanic: boolean
  married: boolean
  nodegree: boolean
  re74: number
  re75: number
  re78: number
}

interface HistDetail {
  name: string
  key: number | string
  count: number
}

interface ReHistWithDetailRes {
  raws: {
    age: HistDetail[]
    black: HistDetail[]
    education: HistDetail[]
    hispanic: HistDetail[]
    married: HistDetail[]
    nodegree: HistDetail[]
    treat: HistDetail[]
  }
}


class ApiClient {
  async getRaws() {
    const raws = await fetch(BASE_URL + '/raws')
    return raws.json()
  }
  async getReHistogram() {
    const hist = await fetch(BASE_URL + '/re_histogram')
    return hist.json()
  }
  async getReHistogramWithRank(rank: string) : Promise<ReHistWithDetailRes> {
    const hist = await fetch(BASE_URL + `/re_histogram/${rank}`)
    return hist.json()
  }
}

export const api = new ApiClient()
