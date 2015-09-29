/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package cn.ttsk.library.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088501891311140";

	//收款支付宝账号
	public static final String DEFAULT_SELLER = "xiewei@sooker.com";
	
	public static final String NOTIFY_URL = "http://api.vickeynce.com/nce2pay/alipaynotify";

	//商户私钥，自助生成
	public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOtnt5zMKK9g1GQFSX+AhVxgRr3A/UKxxxhIyaaOrgdcml9rHBXjOB9ZdL3BEurlcBcI7WO9C/EhSROZgp8K/aNq93HNsgpFC4rnPcxvf/zQKXCPlFqgVgFRC6jrtuMWu9Eow0IbfPziHYR+OYorghlR6lQgvet92ndMVWGYbJ75AgMBAAECgYAQj/yQMKEQLczjuKeCLAW2CSQRB7oJ7j4mzEA70sAVzoMiAYQ5WgJB2mZnT2VIV5o6VKt3H/7uGTxt/5f8HqbXrQxyEAJbcZHgNGut1xys0YouZVLwsxcSJR21NTi/qXuVw8862cI3hvjZFr0xyUCaMcDsMHk5qtzR+vZumw6/sQJBAPdccUFAQdKxxDGY0uyzVErJ6a/ASUDTph4+pocvUiP3mWyCjipDDVjSradeaTgTNL9CXpkEE56t9nR1Qest7PUCQQDzoGGgdrL2+ATf9Oh6YyzeCaRTu/icF/fO7fhKxV2isZrfAhiLSfcFW33dVgT/9YCsd1PHt4Dsani/3kKSPid1AkEAjJOXtaqL/e6S/lDIxkZgCCTZzszrlJDz3kpin38wIkqrgbRskO4MXsdUc0K/b2PS3UMdub5MXCinYKXm8X3nJQJAEGejS0fS4zhuM5ptMRiOg+EIVK8anYArqp5xkq9Zj8P/1rZwaqivXof7oLTTH10rrfFXDeK4ZrMAsILJ3skapQJBANyqtUvDqoM+4qR7xRGzbFSaSzZqJKLiWcXTXWcrEQATgfcccafnidkAeBGkhn6AC9huePQnMTeyMqeTD5qUP3E=";

	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

}
