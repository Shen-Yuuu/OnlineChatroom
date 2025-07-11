create database if not exists java_chatroom charset utf8;

use java_chatroom;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `userId` int(11) NOT NULL AUTO_INCREMENT,
                         `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                         `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                         `gender`int,
                         `description`text,
                         PRIMARY KEY (`userId`) USING BTREE,
                         UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

insert into user values(1, '张三', '123', 1, '我是张三');
insert into user values(2, '李四', '123', 1, '我是李四');
insert into user values(3, '王五', '123', 0, '我是王五');
insert into user values(4, '赵六', '123', 0, '我是赵六');
insert into user values(5, '124321', '23232', 1, 'usdgs');

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `userId` int(11) NOT NULL,
                           `friendId` int(11) NOT NULL,
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message_session
-- ----------------------------
DROP TABLE IF EXISTS `message_session`;
CREATE TABLE `message_session`  (
                                    `sessionId` int(11) NOT NULL AUTO_INCREMENT,
                                    `lastMessage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                    `createTime` datetime(0) NULL,
                                    PRIMARY KEY (`sessionId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message_session_user
-- ----------------------------
DROP TABLE IF EXISTS `message_session_user`;
CREATE TABLE `message_session_user`  (
                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                         `sessionId` int(11) NOT NULL,
                                         `userId` int(11) NOT NULL,
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for moment
-- ----------------------------
DROP TABLE IF EXISTS `moment`;
CREATE TABLE `moment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of moment
-- ----------------------------

-- ----------------------------
-- Table structure for moment_comment
-- ----------------------------
DROP TABLE IF EXISTS `moment_comment`;
CREATE TABLE `moment_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `moment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of moment_comment
-- ----------------------------

-- ----------------------------
-- Table structure for moment_like
-- ----------------------------
DROP TABLE IF EXISTS `moment_like`;
CREATE TABLE `moment_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `moment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of moment_like
-- ----------------------------

-- ----------------------------
-- Records of moment
-- ----------------------------

-- ----------------------------

-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
                          `groupId` int NOT NULL AUTO_INCREMENT,
                          `groupName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未命名群',
                          `ownerId` int NULL DEFAULT NULL,
                          `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                          `groupStatus` int NULL DEFAULT NULL,
                          PRIMARY KEY (`groupId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupmessagerecord
-- ----------------------------
DROP TABLE IF EXISTS `groupmessagerecord`;
CREATE TABLE `groupmessagerecord`  (
                                       `recordId` int NOT NULL AUTO_INCREMENT COMMENT '群聊记录ID，主键',
                                       `groupId` int NOT NULL COMMENT '群号',
                                       `userId` int NOT NULL COMMENT '发消息的用户ID',
                                       `messageContent` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '消息内容',
                                       `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间',
                                       PRIMARY KEY (`recordId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '群聊记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupusers
-- ----------------------------
DROP TABLE IF EXISTS `groupusers`;
CREATE TABLE `groupusers`  (
                               `groupUserId` bigint NOT NULL AUTO_INCREMENT COMMENT '主键索引',
                               `groupId` int NOT NULL COMMENT '群号',
                               `userId` int NOT NULL COMMENT '用户号',
                               PRIMARY KEY (`groupUserId`) USING BTREE,
                               UNIQUE INDEX `uk_group_user`(`groupId` ASC, `userId` ASC) USING BTREE COMMENT '确保群和用户的唯一性'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '群与用户关联表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Records of user

-- ----------------------------
DROP TABLE IF EXISTS `moment_comment`;
CREATE TABLE `moment_comment`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `moment_id` bigint NOT NULL,
                                   `user_id` bigint NOT NULL,
                                   `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
                                   `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of moment_comment
-- ----------------------------

-- ----------------------------
-- Table structure for moment_like
-- ----------------------------
DROP TABLE IF EXISTS `moment_like`;
CREATE TABLE `moment_like`  (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `moment_id` bigint NOT NULL,
                                `user_id` bigint NOT NULL,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of moment_like
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
                           `messageId` bigint NOT NULL AUTO_INCREMENT,
                           `sessionId` int NOT NULL,
                           `fromId` int NOT NULL,
                           `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                           `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`messageId`),
                           KEY `idx_session_time` (`sessionId`,`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



ALTER TABLE message ADD INDEX idx_sessionId (sessionId);
ALTER TABLE message ADD INDEX idx_fromId (fromId);
ALTER TABLE message ADD INDEX idx_createTime (createTime);
ALTER TABLE message_session_user ADD INDEX idx_userId (userId);
ALTER TABLE message_session_user ADD INDEX idx_sessionId (sessionId)

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
                          `groupId` int NOT NULL AUTO_INCREMENT,
                          `groupName` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '未命名群',
                          `ownerId` int NULL DEFAULT NULL,
                          `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                          `groupStatus` int NULL DEFAULT NULL,
                          PRIMARY KEY (`groupId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES (1, '测试群', 1, '2025-07-11 00:07:26', NULL);
INSERT INTO `group` VALUES (2, '测试群2', 2, '2025-07-11 00:08:30', NULL);
INSERT INTO `group` VALUES (3, '按不出', 1, '2025-07-11 01:49:08', NULL);
INSERT INTO `group` VALUES (4, '李四和赵六', 2, '2025-07-11 08:45:50', NULL);

-- ----------------------------
-- Table structure for groupmessagerecord
-- ----------------------------
DROP TABLE IF EXISTS `groupmessagerecord`;
CREATE TABLE `groupmessagerecord`  (
                                       `recordId` int NOT NULL AUTO_INCREMENT COMMENT '群聊记录ID，主键',
                                       `groupId` int NOT NULL COMMENT '群号',
                                       `userId` int NOT NULL COMMENT '发消息的用户ID',
                                       `messageContent` varchar(2048) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '消息内容',
                                       `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间',
                                       PRIMARY KEY (`recordId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '群聊记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of groupmessagerecord
-- ----------------------------
INSERT INTO `groupmessagerecord` VALUES (1, 1, 1, '大家好！这是第一条群消息。', '2025-07-11 00:14:46');
INSERT INTO `groupmessagerecord` VALUES (2, 1, 2, '收到，张三！', '2025-07-11 00:14:46');
INSERT INTO `groupmessagerecord` VALUES (3, 1, 1, '123', '2025-07-11 01:48:45');
INSERT INTO `groupmessagerecord` VALUES (4, 4, 2, '哈哈哈赵六你也有今天', '2025-07-11 08:46:24');
INSERT INTO `groupmessagerecord` VALUES (5, 4, 4, '不是吧李四！！！\n', '2025-07-11 08:47:50');
INSERT INTO `groupmessagerecord` VALUES (6, 1, 3, '我是王五', '2025-07-11 08:53:52');
INSERT INTO `groupmessagerecord` VALUES (7, 1, 1, '我来试试顺序', '2025-07-11 09:14:19');
INSERT INTO `groupmessagerecord` VALUES (8, 1, 5, '我是高志豪', '2025-07-11 09:28:21');
INSERT INTO `groupmessagerecord` VALUES (9, 1, 2, '我是里斯\n', '2025-07-11 09:37:26');
INSERT INTO `groupmessagerecord` VALUES (10, 1, 2, '上下滚轮', '2025-07-11 09:37:40');

-- ----------------------------
-- Table structure for groupusers
-- ----------------------------
DROP TABLE IF EXISTS `groupusers`;
CREATE TABLE `groupusers`  (
                               `groupUserId` bigint NOT NULL AUTO_INCREMENT COMMENT '主键索引',
                               `groupId` int NOT NULL COMMENT '群号',
                               `userId` int NOT NULL COMMENT '用户号',
                               PRIMARY KEY (`groupUserId`) USING BTREE,
                               UNIQUE INDEX `uk_group_user`(`groupId` ASC, `userId` ASC) USING BTREE COMMENT '确保群和用户的唯一性'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '群与用户关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of groupusers
-- ----------------------------
INSERT INTO `groupusers` VALUES (1, 1, 1);
INSERT INTO `groupusers` VALUES (2, 1, 2);
INSERT INTO `groupusers` VALUES (3, 1, 3);
INSERT INTO `groupusers` VALUES (8, 1, 5);
INSERT INTO `groupusers` VALUES (4, 3, 1);
INSERT INTO `groupusers` VALUES (5, 3, 2);
INSERT INTO `groupusers` VALUES (6, 4, 2);
INSERT INTO `groupusers` VALUES (7, 4, 4);

ALTER TABLE message_session_user ADD INDEX idx_sessionId (sessionId)

