package com.lin.MyTest.advice;

import com.lin.MyTest.exception.AbstractBaseException;
import com.lin.MyTest.exception.webclient.AbstractNetException;
import com.lin.MyTest.model.view.ErrorWrapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdive {

    private static Logger logger = LoggerFactory.getLogger(ExceptionAdive.class);

    @Value("third party request error:%s; url:%s; code:%s; msg:%s;")
    private String ThirdpartyRequestLogTemplate;

    @Value("AbstractBaseException code:%d; msg:%s;")
    private String AbstractBaseExceptionLogTemplate;

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ErrorWrapView handleBindException(BindException exception) {
        return new ErrorWrapView(100001, exception.getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ErrorWrapView handleValidationException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return new ErrorWrapView(100001, bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ErrorWrapView handleValidationException(UnsatisfiedServletRequestParameterException exception) {
        return new ErrorWrapView(100001, "输入参数有误");
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ErrorWrapView handleValidationException(MissingServletRequestParameterException exception) {
        return new ErrorWrapView(100001, "输入参数有误");

    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(AbstractBaseException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ErrorWrapView handleCustomException(AbstractBaseException e) {
        logger.error(String.format(AbstractBaseExceptionLogTemplate, e.getCode(), e.getMsg()), e);
        return new ErrorWrapView(e.getCode(), e.getMsg());
    }

    /**
     * 处理第三方服务异常
     */
    @ExceptionHandler(AbstractNetException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ErrorWrapView handleThirdPartyNetException(AbstractNetException netException) {
        AbstractBaseException exception = null;
        ErrorWrapView errorWrapView = new ErrorWrapView();
        if (exception != null) {
            errorWrapView.setCode(exception.getCode());
            errorWrapView.setMsg(exception.getMsg());
        }

        logger.error(String.format(ThirdpartyRequestLogTemplate, netException.getClass(), netException.getUrl(),
                netException.getCode(), netException.getMsg()), netException);
        return errorWrapView;
    }
}
