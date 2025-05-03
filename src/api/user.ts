import {axios} from '../utils/request'
import {USER_MODULE} from './_prefix'


type RegisterInfo = {
    userId: number,
    userName: string,
    userPassword: string,
    userCreateTime: string,
    userAvatar:string,
}

export const userLogin = (userName: string, password: string) => {
    console.log(userName, password)
    return axios.post(`${USER_MODULE}/login`, null, { params: { userName, password } })
        .then(res => {
            return res;
        });
}

// 用户注册
export const userRegister = (registerInfo: RegisterInfo) => {
    return axios.post(`${USER_MODULE}/register`, registerInfo,
        {headers: {'Content-Type': 'application/json'}})
        .then(res => {
            return res
        })
}

// 获取用户信息
export const userInfo = () => {
    return axios.get(`${USER_MODULE}`)
        .then(res => {
            return res
        })
}
