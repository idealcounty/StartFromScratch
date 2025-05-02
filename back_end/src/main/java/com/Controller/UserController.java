package com.Controller;
import com.Service.UserService;
import com.VO.ResultVO;
import com.VO.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResultVO<Boolean> register(@RequestBody UserVO userVO){
        return ResultVO.buildSuccess(userService.register(userVO));
    }

    @PostMapping("/login")
    public ResultVO<String> login(@RequestParam("userName") String userName, @RequestParam("password") String password){
        return ResultVO.buildSuccess(userService.login(userName, password));
    }

    @GetMapping
    public ResultVO<UserVO> getInformation(){
        return ResultVO.buildSuccess(userService.getInformation());
    }

    @PostMapping ("/{userId}")
    public ResultVO<Boolean> updateInformation(@PathVariable int userId,@RequestBody UserVO userVO){
        boolean success = userService.updateInformation(userId,userVO);
        if (success) {
            return ResultVO.buildSuccess(true);
        }
        else {
            return ResultVO.buildFailure("请求信息失败");
        }
    }

}
