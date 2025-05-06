import { createRouter, createWebHashHistory } from "vue-router"

const router = createRouter({
    history: createWebHashHistory(),
    routes: [{
        path: '/', // 添加默认路由
        component: () => import('../views/user/Home.vue'), // 假设你的主页组件是 Home.vue
        meta: { title: '主页' }
    },{
        path: '/home', // 添加默认路由
        component: () => import('../views/user/Home.vue'), // 假设你的主页组件是 Home.vue
        meta: { title: '主页' }
    },{
        path: '/login',
        component: () => import('../views/user/Login.vue'),
        meta: { title: '用户登录' }
    },{
        path: '/register',
        component: () => import('../views/user/Register.vue'),
        meta: { title: '用户注册' }
    },{
        path: '/init',
        component: () => import('../views/user/Init.vue'),
        meta: { title: '初始点数分配' }
    },{
        path: '/choose',
        component: () => import('../views/user/Choose.vue'),
        meta: { title: '初始点数分配' }
    },]
})

router.beforeEach((to, _, next) => {
    const token: string | null = sessionStorage.getItem('token');
    const role: string | null = sessionStorage.getItem('role')

    if (to.meta.title) {
        document.title = to.meta.title
    }

    if (token) {
        if (to.meta.permission) {
            if (to.meta.permission.includes(role!)) {
                next()
            } else {
                next('/404')
            }
        } else {
            next()
        }
    } else {
        if (to.path === '/login') {
            next();
        } else if (to.path === '/register') {
            next()
        } else {
            next('/login')
        }
    }
})

export { router }
