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

-- ----------------------------
-- Records of user
-- ----------------------------
-- INSERT INTO `user` VALUES (1, '张三', '123'), (2, '李四', '123'), (3, '王五', '123'), (4, '赵六', '123');

-- ----------------------------
-- Records of friend
-- ----------------------------
INSERT INTO `friend` (`userId`, `friendId`) VALUES (1, 2), (2, 1), (1, 3), (3, 1), (1, 4), (4, 1);

-- ----------------------------
-- Records of message_session
-- ----------------------------
INSERT INTO `message_session` (`sessionId`, `createTime`) VALUES (1, '2026-05-01 00:00:00'), (2, '2026-06-01 00:00:00');

-- ----------------------------
-- Records of message_session_user
-- ----------------------------
INSERT INTO `message_session_user` (`id`, `sessionId`, `userId`) VALUES (1, 1, 2), (2, 1, 3);

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` (`messageId`, `sessionId`, `fromId`, `content`, `createTime`) VALUES (1, 1, 1, '今晚吃啥', '2026-05-01 17:00:00'), (2, 2, 1, '随便', '2026-05-01 17:01:00'), (3, 1, 1, '那吃面?', '2026-05-01 17:02:00'), (4, 2, 1, '不想吃', '2026-05-01 17:03:00'), (5, 1, 1, '那你想吃啥', '2026-05-01 17:04:00'), (6, 2, 1, '随便', '2026-05-01 17:05:00'), (11, 1, 1, '那吃米饭炒菜?', '2026-05-01 17:06:00'), (8, 2, 1, '不想吃', '2026-05-01 17:07:00'), (9, 1, 1, '那你想吃啥?', '2026-05-01 17:08:00'), (10, 2, 1, '随便', '2026-05-01 17:09:00'), (7, 1, 2, '晚上一起约?', '2026-05-02 12:00:00');


ALTER TABLE message ADD INDEX idx_sessionId (sessionId);
ALTER TABLE message ADD INDEX idx_fromId (fromId);
ALTER TABLE message ADD INDEX idx_createTime (createTime);
ALTER TABLE message_session_user ADD INDEX idx_userId (userId);
ALTER TABLE message_session_user ADD INDEX idx_sessionId (sessionId)