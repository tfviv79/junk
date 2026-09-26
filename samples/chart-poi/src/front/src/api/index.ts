
import { environment } from '@/environments/environment'

const BASE_URL = environment.apiBaseUrl

class ApiClient {
  async getRaws() {
    const raws = await fetch(BASE_URL + '/raws')
    return raws.json()
  }
  async getReHistogram() {
    const hist = await fetch(BASE_URL + '/re_histogram')
    return hist.json()
  }
}

export const api = new ApiClient()
