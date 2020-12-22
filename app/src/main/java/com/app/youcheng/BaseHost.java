package com.app.youcheng;


public class BaseHost {
    public static String getBaseUrlHost = "http://47.112.32.114:8080/";

    /**
     * 注册
     */
    public static String registerUrl = "app/register";

    /**
     * 登录
     */
    public static String loginUrl = "app/login";

    /**
     * 上传
     */
    public static String uploadUrl = "app/common/upload";

    /**
     * 获取短信验证码(不需要TOKEN)
     */
    public static String getSmsCodeUrl = "app/sms/code/";

    /**
     * 发送短信验证码(需要TOKEN)
     */
    public static String getSmsCodeNeedUrl = "app/send/code/";

    /**
     * 获取用户企业信息
     */
    public static String infoUrl = "app/info";

    /**
     * 获取用户企业信息
     */
    public static String getUserListUrl = "app/user/info";

    /**
     * 新增用户信息
     */
    public static String addUser = "app/user";

    /**
     * 解绑用户信息
     */
    public static String deleteUser = "app/user/";

    /**
     * 忘记密码
     */
    public static String forgetPwd = "app/user/forget";

    /**
     * 修改手机号码
     */
    public static String phoneUpdate = "app/user/phone";

    /**
     * 修改密码
     */
    public static String pwdUpdate = "app/user/pwd";

    /**
     * 获取短信通知设置/修改短信通知设置
     */
    public static String settingSms = "app/user/setting";

    /**
     * 修改展示账单金额,展示发送人信息
     */
    public static String settingBill = "app/user/setting/bill";

    /**
     * 企业列表
     */
    public static String homeList = "app/index/list";

    /**
     * 企业分类列表
     */
    public static String homeTypeList = "app/index/type/";

    /**
     * 模糊搜索企业列表
     */
    public static String queryEnterpriseList = "app/index/queryEnterpriseList";

    /**
     * 企业详细信息
     */
    public static String homeDetail = "app/index/";

    /**
     * 新增企业圈
     */
    public static String friendsAdd = "app/friends/add";

    /**
     * 移除企业圈
     */
    public static String friendsDelete = "app/friends/delete/";

    /**
     * 查询企业圈列表
     */
    public static String friendsList = "app/friends/list";

    /**
     * 查询新增企业圈列表
     */
    public static String friendsInsertList = "app/friends/insert/list";

    /**
     * 修改企业基本信息
     */
    public static String entInfo = "app/ent/info";

    /**
     * 修改企业名称
     */
    public static String entName = "app/ent/name";

    /**
     * 企业分值明细
     */
    public static String entRank = "app/ent/rank/";

    /**
     * 新增标签
     */
    public static String labelAdd = "app/label/add";

    /**
     * 删除标签
     */
    public static String labelDelete = "app/label/delete/";

    /**
     * 查询标签列表
     */
    public static String labelList = "app/label/list";

    /**
     * 建档
     */
    public static String fileUrl = "app/file/";

    /**
     * 申诉账单
     */
    public static String checkBill = "app/bill/checkBill";

    /**
     * 删除账单
     */
    public static String deleteBill = "app/bill/delete/";

    /**
     * 待评价账单
     */
    public static String evaluateBill = "app/bill/evaluate/list";

    /**
     * 评价企业账单
     */
    public static String evaluationBill = "app/bill/evaluation";

    /**
     * 接收到的账单
     */
    public static String receiveBill = "app/bill/receive/list";

    /**
     * APP获取接收账单详细信息
     */
    public static String receiveDetailBill = "app/bill/receive/";

    /**
     * 发送账单
     */
    public static String sendBill = "app/bill/send";

    /**
     * 发送出去的账单列表
     */
    public static String sendBillList = "app/bill/send/list";

    /**
     * APP获取发送账单详细信息
     */
    public static String sendBillDetail = "app/bill/send/";

    /**
     * 修改账单
     */
    public static String updateBill = "app/bill/updateBill";

    /**
     * 更新账单状态
     */
    public static String updateStatus = "app/bill/updateStatus";

    /**
     * 返回账单
     */
    public static String returnBill = "app/bill/returnBill";

    /**
     * 获取企业余额
     */
    public static String entMoney = "app/ent/money";

    /**
     * 我的邀请码
     */
    public static String shareCode = "app/ent/shareCode";

    /**
     * 请求支付宝支付
     */
    public static String aliPay = "app/pay/aliPay";

    /**
     * 请求余额支付
     */
    public static String localPay = "app/pay/localPay";

    /**
     * 请求微信支付
     */
    public static String wxPay = "app/pay/wxPay";

    /**
     * 消费明细
     */
    public static String payList = "app/pay/list";

    /**
     * 统计账单逾期和未支付数量
     */
    public static String billCount = "app/bill/count";

    /**
     * 企业注销
     */
    public static String cancelAccount = "app/ent/cancelAccount";

//    /**
//     * 提醒对方逾期未支付
//     */
//    public static String noticeBill = "app/bill/notice/bill/";

    /**
     * 退出登录
     */
    public static String logout = "logout";

    /**
     * 获取通知公告列表
     */
    public static String noticeList = "admin/system/notice/list";

    /**
     * 查询账单消息列表
     */
    public static String billMsgList = "app/message/list";

    /**
     * 重生卡
     */
    public static String entReset = "app/ent/reset";

    /**
     * 获取未读消息数量
     */
    public static String msgCount = "app/message/count";

    /**
     * 更新消息状态
     */
    public static String updateMsgStatus = "app/message/update/status/";

    /**
     * 是否建档，返回1是已建档，0是未建档或者正在审核
     */
    public static String fileStatus = "app/file/";

    /**
     * 获取发送短信手机号码
     */
    public static String getPhone = "app/bill/phone/";

    /**
     * 获取账单结束日
     */
    public static String getEndTime = "app/bill/time";

    /**
     * 根据企业名称查询企业代码
     */
    public static String getFileName = "app/file/name/";


}
