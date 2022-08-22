package com.pickme.reggie.common.exception;

import com.pickme.reggie.common.MC;
import com.pickme.reggie.common.R;
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
    public R<String> exceptionHandler(Exception ex) {
        ex.printStackTrace();
        return R.error(MC.E_UNKNOWN + ex.getMessage());
    }

    /**
     * 处理业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public R<String> BusinessException(BusinessException ex) {
        return R.error(ex.getMessage());
    }
}
