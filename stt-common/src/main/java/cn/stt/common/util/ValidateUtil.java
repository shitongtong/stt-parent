package cn.stt.common.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @ClassName ValidateUtil
 * @Description TODO
 * @Author shitt7
 * @Date 2019/3/1 10:26
 * @Version 1.0
 */
public class ValidateUtil {
    public static <T> void validate(T t) throws ValidationException {
        ValidatorFactory vFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = vFactory.getValidator();
        Set<ConstraintViolation<T>> set = validator.validate(t);
        if (set.size() > 0) {
            StringBuilder validateError = new StringBuilder();
            for (ConstraintViolation<T> val : set) {
                validateError.append(val.getMessage());
            }
            throw new ValidationException(validateError.toString());
        }
    }
}
