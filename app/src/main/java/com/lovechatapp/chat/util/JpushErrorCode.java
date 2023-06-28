package com.lovechatapp.chat.util;

import com.lovechatapp.chat.base.AppManager;

/**
 * 处理极光error code
 */
public class JpushErrorCode {

    public static void toastCode(int code, String msg) {
        String message = "发送失败" + code;
        switch (code) {
            /////////////////////////////////////////app error//////////////////////////////////////////
            case 871101:
//                Invalid request parameters.请求参数不合法
                message = "请求参数不合法";
                break;
            case 871102:
//                Request failed.please check your network connection.请求失败，请检查网络
                message = "请求失败，请检查网络";
                break;
            case 871103:
//                Server returned an unexpected error code.服务器内部错误
                message = "服务器内部错误";
                break;
            case 871104:
//                Server internal error.服务器内部错误
                message = "服务器内部错误";
                break;
            case 871105:
//                User info not found.请求的用户信息不存在
                message = "用户不存在";
                break;
            case 871201:
//                Get response timeout, please try again later.响应超时
                message = "发送超时";
                break;
            case 871300:
//                Have not logged in.api调用发起者尚未登录
                message = "Im不可用";
                break;
            case 871301:
//                Invalid parameters.api调用传入的参数不合法
                message = "参数不合法";
                break;
            case 871302:
//                Message content exceeds its max length.发送消息的消息体过大，整个消息体大小不能超过4k
                message = "消息内容过多";
                break;
            case 871303:
//                Invalid username.用户名不合法
                message = "用户名不合法";
                break;
            case 871304:
//                Invalid password.密码不合法
                message = "密码不合法";
                break;
            case 871305:
//                Invalid name.名称不合法（包括nickname groupname notename）
                message = "名称不合法";
                break;
            case 871306:
//                Invalid input.其他输入不合法
                message = "输入不合法";
                break;
            case 871307:
                message = "用户不存在";
                break;
//                Some user not exists, operate failed.添加或移除群成员时，传入的成员列表中有用户不存在
            case 871308:
                message = "初始化";
                break;
//                SDK have not init yet.SDK尚未初始化
            case 871309:
                message = "消息中包含的文件不存在";
                break;
//                Attached file not found.消息中包含的文件不存在
            case 871310:
                message = "网络连接已断开，请检查网络";
                break;
//                Network not available, please check your network connection.网络连接已断开，请检查网络
            case 871311:
                message = "下载头像失败";
                break;
//                User avatar not specified.download avatar failed.用户未设定头像，下载头像失败
            case 871312:
                message = "创建ImageContent失败";
                break;
//                Create image content failed.创建ImageContent失败
            case 871313:
                message = "协议版本号不匹配";
                break;
//                Message parse error, version not match.消息解析出错，协议版本号不匹配
            case 871314:
                message = "缺少关键参数";
                break;
//                Message parse error, lack of key parameter.消息解析出错，缺少关键参数
            case 871315:
                message = "消息解析出错";
                break;
//                Message parse error, check logcat for more information 消息解析出错
            case 871317:
                message = "不能给自己消息";
                break;
//                Target user cannot be yourself.操作目标用户不能是自己
            case 871318:
//                Illegal message content.不合法的消息体，出现这个问题可能是由于上层没有参照集成文档进行混淆配置导致的，关于jmessage的混淆配置见集成指南
                message = "不合法的消息体";
                break;
            case 871319:
                message = "创建转发消息失败";
                break;
//                Create ForwardMessage failed 创建转发消息失败，具体原因见logcat打印
            case 871320:
                message = "消息标记已读失败";
                break;
//                Set message HaveRead status failed.将消息标记为已读时出现问题，可能这条消息已经是已读状态，或者这条消息本身不是接受类型的消息
            case 871321:
                message = "获取未回执详情失败";
                break;
//                Get receipt details failed.获取未回执详情失败，只有消息的发送者可以查询消息的未回执详情
            case 871322:
                message = "获取未回执详情失败";
                break;
//                Get receipt details failed.获取未回执详情失败，这条消息尚未成功发送，只有成功发送的消息可以查询未回执详情
            case 871323:
                message = "聊天室不存在";
                break;
//                Chatroom not exist.请求的聊天室信息未找到，该聊天室不存在
            case 871324:
                message = "消息类型不合法";
                break;
//                Illegal message content type, when send message.发送消息时消息体类型不合法，
//                注意eventNotification和prompt类型的消息体不能发送
            case 871325:
                message = "消息状态不合法";
                break;
//                Illegal message status.only created or send_failed message can be sent.发送消息时消息状态不合法，
//                只有消息状态为创建和发送失败的消息可以被发送
            case 871326:
                message = "不支持的操作";
                break;
//                unsupported operation.不支持的操作，例如聊天室撤回消息
            case 871327:
                message = "已取消";
                break;
//                operation is cancelled 操作已被取消，上层调用取消接口（消息发送取消，附件下载取消），取消成功后返回此错误码
            case 871402:
                message = "文件上传失败";
                break;
//                Upload file failed.auth error.文件上传失败
            case 871403:
                message = "文件上传失败";
                break;
//                Upload file failed.文件上传失败
            case 871404:
                message = "文件下载失败";
                break;
//                Download file failed.文件下载失败
            case 871501:
                message = "token无效";
                break;
//                Push register error, appkey and appid not match.appkey与包名不匹配或者token无效
            case 871502:
                message = "appKey无效";
                break;
//                Push register error, invalid appkey.appKey无效。请检查 AndroidManifest.xml 里的 appKey 配置，
//                它必须是从 JPush 控制台创建应用得到的。
            case 871503:
                message = "appKey与平台不匹配";
                break;
//                Push register error, appkey not matches platform appKey与平台不匹配。有可能在 JPush 控制台上，
//                未配置此 appKey 支持 Android 平台。
            case 871504:
                message = "注册未完成，请稍候重试";
                break;
//                Push register not finished.Push 注册未完成，请稍候重试。如果持续出现这个问题，可能你的 JPush 配置不正确。
            case 871505:
                message = "包名在控制台上不存在";
                break;
//                Push register error,package not exists.Push 注册失败, 对应包名在控制台上不存在。
            case 871506:
                message = "注册失败, 对应包名在控制台上不存在";
                break;
//                Push register error, invalid IMEI.Push 注册失败，设备IMEI不合法
            /////////////////////app error end/////////////////////////////////////
            /////////////////////sever error/////////////////////////////////////
            case 898000:
//                Server internal error 内部错误
                message = "内部错误";
                break;
            case 898001:
//                User exist 用户已存在
                message = "用户已存在";
                break;
            case 898002:
//                No such user 用户不存在, 并且指出了不存在的用户名称
                message = "用户不存在";
                break;
            case 898003:
//                Parameter invalid !请求参数不合法
                message = "请求参数不合法";
                break;
            case 898004:
//                Password error 更新密码操作，用户密码错误
                message = "更新密码操作，用户密码错误";
                break;
            case 898006:
//                Group id invalid Group id不存在
                message = "Group id不存在";
                break;
            case 898007:
//                Missing authen info 校验信息为空
                message = "校验信息为空";
                break;
            case 898008:
//                Basic authentication failed.校验失败
                message = "校验失败";
                break;
            case 898009:
//                Appkey not exists appkey不存在
                message = "appkey不存在";
                break;
            case 898010:
//                Token expired API请求 token 过期。正常情况下SDK会自动重新获取 token。
                message = "token 过期";
                break;
            case 898011:
//                no auth to query other appkey 's user or appkey no exist	查询的appkey不具备跨应用权限 或者appkey不存在
                message = "appkey不存在";
                break;
            case 898030:
//                Server response time out, please try again later 系统繁忙，稍后重试
                message = "系统繁忙，稍后重试";
                break;
            case 898031:
//                project not exist 音视频服务还未开通，请参考doc和portal里的规则开通服务
                message = "音视频服务还未开通";
                break;
            case 899000:
//                Server internal error 系统内部错误
                message = "系统内部错误";
                break;
            case 899001:
//                User exist 用户已存在
                message = "用户已存在";
                break;
            case 899002:
//                No such user 用户不存在
                message = "用户不存在";
                break;
            case 899003:
//                parameter invalid 参数错误，Request Body参数不符合要求;
                message = "参数错误";
//                resend 值不符合要求;
//                用户名或者密码不合法;
//                群组Group id不合法
                break;
            case 899006:
//                Group id invalid Group id 不存在
                message = "Group id 不存在";
                break;
            case 899007:
//                Missing authen info 校验信息为空
                message = "校验信息为空";
                break;
            case 899008:
//                Basic authentication failed 校验失败
                message = "校验失败";
                break;
            case 899009:
//                Appkey not exit Appkey不存在
                message = "Appkey不存在";
                break;
            case 899011:
//                Repeat to add the members 重复添加群成员
                message = "重复添加群成员";
                break;
            case 899012:
//                No enough space for members 没有足够位置添加此次请求的成员
                message = "没有足够位置添加此次请求的成员";
                break;
            case 899013:
//                User list is bigger than 500 注册列表大于500，批量注册最大长度为500
                message = "注册列表大于500";
                break;
            case 899014:
//                User list is bigger than 500 添加操作操作成功 remove操作有username不存在讨论组中 remove失败
                message = "添加操作操作成功";
                break;
            case 899015:
//                User 's group are full can not continue	用户加入讨论组达到上限
                message = "用户加入讨论组达到上限";
                break;
            case 899016:
//                No authority to send message 用户没有管理员权限发送信息
                message = "用户没有管理员权限发送信息";
                break;
            case 899017:
//                There are usernames exist in blacklist 用户已经被添加进黑名单
                message = "用户已经被添加进黑名单";
                break;
            case 899018:
//                Admin can not be added into blacklist 管理员不能被添加进黑名单
                message = "管理员不能被添加进黑名单";
                break;
            case 899019:
//                Here are usernames not exist in blacklist 删除目标黑名单用户不存在黑名单中
                message = "删除目标黑名单用户不存在黑名单中";
                break;
            case 899020:
//                no auth to operating other appkey 跨应用失败
                message = "跨应用失败";
                break;
            case 899021:
//                should use cross app api 查询失败 应该使用跨应用api
                message = "查询失败 应该使用跨应用api";
                break;
            case 899043:
//                duplicate add user 已经设置此用户为消息免打扰，重复设置错误
                message = "已经设置此用户为消息免打扰，重复设置错误";
                break;
            case 899044:
//                user is not exist in setting 取消消息免打扰用户时，该用户不存在当前设置中
                message = "取消消息免打扰用户时，该用户不存在当前设置中";
                break;
            case 899045:
//                group is not exist 设置群组消息免打扰时，群组不存在该系统中
                message = "设置群组消息免打扰时，群组不存在该系统中";
                break;
            case 899046:
//                user is not in group 设置群组消息免打扰时，设置的群组，用户不在该群组中
                message = "设置群组消息免打扰时，设置的群组，用户不在该群组中";
                break;
            case 899047:
//                duplicate add group 已经设置此群组为消息免打扰，重复设置错误
                message = "已经设置此群组为消息免打扰，重复设置错误";
                break;
            case 899048:
//                already open global 已经设置全局为消息免打扰，重复设置错误
                message = "已经设置全局为消息免打扰，重复设置错误";
                break;
            case 899049:
//                group is not exist in setting 取消消息免打扰群组时，该群组不存在当前设置中
                message = "取消消息免打扰群组时，该群组不存在当前设置中";
                break;
            case 899050:
//                already close global 已经设置全局为消息免打扰，重复设置错误
                message = "已经设置全局为消息免打扰，重复设置错误";
                break;
            case 899070:
//                添加的好友已经存在好友列表中
                message = "添加的好友已经存在好友列表中";
                break;
            case 899071:
//                更新的好友不存在好友列表中
                message = "更新的好友不存在好友列表中";
                break;
            case 899030:
//                Server response time out, please try again later 系统繁忙，稍后重试
                message = "系统繁忙，稍后重试";
                break;
            case 899081:
//                room id no exist 聊天室ID不存在
                message = "聊天室ID不存在";
                break;
            case 899082:
//                user not in room 用户不在聊天室中
                message = "用户不在聊天室中";
                break;
            case 800003:
//                appkey not exist appkey未注册
                message = "appkey未注册";
                break;
            case 800005:
//                user not exist 用户ID未注册（appkey无该UID）
                message = "用户ID未注册";
                break;
            case 800006:
//                user not exist 用户ID不存在（数据库中无该UID）
                message = "用户ID不存在";
                break;
            case 800008:
//                invalid request 请求类型无法识别
                message = "请求类型无法识别";
                break;
            case 800009:
//                system error 服务器系统错误
                message = "服务器系统错误";
                break;
            case 800012:
//                user never login 发起的用户处于登出状态，账号注册以后从未登录过，需要先登录
                message = "发起的用户处于登出状态，账号注册以后从未登录过，需要先登录";
                break;
            case 800013:
//                user logout 发起的用户处于登出状态，请求的用户已经登出，需要先登录
                message = "发起的用户处于登出状态，请求的用户已经登出，需要先登录";
                break;
            case 800014:
//                appkey not match 发起的用户appkey与目标不匹配
                message = "发起的用户appkey与目标不匹配";
                break;
            case 800016:
//                device not match 发起的用户设备不匹配, 当前请求的设备与上次登录的设备不匹配导致，需要先登录
                message = "发起的用户设备不匹配, 当前请求的设备与上次登录的设备不匹配导致，需要先登录";
                break;
            case 800017:
//                beyond the frequency limit 发送请求频率超过系统限制
                message = "发送请求频率超过系统限制";
                break;
            case 800018:
//                group id not exist 群组ID不存在
                message = "群组ID不存在";
                break;
            case 800019:
//                req user not in group 请求用户不在群组中
                message = "请求用户不在群组中";
                break;
            case 800020:
//                request user no permission 请求用户无操作权限
                message = "请求用户无操作权限";
                break;
            case 801003:
//                user not exist 登录的用户名未注册，登录失败
                message = "登录的用户名未注册，登录失败";
                break;
            case 801004:
//                invalid password 登录的用户密码错误，登录失败
                message = "登录的用户密码错误，登录失败";
                break;
            case 801005:
//                invalid device 登录的用户设备有误，登录失败
                message = "登录的用户设备有误，登录失败";
                break;
            case 801006:
//                user disabled 登录的用户被禁用，登录失败
                message = "登录的用户被禁用，登录失败";
                break;
            case 801007:
//                multi channel online error 多通道同时登录错误，登录失败
                message = "多通道同时登录错误，登录失败";
                break;
            case 802002:
//                username not match 登出用户名和登录用户名不匹配，登出失败
                message = "登出用户名和登录用户名不匹配，登出失败";
                break;
            case 803001:
//                system error 发送消息失败，状态存储异常
                message = "发送消息失败，状态存储异常";
                break;
            case 803002:
//                system error 发送消息失败，系统网络异常
                message = "发送消息失败，系统网络异常";
                break;
            case 803003:
//                target user not exist 发送消息失败，目标用户未注册或不存在
                message = "发送消息失败，目标用户未注册或不存在";
                break;
            case 803004:
//                target group not exist 发送消息失败，目标讨论组不存在
                message = "发送消息失败，目标讨论组不存在";
                break;
            case 803005:
//                user not in group 发送消息失败，发起者不在目标讨论组中
                message = "发送消息失败，发起者不在目标讨论组中";
                break;
            case 803006:
//                user not permitted 发送消息失败，发起者权限不够或者类别不匹配
                message = "发送消息失败，发起者权限不够或者类别不匹配";
                break;
            case 803007:
//                length of message exceed limit 发送消息失败，消息长度超过限制
                message = "发送消息失败，消息长度超过限制";
                break;
            case 803008:
//                user in blacklist 发送消息失败，发送者已被接收者拉入黑名单，仅限单聊
                message = "发送消息失败，发送者已被接收者拉入黑名单，仅限单聊";
                break;
            case 803009:
//                the message contains sensitive word:
//            the word 发送消息失败，消息内容包含敏感词汇：具体敏感词
                message = "发送消息失败，消息内容包含敏感词汇：" + msg.replace("the message contains sensitive word:", "");
                break;
            case 803010:
//                beyond the frequency limit 发送消息失败，发送频率超过系统限制，无法发送，客户端限制每分钟60条
                message = "发送消息失败，发送频率超过系统限制，无法发送，客户端限制每分钟60条";
                break;
            case 803011:
//                msg content json error 发送消息失败，消息格式配置错误
                message = "发送消息失败，消息格式配置错误";
                break;
            case 803012:
//                can not chat while silent 发送消息失败，请求用户被管理员禁言
                message = "发送消息失败，请求用户被管理员禁言";
                break;
            case 805001:
//                target user not exist 目标用户不存在
                message = "目标用户不存在";
                break;
            case 805002:
//                already is friend 添加好友失败：双方已经是好友
                message = "添加好友失败：双方已经是好友";
                break;
            case 805003:
//                user not friend 删除好友失败：目标用户并不是自己的好友
                message = "删除好友失败：目标用户并不是自己的好友";
                break;
            case 805004:
//                invalid friend memo 修改好友备注失败：备注内容无效，无法修改
                message = "修改好友备注失败：备注内容无效，无法修改";
                break;
            case 805006:
//                invitation event is not valid 添加好友失败：邀请事件无效
                message = "添加好友失败：邀请事件无效";
                break;
            case 808001:
//                group name invalid 创建群组时群名为空，创建群组失败
                message = "创建群组时群名为空，创建群组失败";
                break;
            case 808002:
//                user not permitted to create group 用户无创建群组权限，创建群组失败
                message = "用户无创建群组权限，创建群组失败";
                break;
            case 808003:
//                amount of group exceed limit 用户拥有的群组数量已达上限, 无法再创建
                message = "用户拥有的群组数量已达上限, 无法再创建";
                break;
            case 808004:
//                length of group name exceed limit 群组名长度超出上限，创建群组失败
                message = "群组名长度超出上限，创建群组失败";
                break;
            case 808005:
//                length of group desc exceed limit 群组描述长度超出上限，创建群组失败
                message = "群组描述长度超出上限，创建群组失败";
                break;
            case 808006:
//                max group member count error 创建群组时指定成员人数上限错误
                message = "创建群组时指定成员人数上限错误";
                break;
            case 810001:
//                target in group blacklist 目标用户在群组黑名单中，无法加入
                message = "目标用户在群组黑名单中，无法加入";
                break;
            case 810002:
//                add member list is null 添加的成员列表为空
                message = "添加的成员列表为空";
                break;
            case 810005:
//                have member not register 添加的成员列表中包含未注册成员
                message = "添加的成员列表中包含未注册成员";
                break;
            case 810007:
//                repeated added member 添加的成员列表中有成员重复添加
                message = "添加的成员列表中有成员重复添加";
                break;
            case 810008:
//                amount of member exceed group limit 添加的成员数量超出群组拥有的最大成员数上限
                message = "添加的成员数量超出群组拥有的最大成员数上限";
                break;
            case 810009:
//                amount of group exceed member limit 添加的成员列表中有成员拥有的群组数量已达上限
                message = "添加的成员列表中有成员拥有的群组数量已达上限";
                break;
            case 811002:
//                del member list is null 删除的成员列表为空
                message = "删除的成员列表为空";
                break;
            case 811005:
//                have member not register 删除的成员列表中有成员未注册
                message = "删除的成员列表中有成员未注册";
                break;
            case 811006:
//                member of group not permitted deleted 删除的成员列表中有成员该用户没有权限进行删除
                message = "删除的成员列表中有成员该用户没有权限进行删除";
                break;
            case 811007:
//                repeated deleted member 删除的成员列表中有成员重复删除
                message = "删除的成员列表中有成员重复删除";
                break;
            case 811008:
//                have member not in group 删除的成员列表中有成员不在该群组中
                message = "删除的成员列表中有成员不在该群组中";
                break;
            case 812003:
//                length of group name exceed limit 群组名超出长度上限
                message = "群组名超出长度上限";
                break;
            case 812004:
//                length of group desc exceed limit 群组描述超出上限
                message = "群组描述超出上限";
                break;
            case 818001:
//                target list is null 用户添加黑名单时，成员列表为空，添加失败
                message = "用户添加黑名单时，成员列表为空，添加失败";
                break;
            case 818002:
//                member not exist 用户添加黑名单时，成员列表中有成员不存在，添加失败
                message = "用户添加黑名单时，成员列表中有成员不存在，添加失败";
                break;
            case 818003:
//                member not permitted added 用户添加黑名单时，成员列表中有成员不能被添加，添加失败
                message = "用户添加黑名单时，成员列表中有成员不能被添加，添加失败";
                break;
            case 818004:
//                repeated added member 重复添加
                message = "重复添加";
                break;
            case 818005:
//                exceed max blacklist count 超过黑名单最大限制
                message = "超过黑名单最大限制";
                break;
            case 818006:
//                target not allow to add blacklist 目标用户列表中存在不允许加入黑名单的用户
                message = "目标用户列表中存在不允许加入黑名单的用户";
                break;
            case 819001:
//                zero member 用户移除好友出黑名单时，成员列表为空，操作失败
                message = "用户移除好友出黑名单时，成员列表为空，操作失败";
                break;
            case 819002:
//                member not exist 用户删除黑名单时，成员列表中有成员不存在，删除失败
                message = "用户删除黑名单时，成员列表中有成员不存在，删除失败";
                break;
            case 819003:
//                repeated deleted member 重复删除
                message = "重复删除";
                break;
            case 819004:
//                target not in blacklist 待删除的黑名单用户不在黑名单中
                message = "待删除的黑名单用户不在黑名单中";
                break;
            case 831001:
//                member already set 用户添加成员消息免打扰时，该成员已处于免打扰状态
                message = "用户添加成员消息免打扰时，该成员已处于免打扰状态";
                break;
            case 832001:
//                member never set 用户删除成员消息免打扰时，该成员不处于免打扰状态
                message = "用户删除成员消息免打扰时，该成员不处于免打扰状态";
                break;
            case 833001:
//                group not exist 用户添加群组消息免打扰时，该群组不存在
                message = "用户添加群组消息免打扰时，该群组不存在";
                break;
            case 833002:
//                user not in group 用户添加群组消息免打扰时，用户不存在该群组中
                message = "用户添加群组消息免打扰时，用户不存在该群组中";
                break;
            case 833003:
//                group already set 用户添加群组消息免打扰时，该群组已处于免打扰状态
                message = "用户添加群组消息免打扰时，该群组已处于免打扰状态";
                break;
            case 834001:
//                group never set 用户删除群组消息免打扰时，该群组不处于免打扰状态
                message = "用户删除群组消息免打扰时，该群组不处于免打扰状态";
                break;
            case 835001:
//                already set 用户添加全局消息免打扰时，该用户已处于全局免打扰状态
                message = "用户添加全局消息免打扰时，该用户已处于全局免打扰状态";
                break;
            case 836001:
//                never set 用户删除全局消息免打扰时，该用户不处于全局免打扰状态
                message = "用户删除全局消息免打扰时，该用户不处于全局免打扰状态";
                break;
            case 842001:
//                group not exist 用户添加群组消息屏蔽时，该群组不存在
                message = "用户添加群组消息屏蔽时，该群组不存在";
                break;
            case 842002:
//                user not in group 用户添加群组消息屏蔽时，用户不在该群组中
                message = "用户添加群组消息屏蔽时，用户不在该群组中";
                break;
            case 842003:
//                group already set 用户添加群组消息屏蔽时，该群组已处于消息屏蔽状态
                message = "用户添加群组消息屏蔽时，该群组已处于消息屏蔽状态";
                break;
            case 843001:
//                group never set 用户删除群组消息屏蔽时，该群组不处于消息屏蔽状态
                message = "用户删除群组消息屏蔽时，该群组不处于消息屏蔽状态";
                break;
            case 847001:
//                user not in chatroom 发送聊天室消息失败，发起者不在该聊天室中
                message = "发送聊天室消息失败，发起者不在该聊天室中";
                break;
            case 847002:
//                user baned to post 发送聊天室消息失败，发起者在该聊天室中被禁言
                message = "发送聊天室消息失败，发起者在该聊天室中被禁言";
                break;
            case 847003:
//                chatroom not exist 发送聊天室消息失败，该聊天室不存在
                message = "发送聊天室消息失败，该聊天室不存在";
                break;
            case 847004:
//                length of chatroom message exceed limit 发送聊天室消息失败，消息长度超过限制
                message = "发送聊天室消息失败，消息长度超过限制";
                break;
            case 847005:
//                chatroom msg content json error 发送聊天室消息失败，消息内容格式错误
                message = "发送聊天室消息失败，消息内容格式错误";
                break;
            case 850001:
//                chatroom not exist 删除不存在的聊天室
                message = "删除不存在的聊天室";
                break;
            case 851001:
//                repeated invit chatroom member 邀请成员到聊天室时，邀请的成员列表中有重复的成员，邀请失败
                message = "邀请成员到聊天室时，邀请的成员列表中有重复的成员，邀请失败";
                break;
            case 851002:
//                invit member not exist 邀请成员到聊天室时，邀请的成员列表中有未注册成员，邀请失败
                message = "邀请成员到聊天室时，邀请的成员列表中有未注册成员，邀请失败";
                break;
            case 851003:
//                member has in the chatroom 邀请或加入到聊天室时，邀请或加入的成员已在聊天室中，邀请或加入失败
                message = "邀请或加入到聊天室时，邀请或加入的成员已在聊天室中，邀请或加入失败";
                break;
            case 851004:
//                chatroom not exist 邀请或加入不存在的聊天室
                message = "邀请或加入不存在的聊天室";
                break;
            case 851005:
//                zero member 邀请成员到聊天室时，邀请的成员列表为空，邀请成员失败
                message = "邀请成员到聊天室时，邀请的成员列表为空，邀请成员失败";
                break;
            case 851006:
//                amount of member exceed chatroom limit 邀请或加入聊天时，邀请的人员数量超过聊天室剩余加入的人员数量
                message = "邀请或加入聊天时，邀请的人员数量超过聊天室剩余加入的人员数量";
                break;
            case 851007:
//                members have been blacklisted 邀请或加入聊天室时，邀请或加入的人员已被列入了黑名单
                message = "邀请或加入聊天室时，邀请或加入的人员已被列入了黑名单";
                break;
            case 852001:
//                user not in chatroom 踢出或退出聊天室时，该用户其实并不在该聊天室中，踢出或退出聊天室失败
                message = "踢出或退出聊天室时，该用户其实并不在该聊天室中，踢出或退出聊天室失败";
                break;
            case 852002:
//                chatroom not exist 踢出或退出不存在的聊天室
                message = "踢出或退出不存在的聊天室";
                break;
            case 852003:
//                zero member 踢出成员到聊天室时，踢出的成员列表为空，踢出成员失败
                message = "踢出成员到聊天室时，踢出的成员列表为空，踢出成员失败";
                break;
            case 852004:
//                owner can not leave chatroom 踢出或退出聊天室时，存在owner用户退出聊天室
                message = "踢出或退出聊天室时，存在owner用户退出聊天室";
                break;
            case 853001:
//                chatroom not exist 更新不存在的聊天室信息
                message = "更新不存在的聊天室信息";
                break;
            case 853002:
//                owner not in chatroom 更新聊天室owner时，新的owner并不在该聊天室中
                message = "更新聊天室owner时，新的owner并不在该聊天室中";
                break;
            case 855001:
//                out of time 消息撤回失败，超出撤回时间
                message = "消息撤回失败，超出撤回时间";
                break;
            case 855002:
//                request user is not message sender 消息撤回失败，请求撤回方不是消息发送方
                message = "消息撤回失败，请求撤回方不是消息发送方";
                break;
            case 855003:
//                request message not exist 消息撤回失败，请求撤回消息不存在
                message = "消息撤回失败，请求撤回消息不存在";
                break;
            case 855004:
//                message already retract 消息撤回失败，该消息已经撤回
                message = "消息撤回失败，该消息已经撤回";
                break;
            case 856001:
//                this request already process 审批失效，该添加成员邀请已经被处理
                message = "审批失效，该添加成员邀请已经被处理";
                break;
            case 856002:
//                invalid request data 请求数据无效
                message = "请求数据无效";
                break;
            case 857001:
//                target group not exist 目标群组不存在
                message = "目标群组不存在";
                break;
            case 857002:
//                target not online 目标不在线
                message = "目标不在线";
                break;
            case 857003:
//                target user not exist 目标用户不存在
                message = "目标用户不存在";
                break;
            case 857004:
//                length of trans cmd exceed limit 透传消息长度超过限制
                message = "透传消息长度超过限制";
                break;
            case 857005:
//                user not in group 请求用户不在群组中
                message = "请求用户不在群组中";
                break;
            case 857006:
//                target can 't be self	目标不能为自己
                message = "目标不能为自己";
                break;
            case 859001:
//                user already in the group 用户已经在目标群组中
                message = "用户已经在目标群组中";
                break;
            case 859002:
//                group type not support 目标群组类型不支持申请入群
                message = "目标群组类型不支持申请入群";
                break;
            case 786001:
//                length of group announcement exceed limit 群公告长度超出上限
                message = "群公告长度超出上限";
                break;
            case 787001:
//                announcement not exist 待删除公告不存在
                message = "待删除公告不存在";
                break;
            case 765001:
//                target not in group 目标用户不在群组中
                message = "目标用户不在群组中";
                break;
            case 765002:
//                request user no permission 请求用户无操作权限
                message = "请求用户无操作权限";
                break;
            case 7130001:
//                request user no permission 用户没有权限设置管理员
                message = "用户没有权限设置管理员";
                break;
            case 7130002:
//                set member is admin 设置为管理员的成员已经是管理员
                message = "设置为管理员的成员已经是管理员";
                break;
            case 7130003:
//                set member is owner 设置为管理员的成员是个聊天室owner
                message = "设置为管理员的成员是个聊天室owner";
                break;
            case 7130004:
//                exceed admin max count 超过管理员最大数量
                message = "超过管理员最大数量";
                break;
            case 7130005:
//                chatroom not exist 聊天室不存在
                message = "聊天室不存在";
                break;
            case 7130006:
//                set member not exist 设置为管理员的成员不在聊天室中
                message = "设置为管理员的成员不在聊天室中";
                break;
            case 7130007:
//                set target to null 设置目标为空
                message = "设置目标为空";
                break;
            case 7131001:
//                request user no permission 用户没有权限删除管理员资格（只有owner才能删除管理员资格）
                message = "用户没有权限删除管理员资格";
                break;
            case 7131002:
//                set member is not an admin 对不是管理员的用户删除其管理员资格
                message = "对不是管理员的用户删除其管理员资格";
                break;
            case 7132001:
//                request user no permission 用户没有权限设置黑名单
                message = "用户没有权限设置黑名单";
                break;
            case 7132002:
//                owner can not be set to blacklist owner不能被设为黑名单
                message = "owner不能被设为黑名单";
                break;
            case 7132003:
//                admin can not be set to blacklist 管理员不能被设为黑名单
                message = "管理员不能被设为黑名单";
                break;
            case 7132004:
//                chatroom not exist 聊天室不存在
                message = "聊天室不存在";
                break;
            case 7132005:
//                exceed blacklist max count 超过黑名单最大数量
                message = "超过黑名单最大数量";
                break;
            case 7132006:
//                set member has in the blacklist 添加黑名单的成员已在黑名单中
                message = "添加黑名单的成员已在黑名单中";
                break;
            case 7132007:
//                set target to null 设置目标为空
                message = "设置目标为空";
                break;
            case 7133001:
//                delete target not in blacklist 删除黑名单的成员不在黑名单中
                message = "删除黑名单的成员不在黑名单中";
                break;
            case 7100001:
//                notify target is null or target logout 音视频信令通知目标用户为空或者目标已全部登出
                message = "音视频信令通知目标用户为空或者目标已全部登出";
                break;
            case 7100002:
//                invite targe logout 音视频被邀请的用户已登出
                message = "音视频被邀请的用户已登出";
                break;
            case 7100006:
//                not enough rtc trial time 音视频服务试用时长不够，停用
                message = "音视频服务试用时长不够，停用";
                break;
        }
        ToastUtil.INSTANCE.showToast(AppManager.getInstance(), message);
    }
}