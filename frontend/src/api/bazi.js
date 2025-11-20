import Request from 'luch-request'

const http = new Request()

// In development, we point to localhost. 
// In production, this should be the deployed server URL.
http.config.baseURL = 'http://localhost:8080/api/v1'

export const calculateBazi = (data) => {
    return http.post('/bazi/calculate', data)
}
