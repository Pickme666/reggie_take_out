package com.pickme.reggie.common.exception;

import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.Res;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局异常处理
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理其他或未知异常
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Res<String> exceptionHandler(Exception ex) {
        ex.printStackTrace();
        return Res.error(MC.E_UNKNOWN + ex.getMessage());
    }

    /**
     * 处理业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Res<String> BusinessException(BusinessException ex) {
        return Res.error(ex.getMessage());
    }
}
