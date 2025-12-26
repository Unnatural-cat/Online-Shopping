package com.example.OnlineShopping.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 收货地址请求DTO
 */
@Data
public class AddressRequest {
    /**
     * 收件人姓名
     */
    @NotBlank(message = "收件人姓名不能为空")
    @Size(max = 50, message = "收件人姓名长度不能超过50个字符")
    private String receiverName;

    /**
     * 收件人电话
     */
    @NotBlank(message = "收件人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String receiverPhone;

    /**
     * 省份
     */
    @Size(max = 50, message = "省份长度不能超过50个字符")
    private String province;

    /**
     * 城市
     */
    @Size(max = 50, message = "城市长度不能超过50个字符")
    private String city;

    /**
     * 区/县
     */
    @Size(max = 50, message = "区/县长度不能超过50个字符")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String detailAddress;

    /**
     * 邮政编码
     */
    @Size(max = 10, message = "邮政编码长度不能超过10个字符")
    private String postalCode;

    /**
     * 是否设为默认地址
     */
    private Boolean isDefault;
}

