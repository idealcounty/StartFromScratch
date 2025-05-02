import {axios} from '../utils/request'
import {USER_MODULE} from './_prefix'

type LoginInfo = {
    phone: string,
    password: string
}

type RegisterInfo = {
    userId: number,
    userName: string,
    userPhone: string,
    userPassword: string,
    userAddress: string,
    userRole: string,
    userCreateTime: string,
    userBalance: number,
    userAvatar: string,
}

export const userLogin = (loginInfo: LoginInfo) => {
    return axios.post(`${USER_MODULE}/login`, null, {params: loginInfo})
        .then(res => {
            return res
        })
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
