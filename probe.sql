/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : probe

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2017-12-22 16:38:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(100) DEFAULT NULL COMMENT '头像地址',
  `sex` int(2) DEFAULT NULL COMMENT '性别，0男，1女',
  `platform` int(2) DEFAULT NULL COMMENT '平台，android、ios',
  `imei` varchar(60) DEFAULT NULL COMMENT '设备唯一编号',
  `reg_time` datetime DEFAULT NULL COMMENT '注册时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

INSERT INTO `user` (`user_name`, `password`, `mobile`, `avatar_url`, `sex`, `platform`, `imei`, `reg_time`, `last_login_time`) VALUES ('name', 'pwd', '13426331143', 'avatar_url', '1', '0', NULL, now(), now());
