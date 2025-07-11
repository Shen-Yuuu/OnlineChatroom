/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : java_chatroom

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 10:57:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend`  (
                           `userId` int NULL DEFAULT NULL,
                           `friendId` int NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` VALUES (1, 2);
INSERT INTO `friend` VALUES (2, 1);
INSERT INTO `friend` VALUES (1, 3);
INSERT INTO `friend` VALUES (3, 1);
INSERT INTO `friend` VALUES (1, 4);
INSERT INTO `friend` VALUES (4, 1);
INSERT INTO `friend` VALUES (1, 2);
INSERT INTO `friend` VALUES (2, 1);
INSERT INTO `friend` VALUES (1, 3);
INSERT INTO `friend` VALUES (3, 1);
INSERT INTO `friend` VALUES (1, 4);
INSERT INTO `friend` VALUES (4, 1);
INSERT INTO `friend` VALUES (1, 5);
INSERT INTO `friend` VALUES (5, 1);

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

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
                            `messageId` bigint NOT NULL AUTO_INCREMENT,
                            `sessionId` int NOT NULL,
                            `fromId` int NOT NULL,
                            `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
                            `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`messageId`) USING BTREE,
                            INDEX `idx_session_time`(`sessionId` ASC, `createTime` ASC) USING BTREE,
                            INDEX `idx_sessionId`(`sessionId` ASC) USING BTREE,
                            INDEX `idx_fromId`(`fromId` ASC) USING BTREE,
                            INDEX `idx_createTime`(`createTime` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 1, 1, '今晚吃啥', '2026-05-01 17:00:00');
INSERT INTO `message` VALUES (2, 2, 1, '随便', '2026-05-01 17:01:00');
INSERT INTO `message` VALUES (3, 1, 1, '那吃面?', '2026-05-01 17:02:00');
INSERT INTO `message` VALUES (4, 2, 1, '不想吃', '2026-05-01 17:03:00');
INSERT INTO `message` VALUES (5, 1, 1, '那你想吃啥', '2026-05-01 17:04:00');
INSERT INTO `message` VALUES (6, 2, 1, '随便', '2026-05-01 17:05:00');
INSERT INTO `message` VALUES (7, 1, 2, '晚上一起约?', '2026-05-02 12:00:00');
INSERT INTO `message` VALUES (8, 2, 1, '不想吃', '2026-05-01 17:07:00');
INSERT INTO `message` VALUES (9, 1, 1, '那你想吃啥?', '2026-05-01 17:08:00');
INSERT INTO `message` VALUES (10, 2, 1, '随便', '2026-05-01 17:09:00');
INSERT INTO `message` VALUES (11, 1, 1, '那吃米饭炒菜?', '2026-05-01 17:06:00');
INSERT INTO `message` VALUES (12, 2, 1, 'gzhsb\n', '2025-07-10 23:11:22');

-- ----------------------------
-- Table structure for message_session
-- ----------------------------
DROP TABLE IF EXISTS `message_session`;
CREATE TABLE `message_session`  (
                                    `sessionId` int NOT NULL AUTO_INCREMENT,
                                    `lastMessage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                    `createTime` datetime NULL DEFAULT NULL,
                                    PRIMARY KEY (`sessionId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_session
-- ----------------------------
INSERT INTO `message_session` VALUES (1, NULL, '2026-05-01 00:00:00');
INSERT INTO `message_session` VALUES (2, NULL, '2026-06-01 00:00:00');
INSERT INTO `message_session` VALUES (3, NULL, '2025-07-10 23:42:25');

-- ----------------------------
-- Table structure for message_session_user
-- ----------------------------
DROP TABLE IF EXISTS `message_session_user`;
CREATE TABLE `message_session_user`  (
                                         `sessionId` int NULL DEFAULT NULL,
                                         `userId` int NULL DEFAULT NULL,
                                         `isOwner` tinyint NULL DEFAULT 0,
                                         INDEX `idx_userId`(`userId` ASC) USING BTREE,
                                         INDEX `idx_sessionId`(`sessionId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_session_user
-- ----------------------------
INSERT INTO `message_session_user` VALUES (1, 1, 0);
INSERT INTO `message_session_user` VALUES (1, 2, 0);
INSERT INTO `message_session_user` VALUES (2, 1, 0);
INSERT INTO `message_session_user` VALUES (2, 3, 0);
INSERT INTO `message_session_user` VALUES (3, 1, 0);
INSERT INTO `message_session_user` VALUES (3, 4, 0);
INSERT INTO `message_session_user` VALUES (NULL, 3, 1);
INSERT INTO `message_session_user` VALUES (NULL, 3, 5);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `userId` int NOT NULL AUTO_INCREMENT,
                         `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                         `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                         `gender` int NULL DEFAULT NULL,
                         `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
                         PRIMARY KEY (`userId`) USING BTREE,
                         UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '张三', '123', 1, '我是张三');
INSERT INTO `user` VALUES (2, '李四', '123', 1, '我是李四');
INSERT INTO `user` VALUES (3, '王五', '123', 0, '我是王五');
INSERT INTO `user` VALUES (4, '赵六', '123', 0, '我是赵六');
INSERT INTO `user` VALUES (5, '124321', '23232', 1, 'usdgs');

SET FOREIGN_KEY_CHECKS = 1;
