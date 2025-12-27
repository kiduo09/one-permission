-- ============================================
-- one-permission 数据库初始化脚本
-- 包含所有表结构创建和测试数据初始化
-- ============================================

-- 创建应用表
CREATE TABLE IF NOT EXISTS `applications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) NOT NULL COMMENT '应用名称',
  `app_key` varchar(100) NOT NULL COMMENT '应用Key（唯一标识）',
  `client_id` varchar(50) NOT NULL COMMENT '客户端ID（用于对外接口认证）',
  `client_secret` varchar(100) NOT NULL COMMENT '客户端密钥（用于对外接口认证）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`),
  UNIQUE KEY `uk_client_id` (`client_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用表';

-- 初始化应用数据（显式列名，包含 clientId 和 clientSecret）
INSERT IGNORE INTO `applications`
(`id`, `name`, `app_key`, `client_id`, `client_secret`, `remark`, `status`, `create_time`, `update_time`) VALUES
('1', 'CRM系统', 'crm_20251217', 'app_crm20251217', 'crm20251217clientsecret1234567890123456', 'CRM系统营销系统', '正常', '2025-12-17 21:27:50', '2025-12-17 21:27:50');
INSERT IGNORE INTO `applications`
(`id`, `name`, `app_key`, `client_id`, `client_secret`, `remark`, `status`, `create_time`, `update_time`) VALUES
('2', 'ERP系统', 'erp_20251217', 'app_erp20251217', 'erp20251217clientsecret1234567890123456', 'ERP系统', '正常', '2025-12-17 21:28:43', '2025-12-17 21:28:43');
INSERT IGNORE INTO `applications`
(`id`, `name`, `app_key`, `client_id`, `client_secret`, `remark`, `status`, `create_time`, `update_time`) VALUES
('5', 'test', 'test', 'app_test20251219', 'test20251219clientsecret1234567890123456', '3335', '正常', '2025-12-19 15:40:52', '2025-12-19 15:40:52');

-- 创建部门表
CREATE TABLE IF NOT EXISTS `departments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) NOT NULL COMMENT '部门名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父部门ID（NULL表示顶级部门）',
  `ancestors` varchar(500) DEFAULT '' COMMENT '祖级列表（从顶级到当前部门的ID路径，逗号分隔）',
  `code` varchar(100) DEFAULT NULL COMMENT '部门编码',
  `level` int(11) NOT NULL DEFAULT '1' COMMENT '部门层级（1为顶级）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '显示排序',
  `leader_work_no` varchar(50) DEFAULT NULL COMMENT '部门负责人工号',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/禁用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_ancestors` (`ancestors`),
  KEY `idx_code` (`code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 初始化部门数据（显式列名，避免列顺序变更导致问题）
-- 顶级部门 ancestors 为空字符串
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('1', '研发中心', null, '', 'RD001', '1', '1', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('2', '产品中心', null, '', 'PD001', '1', '2', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('3', '运营中心', null, '', 'OP001', '1', '3', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('4', '市场中心', null, '', 'MK001', '1', '4', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('5', '财务中心', null, '', 'FN001', '1', '5', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('6', '人事中心', null, '', 'HR001', '1', '6', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');

-- 二级部门 ancestors 为顶级部门ID
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('7', '前端开发组', '1', '1', 'RD001-01', '2', '1', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('8', '后端开发组', '1', '1', 'RD001-02', '2', '2', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('9', '移动开发组', '1', '1', 'RD001-03', '2', '3', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('10', '算法组', '1', '1', 'RD001-04', '2', '4', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('11', '数据组', '1', '1', 'RD001-05', '2', '5', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('12', '测试组', '1', '1', 'RD001-06', '2', '6', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('13', '产品设计组', '2', '2', 'PD001-01', '2', '1', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('14', '产品运营组', '2', '2', 'PD001-02', '2', '2', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('15', '内容运营组', '3', '3', 'OP001-01', '2', '1', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');
INSERT IGNORE INTO `departments`
(`id`, `name`, `parent_id`, `ancestors`, `code`, `level`, `sort`, `leader_work_no`, `status`, `remark`, `create_time`, `update_time`) VALUES
('16', '活动运营组', '3', '3', 'OP001-02', '2', '2', null, '正常', null, '2025-12-17 21:43:37', '2025-12-17 21:43:37');

-- 创建登录用户表
CREATE TABLE IF NOT EXISTS `login_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `login_account` varchar(100) NOT NULL COMMENT '登录账户',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密存储）',
  `admin_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '管理员类型：1-普通管理员，2-系统管理员',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/禁用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_login_account` (`login_account`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_admin_type` (`admin_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录用户表（系统管理员）';

-- 初始化登录用户数据
INSERT IGNORE INTO `login_users` VALUES ('1', 'admin', '管理员', 'admin@company.com', '$2a$10$sUCCi1LHAbOlj55MzHLI/.ezmH6vIeiHleeOb1b.3zjwVGRFwWg52', '2', '正常', '超级管理员账号', '2025-12-17 20:56:19', '2025-12-20 10:18:48');
INSERT IGNORE INTO `login_users` VALUES ('2', 'zhangyu', '章鱼管理员', 'zhangyu@company.com', '$2a$10$sUCCi1LHAbOlj55MzHLI/.ezmH6vIeiHleeOb1b.3zjwVGRFwWg52', '1', '正常', '章鱼应用管理员', '2025-12-17 20:56:19', '2025-12-19 23:59:56');
INSERT IGNORE INTO `login_users` VALUES ('3', 'test1', '测试账号', 'test@company.com', '$2a$10$sUCCi1LHAbOlj55MzHLI/.ezmH6vIeiHleeOb1b.3zjwVGRFwWg52', '1', '禁用', '测试用账号', '2025-12-17 20:56:19', '2025-12-20 00:00:00');
INSERT IGNORE INTO `login_users` VALUES ('4', 'test001', 'test001', '1234@qq.com', '$2a$10$XH4OhHRCX.cN93JDTHBYVOnIStcys3TT5F1LWASCTOu0Urz3Jw022', '1', '正常', '', '2025-12-20 10:19:34', '2025-12-20 10:19:34');

-- 创建普通用户表
CREATE TABLE IF NOT EXISTS `normal_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_no` varchar(50) NOT NULL COMMENT '工号',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `department_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `ad_account` varchar(100) DEFAULT NULL COMMENT 'AD域账号',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `level` varchar(50) DEFAULT NULL COMMENT '职级',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_work_no` (`work_no`),
  KEY `idx_ad_account` (`ad_account`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='普通用户表（业务用户）';

-- 初始化普通用户数据（水浒传人物）
INSERT IGNORE INTO `normal_users` VALUES ('1', 'SH001', '宋江', '11', 'songjiang', '13800001001', 'songjiang@company.com', '及时雨', '首领', '正常', '2025-12-17 21:43:16', '2025-12-18 19:00:51');
INSERT IGNORE INTO `normal_users` VALUES ('2', 'SH002', '卢俊义', '11', 'lujunyi', '13800001002', 'lujunyi@company.com', '玉麒麟', '副首领', '正常', '2025-12-17 21:43:16', '2025-12-18 19:01:02');
INSERT IGNORE INTO `normal_users` VALUES ('3', 'SH003', '吴用', '12', 'wuyong', '13800001003', 'wuyong@company.com', '智多星', '军师', '正常', '2025-12-17 21:43:16', '2025-12-18 19:00:57');
INSERT IGNORE INTO `normal_users` VALUES ('4', 'SH004', '公孙胜', '12', 'gongsunsheng', '13800001004', 'gongsunsheng@company.com', '入云龙', '军师', '正常', '2025-12-17 21:43:16', '2025-12-18 19:01:06');
INSERT IGNORE INTO `normal_users` VALUES ('5', 'SH005', '关胜', '1', 'guansheng', '13800001005', 'guansheng@company.com', '大刀', '五虎将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('6', 'SH006', '林冲', '1', 'linchong', '13800001006', 'linchong@company.com', '豹子头', '五虎将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('7', 'SH007', '秦明', '1', 'qinming', '13800001007', 'qinming@company.com', '霹雳火', '五虎将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('8', 'SH008', '呼延灼', '1', 'huyanzhuo', '13800001008', 'huyanzhuo@company.com', '双鞭', '五虎将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('9', 'SH009', '花荣', '1', 'huarong', '13800001009', 'huarong@company.com', '小李广', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('10', 'SH010', '柴进', '1', 'chaijin', '13800001010', 'chaijin@company.com', '小旋风', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('11', 'SH011', '李应', '1', 'liying', '13800001011', 'liying@company.com', '扑天雕', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('12', 'SH012', '朱仝', '1', 'zhutong', '13800001012', 'zhutong@company.com', '美髯公', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('13', 'SH013', '鲁智深', '1', 'luzhishen', '13800001013', 'luzhishen@company.com', '花和尚', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('14', 'SH014', '武松', '1', 'wusong', '13800001014', 'wusong@company.com', '行者', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('15', 'SH015', '董平', '1', 'dongping', '13800001015', 'dongping@company.com', '双枪将', '五虎将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('16', 'SH016', '张清', '1', 'zhangqing', '13800001016', 'zhangqing@company.com', '没羽箭', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('17', 'SH017', '杨志', '1', 'yangzhi', '13800001017', 'yangzhi@company.com', '青面兽', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('18', 'SH018', '徐宁', '1', 'xuning', '13800001018', 'xuning@company.com', '金枪手', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('19', 'SH019', '索超', '1', 'suochao', '13800001019', 'suochao@company.com', '急先锋', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('20', 'SH020', '戴宗', '1', 'daizong', '13800001020', 'daizong@company.com', '神行太保', '总探声息头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('21', 'SH021', '刘唐', '1', 'liutang', '13800001021', 'liutang@company.com', '赤发鬼', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('22', 'SH022', '李逵', '1', 'likui', '13800001022', 'likui@company.com', '黑旋风', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('23', 'SH023', '史进', '1', 'shijin', '13800001023', 'shijin@company.com', '九纹龙', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('24', 'SH024', '穆弘', '1', 'muhong', '13800001024', 'muhong@company.com', '没遮拦', '八骠骑', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('25', 'SH025', '雷横', '1', 'leiheng', '13800001025', 'leiheng@company.com', '插翅虎', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('26', 'SH026', '李俊', '12', 'lijun', '13800001026', 'lijun@company.com', '混江龙', '水军头领', '正常', '2025-12-17 21:43:16', '2025-12-18 19:01:40');
INSERT IGNORE INTO `normal_users` VALUES ('27', 'SH027', '阮小二', '1', 'ruanxiaoer', '13800001027', 'ruanxiaoer@company.com', '立地太岁', '水军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('28', 'SH028', '张横', '12', 'zhangheng', '13800001028', 'zhangheng@company.com', '船火儿', '水军头领', '正常', '2025-12-17 21:43:16', '2025-12-18 19:01:42');
INSERT IGNORE INTO `normal_users` VALUES ('29', 'SH029', '阮小五', '1', 'ruanxiaowu', '13800001029', 'ruanxiaowu@company.com', '短命二郎', '水军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('30', 'SH030', '张顺', '1', 'zhangshun', '13800001030', 'zhangshun@company.com', '浪里白条', '水军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('31', 'SH031', '阮小七', '1', 'ruanxiaoqi', '13800001031', 'ruanxiaoqi@company.com', '活阎罗', '水军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('32', 'SH032', '杨雄', '1', 'yangxiong', '13800001032', 'yangxiong@company.com', '病关索', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('33', 'SH033', '石秀', '1', 'shixiu', '13800001033', 'shixiu@company.com', '拼命三郎', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('34', 'SH034', '解珍', '1', 'xiezhen', '13800001034', 'xiezhen@company.com', '两头蛇', '步军将校', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('35', 'SH035', '解宝', '1', 'xiebao', '13800001035', 'xiebao@company.com', '双尾蝎', '步军将校', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('36', 'SH036', '燕青', '1', 'yanqing', '13800001036', 'yanqing@company.com', '浪子', '步军头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('37', 'SH037', '朱武', '1', 'zhuwu', '13800001037', 'zhuwu@company.com', '神机军师', '同参赞军务头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('38', 'SH038', '黄信', '1', 'huangxin', '13800001038', 'huangxin@company.com', '镇三山', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('39', 'SH039', '孙立', '1', 'sunli', '13800001039', 'sunli@company.com', '病尉迟', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('40', 'SH040', '宣赞', '1', 'xuanzan', '13800001040', 'xuanzan@company.com', '丑郡马', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('41', 'SH041', '郝思文', '1', 'haosiwen', '13800001041', 'haosiwen@company.com', '井木犴', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('42', 'SH042', '韩滔', '12', 'hantao', '13800001042', 'hantao@company.com', '百胜将', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-18 19:01:55');
INSERT IGNORE INTO `normal_users` VALUES ('43', 'SH043', '彭玘', '11', 'pengqi', '13800001043', 'pengqi@company.com', '天目将', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-18 19:01:57');
INSERT IGNORE INTO `normal_users` VALUES ('44', 'SH044', '单廷珪', '1', 'shantinggui', '13800001044', 'shantinggui@company.com', '圣水将', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('45', 'SH045', '魏定国', '11', 'weidingguo', '13800001045', 'weidingguo@company.com', '神火将', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-18 19:02:01');
INSERT IGNORE INTO `normal_users` VALUES ('46', 'SH046', '萧让', '1', 'xiaorang', '13800001046', 'xiaorang@company.com', '圣手书生', '文职头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('47', 'SH047', '裴宣', '1', 'peixuan', '13800001047', 'peixuan@company.com', '铁面孔目', '文职头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('48', 'SH048', '欧鹏', '1', 'oupeng', '13800001048', 'oupeng@company.com', '摩云金翅', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('49', 'SH049', '邓飞', '1', 'dengfei', '13800001049', 'dengfei@company.com', '火眼狻猊', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('50', 'SH050', '燕顺', '1', 'yanshun', '13800001050', 'yanshun@company.com', '锦毛虎', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('51', 'SH051', '杨林', '1', 'yanglin', '13800001051', 'yanglin@company.com', '锦豹子', '小彪将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('52', 'SH052', '凌振', '1', 'lingzhen', '13800001052', 'lingzhen@company.com', '轰天雷', '专造大小号炮头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('53', 'SH053', '蒋敬', '1', 'jiangjing', '13800001053', 'jiangjing@company.com', '神算子', '文职头领', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('54', 'SH054', '吕方', '1', 'lvfang', '13800001054', 'lvfang@company.com', '小温侯', '守护中军步军骁将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');
INSERT IGNORE INTO `normal_users` VALUES ('55', 'SH055', '郭盛', '1', 'guosheng', '13800001055', 'guosheng@company.com', '赛仁贵', '守护中军步军骁将', '正常', '2025-12-17 21:43:16', '2025-12-17 21:43:16');

-- 创建系统菜单表
CREATE TABLE IF NOT EXISTS `system_menus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) NOT NULL COMMENT '菜单名称',
  `menu_id` varchar(100) NOT NULL COMMENT '菜单ID（唯一标识）',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID（NULL表示顶级菜单）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `route` varchar(500) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(500) DEFAULT NULL COMMENT '组件路径',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/停用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_id` (`menu_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- 创建系统角色表
CREATE TABLE IF NOT EXISTS `system_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) NOT NULL COMMENT '角色名称',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 创建消费者信息表
CREATE TABLE IF NOT EXISTS `consumer_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `consumer_name` varchar(50) NOT NULL COMMENT '消费者名称',
  `client_id` varchar(50) NOT NULL COMMENT 'clientId',
  `client_secret` varchar(100) NOT NULL COMMENT 'clientSecret',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_client_id` (`client_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费者信息表';

-- 初始化消费者信息数据
INSERT IGNORE INTO `consumer_info` VALUES ('1', 'SSO系统', 'zy_cdar6jxklk1iq', '930a082be1424809b127ff1deea012bc', '1', '2025-12-21 14:50:44', '2025-12-21 14:50:44', '0');

-- 创建应用菜单表
CREATE TABLE IF NOT EXISTS `app_menus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_id` bigint(20) NOT NULL COMMENT '应用ID',
  `name` varchar(200) NOT NULL COMMENT '菜单名称',
  `menu_key` varchar(100) NOT NULL COMMENT '菜单ID（唯一标识）',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID（NULL表示顶级菜单）',
  `menu_type` varchar(20) NOT NULL DEFAULT '目录' COMMENT '菜单类型：目录/菜单/按钮',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `icon_active` varchar(100) DEFAULT NULL COMMENT '菜单图标（激活状态）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '显示排序',
  `is_external` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否外链：0-否，1-是',
  `route` varchar(500) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(500) DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(200) DEFAULT NULL COMMENT '权限标识（按钮类型使用）',
  `display_status` varchar(20) NOT NULL DEFAULT '显示' COMMENT '显示状态：显示/隐藏',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '菜单状态：正常/停用',
  `embedded_url` varchar(1000) DEFAULT NULL COMMENT '内嵌url',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_menu_id` (`app_id`,`menu_key`),
  KEY `idx_app_id` (`app_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_menu_type` (`menu_type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用菜单表';

-- 初始化应用菜单数据（CRM系统）
INSERT IGNORE INTO `app_menus` VALUES ('1', '1', '客户管理3', 'crm_customer', null, '菜单', 'customer-icon', 'customer-icon-active', '1', '0', '/crm/customer', 'views/CrmCustomer', '', '显示', '正常', '', '客户管理页面', '2025-12-17 22:56:24', '2025-12-17 22:56:24');
INSERT IGNORE INTO `app_menus` VALUES ('2', '1', '销售管理', 'crm_sales', null, '目录', 'sales-icon', 'sales-icon-active', '2', '0', null, null, null, '显示', '正常', null, '销售管理目录', '2025-12-17 22:56:24', '2025-12-17 22:56:24');
INSERT IGNORE INTO `app_menus` VALUES ('3', '1', '营销管理', 'crm_marketing', null, '目录', 'marketing-icon', 'marketing-icon-active', '3', '0', null, null, null, '显示', '正常', null, '营销管理目录', '2025-12-17 22:56:24', '2025-12-17 22:56:24');
INSERT IGNORE INTO `app_menus` VALUES ('4', '1', '销售机会', 'crm_opportunity', '2', '菜单', 'opportunity-icon', 'opportunity-icon-active', '1', '0', '/crm/sales/opportunity', 'views/SalesOpportunity', null, '显示', '正常', null, '销售机会页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('5', '1', '销售订单', 'crm_order', '2', '菜单', 'order-icon', 'order-icon-active', '2', '0', '/crm/sales/order', 'views/SalesOrder', null, '显示', '正常', null, '销售订单页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('6', '1', '销售报表', 'crm_sales_report', '2', '菜单', 'report-icon', 'report-icon-active', '3', '0', '/crm/sales/report', 'views/SalesReport', null, '显示', '正常', null, '销售报表页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('7', '1', '营销活动', 'crm_campaign', '3', '菜单', 'campaign-icon', 'campaign-icon-active', '1', '0', '/crm/marketing/campaign', 'views/MarketingCampaign', null, '显示', '正常', null, '营销活动页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('8', '1', '线索管理', 'crm_leads', '3', '菜单', 'leads-icon', 'leads-icon-active', '2', '0', '/crm/marketing/leads', 'views/LeadsManagement', null, '显示', '正常', null, '线索管理页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('9', '1', '客户分析', 'crm_analysis', '3', '菜单', 'analysis-icon', 'analysis-icon-active', '3', '0', '/crm/marketing/analysis', 'views/CustomerAnalysis', null, '显示', '正常', null, '客户分析页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('25', '1', '111', '222', '6', '按钮', '', null, '0', '0', '', '', '', '显示', '正常', '', '', '2025-12-20 23:17:11', '2025-12-20 23:17:11');

-- 初始化应用菜单数据（ERP系统）
INSERT IGNORE INTO `app_menus` VALUES ('10', '2', '库存管理', 'erp_inventory', null, '菜单', 'inventory-icon', 'inventory-icon-active', '1', '0', '/erp/inventory', 'views/ErpInventory', null, '显示', '正常', null, '库存管理页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('11', '2', '财务管理', 'erp_finance', null, '目录', 'finance-icon', 'finance-icon-active', '2', '0', null, null, null, '显示', '正常', null, '财务管理目录', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('12', '2', '采购管理', 'erp_purchase', null, '目录', 'purchase-icon', 'purchase-icon-active', '3', '0', null, null, null, '显示', '正常', null, '采购管理目录', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('13', '2', '生产管理', 'erp_production', null, '目录', 'production-icon', 'production-icon-active', '4', '0', null, null, null, '显示', '正常', null, '生产管理目录', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('14', '2', '会计科目', 'erp_account', '11', '菜单', 'account-icon', 'account-icon-active', '1', '0', '/erp/finance/account', 'views/FinanceAccount', null, '显示', '正常', null, '会计科目页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('15', '2', '财务报表', 'erp_report', '11', '菜单', 'report-icon', 'report-icon-active', '2', '0', '/erp/finance/report', 'views/FinanceReport', null, '显示', '正常', null, '财务报表页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('16', '2', '资金管理', 'erp_cash', '11', '菜单', 'cash-icon', 'cash-icon-active', '3', '0', '/erp/finance/cash', 'views/FinanceCash', null, '显示', '正常', null, '资金管理页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('17', '2', '成本核算', 'erp_cost', '11', '菜单', 'cost-icon', 'cost-icon-active', '4', '0', '/erp/finance/cost', 'views/CostAccounting', null, '显示', '正常', null, '成本核算页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('18', '2', '采购订单', 'erp_purchase_order', '12', '菜单', 'purchase-order-icon', 'purchase-order-icon-active', '1', '0', '/erp/purchase/order', 'views/PurchaseOrder', null, '显示', '正常', null, '采购订单页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('19', '2', '供应商管理', 'erp_supplier', '12', '菜单', 'supplier-icon', 'supplier-icon-active', '2', '0', '/erp/purchase/supplier', 'views/Supplier', null, '显示', '正常', null, '供应商管理页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('20', '2', '采购入库', 'erp_purchase_in', '12', '菜单', 'purchase-in-icon', 'purchase-in-icon-active', '3', '0', '/erp/purchase/in', 'views/PurchaseIn', null, '显示', '正常', null, '采购入库页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('21', '2', '生产计划', 'erp_production_plan', '13', '菜单', 'production-plan-icon', 'production-plan-icon-active', '1', '0', '/erp/production/plan', 'views/ProductionPlan', null, '显示', '正常', null, '生产计划页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('22', '2', '生产订单', 'erp_production_order', '13', '菜单', 'production-order-icon', 'production-order-icon-active', '2', '0', '/erp/production/order', 'views/ProductionOrder', null, '显示', '正常', null, '生产订单页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('23', '2', '工艺管理', 'erp_process', '13', '菜单', 'process-icon', 'process-icon-active', '3', '0', '/erp/production/process', 'views/ProcessManagement', null, '显示', '正常', null, '工艺管理页面', '2025-12-17 22:56:25', '2025-12-17 22:56:25');
INSERT IGNORE INTO `app_menus` VALUES ('24', '2', '库存列表', '库存列表', '10', '目录', '', null, '1', '0', '库存列表', '', '', '显示', '正常', '', '', '2025-12-20 10:17:28', '2025-12-20 10:17:28');
INSERT IGNORE INTO `app_menus` VALUES ('26', '2', '3容忍', '热热', '24', '目录', '', null, '0', '0', 'eeee', '', '', '显示', '正常', '', '', '2025-12-20 23:18:27', '2025-12-20 23:18:27');
INSERT IGNORE INTO `app_menus` VALUES ('27', '2', '3-2', '3-2', '15', '目录', '', null, '0', '0', '3-2', '', '', '显示', '正常', '', '', '2025-12-20 23:19:54', '2025-12-20 23:19:54');
INSERT IGNORE INTO `app_menus` VALUES ('28', '2', '3-3', '3-3', '14', '目录', '3-3', null, '0', '0', '3-3', '', '', '显示', '正常', '', '', '2025-12-20 23:20:31', '2025-12-20 23:20:31');

-- 创建应用角色表
CREATE TABLE IF NOT EXISTS `app_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_id` bigint(20) NOT NULL COMMENT '应用ID',
  `system_id` varchar(100) NOT NULL COMMENT '系统ID',
  `name` varchar(200) NOT NULL COMMENT '角色名称',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `status` varchar(20) NOT NULL DEFAULT '正常' COMMENT '状态：正常/禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_app_id` (`app_id`),
  KEY `idx_system_id` (`system_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用角色表';

-- 初始化应用角色数据
INSERT IGNORE INTO `app_roles` VALUES ('1', '1', '1', 'Hr角色', '1', '正常', '2025-12-18 11:18:25', '2025-12-18 11:18:25');
INSERT IGNORE INTO `app_roles` VALUES ('2', '2', '2', '3-3', '0', '正常', '2025-12-20 23:21:09', '2025-12-20 23:21:09');

-- 创建应用角色菜单权限表
CREATE TABLE IF NOT EXISTS `app_role_menus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '应用角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '应用菜单ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用角色菜单权限表';

-- 初始化应用角色菜单权限数据
INSERT IGNORE INTO `app_role_menus` VALUES ('24', '2', '12', '2025-12-21 00:07:15', '2025-12-21 00:07:15');
INSERT IGNORE INTO `app_role_menus` VALUES ('25', '2', '10', '2025-12-21 00:07:15', '2025-12-21 00:07:15');
INSERT IGNORE INTO `app_role_menus` VALUES ('26', '2', '20', '2025-12-21 00:07:15', '2025-12-21 00:07:15');
INSERT IGNORE INTO `app_role_menus` VALUES ('27', '2', '24', '2025-12-21 00:07:15', '2025-12-21 00:07:15');
INSERT IGNORE INTO `app_role_menus` VALUES ('28', '2', '26', '2025-12-21 00:07:15', '2025-12-21 00:07:15');
INSERT IGNORE INTO `app_role_menus` VALUES ('29', '2', '27', '2025-12-21 00:07:15', '2025-12-21 00:07:15');

-- 创建应用角色用户关联表
CREATE TABLE IF NOT EXISTS `app_role_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '应用角色ID',
  `user_work_no` varchar(50) NOT NULL COMMENT '用户工号',
  `assign_type` varchar(20) NOT NULL DEFAULT '个人' COMMENT '分配类型：个人/部门',
  `department` varchar(200) DEFAULT NULL COMMENT '部门名称（当分配类型为部门时使用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_user` (`role_id`,`user_work_no`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_user_work_no` (`user_work_no`),
  KEY `idx_assign_type` (`assign_type`),
  KEY `idx_department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用角色用户关联表';

-- 初始化应用角色用户关联数据
INSERT IGNORE INTO `app_role_users` VALUES ('7', '1', 'SH001', '个人', null, '2025-12-18 19:19:55', '2025-12-18 19:19:55');
INSERT IGNORE INTO `app_role_users` VALUES ('8', '1', 'SH004', '个人', null, '2025-12-19 09:26:22', '2025-12-19 09:26:22');
INSERT IGNORE INTO `app_role_users` VALUES ('9', '1', 'SH042', '个人', null, '2025-12-19 18:02:18', '2025-12-19 18:02:18');

-- 创建应用角色部门关联表
CREATE TABLE IF NOT EXISTS `app_role_departments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '应用角色ID',
  `department_id` bigint(20) NOT NULL COMMENT '部门ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_department` (`role_id`,`department_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用角色部门关联表';

-- 初始化应用角色部门关联数据
INSERT IGNORE INTO `app_role_departments` VALUES ('11', '1', '7', '2025-12-19 11:37:44', '2025-12-19 11:37:44');
INSERT IGNORE INTO `app_role_departments` VALUES ('12', '1', '8', '2025-12-19 11:37:44', '2025-12-19 11:37:44');
INSERT IGNORE INTO `app_role_departments` VALUES ('13', '1', '9', '2025-12-19 11:37:44', '2025-12-19 11:37:44');
INSERT IGNORE INTO `app_role_departments` VALUES ('14', '1', '10', '2025-12-19 11:37:44', '2025-12-19 11:37:44');
INSERT IGNORE INTO `app_role_departments` VALUES ('15', '1', '11', '2025-12-19 11:37:44', '2025-12-19 11:37:44');
INSERT IGNORE INTO `app_role_departments` VALUES ('16', '1', '12', '2025-12-19 11:37:44', '2025-12-19 11:37:44');
INSERT IGNORE INTO `app_role_departments` VALUES ('17', '1', '13', '2025-12-19 11:37:44', '2025-12-19 11:37:44');
INSERT IGNORE INTO `app_role_departments` VALUES ('19', '1', '15', '2025-12-19 11:37:44', '2025-12-19 11:37:44');

-- 创建登录用户应用授权表
CREATE TABLE IF NOT EXISTS `login_user_apps` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `login_user_id` bigint(20) NOT NULL COMMENT '登录用户ID',
  `app_id` bigint(20) NOT NULL COMMENT '应用ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_app` (`login_user_id`,`app_id`),
  KEY `idx_login_user_id` (`login_user_id`),
  KEY `idx_app_id` (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录用户应用授权表';

-- 初始化登录用户应用授权数据
INSERT IGNORE INTO `login_user_apps` VALUES ('6', '1', '2', '2025-12-17 22:21:02', '2025-12-17 22:21:02');
INSERT IGNORE INTO `login_user_apps` VALUES ('7', '1', '1', '2025-12-17 22:21:02', '2025-12-17 22:21:02');
INSERT IGNORE INTO `login_user_apps` VALUES ('8', '2', '1', '2025-12-19 14:22:08', '2025-12-19 14:22:08');
INSERT IGNORE INTO `login_user_apps` VALUES ('10', '3', '5', '2025-12-19 15:40:57', '2025-12-19 15:40:57');
INSERT IGNORE INTO `login_user_apps` VALUES ('11', '4', '5', '2025-12-20 10:28:20', '2025-12-20 10:28:20');

-- 创建用户部门关联表
CREATE TABLE IF NOT EXISTS `user_departments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `department_id` bigint(20) NOT NULL COMMENT '部门ID',
  `is_primary` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否主部门：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dept` (`user_id`,`department_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_is_primary` (`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户部门关联表';
