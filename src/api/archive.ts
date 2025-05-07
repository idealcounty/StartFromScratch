import {axios} from '../utils/request'
import {ARCHIVE_MODULE, USER_MODULE} from './_prefix'


export type archiveInfo = {
    archiveScience: number,
    archiveHealth: number,
    archiveGame: number,
    archiveSocial: number,
    archiveMoney:number,
    userId: number,
}

export const archiveCreate = (archiveinfo:archiveInfo) => {
    console.log(archiveinfo)
    return axios.post(`${ARCHIVE_MODULE}/createArchive`, archiveinfo,
        {headers: {'Content-Type': 'application/json'}})
        .then(res => {
            return res
        })
}

export const getArchive = (userId:number) => {
    return axios.get(`${ARCHIVE_MODULE}/all/${userId}`).then(res=>{
        return res
    })
}