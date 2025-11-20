import { defineStore } from 'pinia'

export const useBaziStore = defineStore('bazi', {
    state: () => ({
        result: null
    }),
    actions: {
        setResult(data) {
            this.result = data
        }
    }
})
