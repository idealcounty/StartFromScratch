import {axios} from '../utils/request'
import {ARCHIVE_MODULE} from './_prefix'


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