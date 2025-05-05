import {axios} from '../utils/request'
import {CHAT_MODULE} from './_prefix'

type ChatInfo = {
    message: string,
}

export const Chatsend = (chatinfo:ChatInfo) => {
    return axios.post(`${CHAT_MODULE}/send`, chatinfo,
        {headers: {'Content-Type': 'application/json'}})
        .then(res => {
            return res
        })
}