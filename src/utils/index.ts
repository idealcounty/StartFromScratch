
//将时间转化为日常方式
export function parseTime(time: string) {
    let times = time.split(/T|\./)
    return times[0] + " " + times[1]
}



